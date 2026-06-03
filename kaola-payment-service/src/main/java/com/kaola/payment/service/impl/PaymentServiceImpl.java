package com.kaola.payment.service.impl;

import com.kaola.payment.client.OrderServiceClient;
import com.kaola.payment.config.PaymentConfig;
import com.kaola.payment.mapper.PaymentMapper;
import com.kaola.payment.model.entity.Payment;
import com.kaola.payment.model.enums.PaymentMethod;
import com.kaola.payment.model.enums.PaymentStatus;
import com.kaola.payment.model.vo.PaymentVO;
import com.kaola.payment.model.vo.WxPayParamsVO;
import com.kaola.payment.service.PaymentConfigService;
import com.kaola.payment.service.PaymentService;
import com.wechat.pay.java.core.RSAPublicKeyConfig;
import com.wechat.pay.java.core.notification.NotificationConfig;
import com.wechat.pay.java.core.notification.NotificationParser;
import com.wechat.pay.java.core.notification.RequestParam;
import com.wechat.pay.java.service.payments.jsapi.JsapiServiceExtension;
import com.wechat.pay.java.service.payments.jsapi.model.Amount;
import com.wechat.pay.java.service.payments.jsapi.model.Payer;
import com.wechat.pay.java.service.payments.jsapi.model.PrepayRequest;
import com.wechat.pay.java.service.payments.jsapi.model.PrepayWithRequestPaymentResponse;
import com.wechat.pay.java.service.payments.model.Transaction;
import com.wechat.pay.java.service.refund.RefundService;
import com.wechat.pay.java.service.refund.model.AmountReq;
import com.wechat.pay.java.service.refund.model.CreateRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentMapper paymentMapper;
    private final PaymentConfigService configService;
    private final OrderServiceClient orderServiceClient;

    // WeChat Pay 客户端缓存（配置变更时重建）
    private volatile JsapiServiceExtension cachedJsapi;
    private volatile RSAPublicKeyConfig cachedWxConfig;
    private volatile String cachedConfigHash;

    // ── 创建微信 JSAPI 支付 ────────────────────────────────────────────────

    @Override
    @Transactional
    public PaymentVO createWechatPayment(Long orderId, String orderNo, BigDecimal amount,
                                         Long userId, String openid) {
        log.info("创建微信支付, orderId={}, orderNo={}, amount={}", orderId, orderNo, amount);

        // 复用已有待支付记录
        Payment existing = paymentMapper.findByOrderId(orderId);
        if (existing != null && PaymentStatus.PENDING.getCode().equals(existing.getStatus())) {
            // TEMP（测试支撑，随 /admin/order/update-amount 改金额测试一起下线）：
            // 正常流程不会改待支付订单金额，此分支仅被「改金额(测试)」触发；金额变了就换 out_trade_no 重下
            // （微信不允许同一 out_trade_no 改金额）。注意：未关旧单(无 closeOrder 能力)，仅供受控测试。
            if (existing.getAmount() != null && existing.getAmount().compareTo(amount) != 0) {
                log.info("待支付记录金额变化 {}→{}，换单号重下, id={}", existing.getAmount(), amount, existing.getId());
                existing.setAmount(amount);
                existing.setPaymentNo(generatePaymentNo());
                existing.setPrepayId(null);
                paymentMapper.updateById(existing);
            } else {
                log.info("复用待支付记录 id={}", existing.getId());
            }
            return buildVoWithWxParams(existing, openid);
        }

        Payment payment = new Payment();
        payment.setPaymentNo(generatePaymentNo());
        payment.setOrderId(orderId);
        payment.setOrderNo(orderNo);
        payment.setUserId(userId);
        payment.setOpenid(openid);
        payment.setPayMethod(PaymentMethod.WECHAT.getCode());
        payment.setAmount(amount);
        payment.setStatus(PaymentStatus.PENDING.getCode());
        paymentMapper.insert(payment);

        return buildVoWithWxParams(payment, openid);
    }

    private PaymentVO buildVoWithWxParams(Payment payment, String openid) {
        PaymentConfig.WechatPayConfig cfg = configService.getWechatConfig();
        validateWechatConfig(cfg);

        JsapiServiceExtension jsapi = getOrBuildJsapiService(cfg);

        PrepayRequest req = new PrepayRequest();
        req.setAppid(cfg.getAppId());
        req.setMchid(cfg.getMchId());
        req.setDescription("考拉推拿服务");
        req.setOutTradeNo(payment.getPaymentNo());
        req.setNotifyUrl(cfg.getNotifyUrl());

        Amount wxAmt = new Amount();
        wxAmt.setTotal(payment.getAmount().multiply(BigDecimal.valueOf(100)).intValue());
        wxAmt.setCurrency("CNY");
        req.setAmount(wxAmt);

        Payer payer = new Payer();
        payer.setOpenid(openid);
        req.setPayer(payer);

        PrepayWithRequestPaymentResponse wxResp = jsapi.prepayWithRequestPayment(req);
        log.info("微信预支付成功, paymentNo={}", payment.getPaymentNo());

        // 保存 prepay_id
        payment.setPrepayId(wxResp.getPackageVal().replace("prepay_id=", ""));
        paymentMapper.updateById(payment);

        WxPayParamsVO wxParams = new WxPayParamsVO();
        wxParams.setAppId(wxResp.getAppId());
        wxParams.setTimeStamp(wxResp.getTimeStamp());
        wxParams.setNonceStr(wxResp.getNonceStr());
        wxParams.setPackageValue(wxResp.getPackageVal());
        wxParams.setSignType(wxResp.getSignType());
        wxParams.setPaySign(wxResp.getPaySign());

        PaymentVO vo = new PaymentVO();
        vo.setId(payment.getId());
        vo.setPaymentNo(payment.getPaymentNo());
        vo.setOrderId(payment.getOrderId());
        vo.setOrderNo(payment.getOrderNo());
        vo.setAmount(payment.getAmount());
        vo.setPayMethod(PaymentMethod.WECHAT.getCode());
        vo.setPayMethodText(PaymentMethod.WECHAT.getDescription());
        vo.setStatus(PaymentStatus.PENDING.getCode());
        vo.setStatusText(PaymentStatus.PENDING.getDescription());
        vo.setWxPayParams(wxParams);
        return vo;
    }

    // ── 微信支付 V3 回调处理 ───────────────────────────────────────────────

    @Override
    @Transactional
    public String handleWechatNotify(String timestamp, String nonce, String signature,
                                      String serial, String body) {
        log.info("接收微信支付回调, serial={}", serial);
        try {
            PaymentConfig.WechatPayConfig cfg = configService.getWechatConfig();
            validateWechatConfig(cfg);

            RSAPublicKeyConfig wxConfig = getOrBuildWxConfig(cfg);
            NotificationParser parser = new NotificationParser((NotificationConfig) wxConfig);

            RequestParam requestParam = new RequestParam.Builder()
                    .serialNumber(serial)
                    .nonce(nonce)
                    .signature(signature)
                    .timestamp(timestamp)
                    .body(body)
                    .build();

            Transaction tx = parser.parse(requestParam, Transaction.class);
            log.info("回调解析成功, outTradeNo={}, tradeState={}", tx.getOutTradeNo(), tx.getTradeState());

            if (Transaction.TradeStateEnum.SUCCESS == tx.getTradeState()) {
                updatePaymentSuccess(tx.getOutTradeNo(), tx.getTransactionId());
            }
            return null; // 处理成功
        } catch (Exception e) {
            log.error("处理微信回调异常", e);
            return e.getMessage();
        }
    }

    private void updatePaymentSuccess(String paymentNo, String transactionId) {
        Payment payment = paymentMapper.findByPaymentNo(paymentNo);
        if (payment == null) {
            log.error("回调找不到支付记录: paymentNo={}", paymentNo);
            return;
        }
        if (PaymentStatus.SUCCESS.getCode().equals(payment.getStatus())) {
            log.info("重复回调，忽略: paymentNo={}", paymentNo);
            return;
        }
        payment.setStatus(PaymentStatus.SUCCESS.getCode());
        payment.setTransactionId(transactionId);
        payment.setPayTime(LocalDateTime.now());
        paymentMapper.updateById(payment);

        try {
            orderServiceClient.notifyOrderPaid(payment.getOrderNo(), transactionId);
            log.info("已通知 order-service 支付成功, orderNo={}", payment.getOrderNo());
        } catch (Exception e) {
            log.error("通知 order-service 失败，需人工处理, orderNo={}", payment.getOrderNo(), e);
        }
    }

    // ── 退款 ─────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public boolean refund(Long orderId, BigDecimal amount) {
        log.info("申请退款, orderId={}, amount={}", orderId, amount);

        Payment payment = paymentMapper.findByOrderId(orderId);
        if (payment == null) {
            throw new RuntimeException("未找到支付记录: orderId=" + orderId);
        }
        // 幂等：已退款直接返回成功，防重复退
        if (PaymentStatus.REFUNDED.getCode().equals(payment.getStatus())) {
            log.info("订单已退款，忽略重复退款, orderId={}", orderId);
            return true;
        }
        if (!PaymentStatus.SUCCESS.getCode().equals(payment.getStatus())) {
            throw new RuntimeException("当前支付状态不可退款: status=" + payment.getStatus());
        }

        PaymentConfig.WechatPayConfig cfg = configService.getWechatConfig();
        validateWechatConfig(cfg);

        RefundService refundService = new RefundService.Builder()
                .config(getOrBuildWxConfig(cfg)).build();

        CreateRequest refundReq = new CreateRequest();
        refundReq.setOutTradeNo(payment.getPaymentNo());
        refundReq.setOutRefundNo("RF" + System.currentTimeMillis());
        refundReq.setReason("管理员申请退款");

        AmountReq amtReq = new AmountReq();
        amtReq.setRefund(amount.multiply(BigDecimal.valueOf(100)).longValue());
        amtReq.setTotal(payment.getAmount().multiply(BigDecimal.valueOf(100)).longValue());
        amtReq.setCurrency("CNY");
        refundReq.setAmount(amtReq);

        refundService.create(refundReq);
        log.info("退款申请成功, paymentNo={}", payment.getPaymentNo());

        payment.setStatus(PaymentStatus.REFUNDED.getCode());
        paymentMapper.updateById(payment);
        return true;
    }

    // ── WeChat Pay 客户端（懒加载 + 配置变更重建） ────────────────────────

    private synchronized JsapiServiceExtension getOrBuildJsapiService(PaymentConfig.WechatPayConfig cfg) {
        String hash = configHash(cfg);
        if (cachedJsapi == null || !hash.equals(cachedConfigHash)) {
            log.info("重建微信支付客户端");
            cachedWxConfig = buildRsaConfig(cfg);
            cachedJsapi = new JsapiServiceExtension.Builder().config(cachedWxConfig).build();
            cachedConfigHash = hash;
        }
        return cachedJsapi;
    }

    private synchronized RSAPublicKeyConfig getOrBuildWxConfig(PaymentConfig.WechatPayConfig cfg) {
        String hash = configHash(cfg);
        if (cachedWxConfig == null || !hash.equals(cachedConfigHash)) {
            cachedWxConfig = buildRsaConfig(cfg);
            cachedConfigHash = hash;
        }
        return cachedWxConfig;
    }

    private RSAPublicKeyConfig buildRsaConfig(PaymentConfig.WechatPayConfig cfg) {
        RSAPublicKeyConfig.Builder builder = new RSAPublicKeyConfig.Builder()
                .merchantId(cfg.getMchId())
                .merchantSerialNumber(cfg.getMchSerialNo())
                .publicKey(cfg.getPublicKey())
                .publicKeyId(cfg.getPublicKeyId())
                // 回调/响应密文走 AEAD 解密，必需 APIv3Key（公钥模式同样需要）；缺它会报 SecretKeySpec "Missing argument"
                .apiV3Key(cfg.getApiV3Key());

        if (StringUtils.hasText(cfg.getPrivateKeyPath())) {
            builder.privateKeyFromPath(cfg.getPrivateKeyPath());
        } else {
            builder.privateKey(cfg.getPrivateKey());
        }
        return builder.build();
    }

    private String configHash(PaymentConfig.WechatPayConfig cfg) {
        return String.valueOf((cfg.getMchId() + cfg.getMchSerialNo()
                + cfg.getPublicKeyId() + cfg.getPublicKey()).hashCode());
    }

    private void validateWechatConfig(PaymentConfig.WechatPayConfig cfg) {
        if (!StringUtils.hasText(cfg.getMchId())
                || !StringUtils.hasText(cfg.getMchSerialNo())
                || (!StringUtils.hasText(cfg.getPrivateKey()) && !StringUtils.hasText(cfg.getPrivateKeyPath()))
                || !StringUtils.hasText(cfg.getPublicKey())
                || !StringUtils.hasText(cfg.getPublicKeyId())
                || !StringUtils.hasText(cfg.getApiV3Key())) {  // 回调/响应密文解密必需
            throw new RuntimeException("微信支付配置不完整，请在后台 [系统配置→支付设置] 填写完整参数（含微信支付公钥、APIv3密钥）");
        }
    }

    private String generatePaymentNo() {
        return "KP" + System.currentTimeMillis()
                + UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase();
    }
}
