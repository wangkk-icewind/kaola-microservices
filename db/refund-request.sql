-- 退款申请/审核记录（用户申请 → 后台审核 → 真退微信）
CREATE TABLE IF NOT EXISTS `t_refund` (
    `id`             BIGINT       NOT NULL AUTO_INCREMENT,
    `order_id`       BIGINT       NOT NULL COMMENT '订单ID',
    `order_no`       VARCHAR(32)  NOT NULL COMMENT '订单号',
    `user_id`        BIGINT       NOT NULL COMMENT '申请用户ID',
    `amount`         DECIMAL(10,2) NOT NULL COMMENT '退款金额(元)',
    `reason`         VARCHAR(255) DEFAULT NULL COMMENT '退款原因',
    `status`         TINYINT      NOT NULL DEFAULT 0 COMMENT '0-申请中(待审核) 1-已同意已退款 2-已拒绝',
    `audit_remark`   VARCHAR(255) DEFAULT NULL COMMENT '审核备注/拒绝原因',
    `transaction_id` VARCHAR(64)  DEFAULT NULL COMMENT '微信退款关联交易号',
    `audit_time`     DATETIME     DEFAULT NULL COMMENT '审核时间',
    `create_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted`        TINYINT      NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`),
    KEY `idx_order_id` (`order_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='退款申请/审核记录';
