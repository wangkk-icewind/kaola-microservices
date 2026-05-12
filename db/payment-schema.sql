-- 支付记录表
CREATE TABLE IF NOT EXISTS `t_payment` (
    `id`             BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `payment_no`     VARCHAR(40)  NOT NULL                COMMENT '内部支付单号（KP+时间戳+随机）',
    `order_id`       BIGINT       NOT NULL                COMMENT '订单 ID',
    `order_no`       VARCHAR(40)  NOT NULL                COMMENT '订单编号（冗余）',
    `user_id`        BIGINT       NOT NULL                COMMENT '用户 ID',
    `openid`         VARCHAR(64)          DEFAULT NULL    COMMENT '用户微信 openid（JSAPI 必需）',
    `pay_method`     TINYINT      NOT NULL DEFAULT 1      COMMENT '支付方式 1-微信 2-支付宝 3-余额',
    `prepay_id`      VARCHAR(128)         DEFAULT NULL    COMMENT '微信预支付 prepay_id',
    `transaction_id` VARCHAR(64)          DEFAULT NULL    COMMENT '微信交易流水号',
    `amount`         DECIMAL(10,2) NOT NULL               COMMENT '支付金额（元）',
    `status`         TINYINT      NOT NULL DEFAULT 0      COMMENT '0-待支付 1-成功 2-失败 3-已退款 4-已关闭',
    `pay_time`       DATETIME             DEFAULT NULL    COMMENT '实际支付时间',
    `create_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted`        TINYINT      NOT NULL DEFAULT 0      COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_payment_no` (`payment_no`),
    KEY `idx_order_id`       (`order_id`),
    KEY `idx_order_no`       (`order_no`),
    KEY `idx_user_id`        (`user_id`),
    KEY `idx_transaction_id` (`transaction_id`),
    KEY `idx_status`         (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='支付记录';

-- 系统配置表（若 admin-service 的 kaola_admin 库中尚未建立）
CREATE TABLE IF NOT EXISTS `t_system_setting` (
    `id`            BIGINT       NOT NULL AUTO_INCREMENT,
    `setting_key`   VARCHAR(100) NOT NULL COMMENT '配置键',
    `setting_value` TEXT                  COMMENT '配置值',
    `setting_group` VARCHAR(50)  NOT NULL DEFAULT 'general' COMMENT '分组（payment/general/...）',
    `description`   VARCHAR(200)          COMMENT '说明',
    `create_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_setting_key` (`setting_key`),
    KEY `idx_group` (`setting_group`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统配置';

-- 预置支付配置占位（实际值由后台填写）
INSERT IGNORE INTO `t_system_setting` (`setting_key`, `setting_value`, `setting_group`, `description`) VALUES
('wechat.appId',        '',  'payment', '小程序 AppID'),
('wechat.mchId',        '',  'payment', '微信支付商户号'),
('wechat.mchSerialNo',  '',  'payment', '商户 API 证书序列号'),
('wechat.apiV3Key',     '',  'payment', 'APIv3 密钥（32字节）'),
('wechat.privateKey',   '',  'payment', '商户 API 私钥（PEM 内容）'),
('wechat.privateKeyPath', '', 'payment', '商户 API 私钥文件路径（与 privateKey 二选一）'),
('wechat.notifyUrl',    'https://kaolalaile.com/api/payment/wechat/notify', 'payment', '支付回调 URL');
