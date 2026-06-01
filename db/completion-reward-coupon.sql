-- 完成奖励券（B5）：订单完成后自动发放的优惠券标记
-- is_completion_reward: 0-否 1-是（启用后用户每完成一笔订单自动发放一张，每人限领一次）
ALTER TABLE t_coupon
    ADD COLUMN is_completion_reward TINYINT NOT NULL DEFAULT 0
    COMMENT '完成奖励券:0-否 1-是（订单完成自动发放，每人限领一次）'
    AFTER customer_type;
