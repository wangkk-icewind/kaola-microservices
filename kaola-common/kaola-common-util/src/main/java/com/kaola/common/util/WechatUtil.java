package com.kaola.common.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * 微信工具类
 *
 * @author Kaola Team
 */
@Slf4j
@Component
public class WechatUtil {

    @Value("${wechat.miniapp.appid:}")
    private String appId;

    @Value("${wechat.miniapp.secret:}")
    private String appSecret;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 通过code获取session信息
     */
    public Map<String, String> code2Session(String code) {
        String url = String.format(
                "https://api.weixin.qq.com/sns/jscode2session?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code",
                appId, appSecret, code
        );

        try {
            String response = restTemplate.getForObject(url, String.class);
            JsonNode jsonNode = objectMapper.readTree(response);

            Map<String, String> result = new HashMap<>();
            if (jsonNode.has("openid")) {
                result.put("openid", jsonNode.get("openid").asText());
            }
            if (jsonNode.has("session_key")) {
                result.put("session_key", jsonNode.get("session_key").asText());
            }
            if (jsonNode.has("unionid")) {
                result.put("unionid", jsonNode.get("unionid").asText());
            }
            if (jsonNode.has("errcode")) {
                result.put("errcode", jsonNode.get("errcode").asText());
                result.put("errmsg", jsonNode.get("errmsg").asText());
            }

            return result;
        } catch (Exception e) {
            log.error("微信code2Session失败", e);
            return null;
        }
    }

    /**
     * 解密手机号
     */
    public String decryptPhoneNumber(String sessionKey, String encryptedData, String iv) {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(sessionKey);
            byte[] ivBytes = Base64.getDecoder().decode(iv);
            byte[] encryptedBytes = Base64.getDecoder().decode(encryptedData);

            SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);

            byte[] decrypted = cipher.doFinal(encryptedBytes);
            String result = new String(decrypted, StandardCharsets.UTF_8);

            JsonNode jsonNode = objectMapper.readTree(result);
            if (jsonNode.has("phoneNumber")) {
                return jsonNode.get("phoneNumber").asText();
            }
            if (jsonNode.has("purePhoneNumber")) {
                return jsonNode.get("purePhoneNumber").asText();
            }

            return null;
        } catch (Exception e) {
            log.error("解密手机号失败", e);
            return null;
        }
    }

    /**
     * 获取access_token
     */
    public String getAccessToken() {
        String url = String.format(
                "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=%s&secret=%s",
                appId, appSecret
        );

        try {
            String response = restTemplate.getForObject(url, String.class);
            JsonNode jsonNode = objectMapper.readTree(response);

            if (jsonNode.has("access_token")) {
                return jsonNode.get("access_token").asText();
            }

            log.error("获取access_token失败: {}", response);
            return null;
        } catch (Exception e) {
            log.error("获取access_token异常", e);
            return null;
        }
    }

    /**
     * SHA1加密
     */
    public String sha1(String data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            byte[] bytes = digest.digest(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            log.error("SHA1加密失败", e);
            return null;
        }
    }

    /**
     * 验证签名
     */
    public boolean verifySignature(String signature, String rawData, String sessionKey) {
        String calculatedSignature = sha1(rawData + sessionKey);
        return signature.equals(calculatedSignature);
    }
}
