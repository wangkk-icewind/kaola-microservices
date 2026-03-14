-- 订单系统扩展支持商品订单
-- 执行日期: 2025-12-17

-- 1. 修改 t_order 表，添加商品订单支持字段
ALTER TABLE `t_order`
ADD COLUMN `order_type` VARCHAR(20) NOT NULL DEFAULT 'SERVICE' COMMENT '订单类型 (SERVICE-服务订单 PRODUCT-商品订单)' AFTER `remark`,
ADD COLUMN `receiver_name` VARCHAR(50) NULL COMMENT '收货人姓名 (商品订单使用)' AFTER `order_type`,
ADD COLUMN `receiver_phone` VARCHAR(20) NULL COMMENT '收货人电话 (商品订单使用)' AFTER `receiver_name`,
ADD COLUMN `receiver_address` VARCHAR(500) NULL COMMENT '收货地址 (商品订单使用)' AFTER `receiver_phone`,
ADD COLUMN `tracking_no` VARCHAR(100) NULL COMMENT '物流单号 (商品订单使用)' AFTER `receiver_address`,
ADD INDEX `idx_order_type` (`order_type`);

-- 2. 修改 t_order_item 表，添加商品项支持字段
ALTER TABLE `t_order_item`
ADD COLUMN `item_type` VARCHAR(20) NOT NULL DEFAULT 'SERVICE' COMMENT '订单项类型 (SERVICE-服务项 PRODUCT-商品项)' AFTER `order_id`,
ADD COLUMN `product_id` BIGINT NULL COMMENT '商品ID (商品订单使用)' AFTER `project_id`,
ADD COLUMN `product_name` VARCHAR(200) NULL COMMENT '商品名称 (冗余存储)' AFTER `product_id`,
ADD COLUMN `quantity` INT NULL DEFAULT 1 COMMENT '数量 (商品订单使用)' AFTER `product_name`,
MODIFY COLUMN `masseur_id` BIGINT NULL COMMENT '技师ID (服务订单使用)',
MODIFY COLUMN `project_id` BIGINT NULL COMMENT '项目ID (服务订单使用)',
MODIFY COLUMN `duration` INT NULL COMMENT '服务时长 (分钟)',
MODIFY COLUMN `extra_duration` INT NULL COMMENT '加钟时长 (分钟)',
MODIFY COLUMN `extra_price` DECIMAL(10,2) NULL COMMENT '加钟费用',
MODIFY COLUMN `start_time` DATETIME NULL COMMENT '服务开始时间',
MODIFY COLUMN `end_time` DATETIME NULL COMMENT '服务结束时间',
ADD INDEX `idx_item_type` (`item_type`),
ADD INDEX `idx_product_id` (`product_id`);

-- 3. 更新现有订单数据，设置订单类型为 SERVICE
UPDATE `t_order` SET `order_type` = 'SERVICE' WHERE `order_type` IS NULL OR `order_type` = '';

-- 4. 更新现有订单项数据，设置订单项类型为 SERVICE
UPDATE `t_order_item` SET `item_type` = 'SERVICE' WHERE `item_type` IS NULL OR `item_type` = '';
