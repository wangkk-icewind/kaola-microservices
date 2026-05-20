-- 订单退款状态扩展：新增 status=6（已退款）+ commission_override 提成裁量字段
-- 执行日期: 2026-05-20
-- 背景：已完成(4)或已评价(5)状态的退款订单，服务已发生，需与"已取消(0)"区分；
--       commission_override 用于管理员手动决定退款订单是否仍发放师傅提成

-- 在 t_order 表添加提成覆盖字段
ALTER TABLE `t_order`
ADD COLUMN `commission_override` TINYINT DEFAULT NULL
COMMENT '提成覆盖: NULL=按默认规则, 1=强制发放, 0=强制不发（仅对 status=6 已退款订单有效）'
AFTER `remark`;

-- status 字段说明（无需 DDL 变更，业务层约束）：
-- 0=已取消（服务未发生，不发提成）
-- 1=待支付  2=已支付  3=服务中  4=已完成  5=已评价
-- 6=已退款（服务已发生，提成按 commission_override 决定，默认=1 发放）
