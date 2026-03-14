-- 商品表 (电子礼卡/项目礼卡/实物商品)
CREATE TABLE IF NOT EXISTS `t_product` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `type` VARCHAR(50) NOT NULL COMMENT '商品类型 (ELECTRONIC_CARD-电子礼卡, PROJECT_CARD-项目礼卡, PHYSICAL_PRODUCT-实物商品)',
  `name` VARCHAR(200) NOT NULL COMMENT '商品名称',
  `price` DECIMAL(10, 2) NOT NULL COMMENT '商品价格',
  `original_price` DECIMAL(10, 2) NULL COMMENT '原价 (用于显示优惠)',
  `face_value` DECIMAL(10, 2) NULL COMMENT '面额 (仅礼卡类型使用)',
  `valid_days` INT NULL DEFAULT -1 COMMENT '有效天数 (仅礼卡类型使用，-1表示永久有效)',
  `stock` INT NOT NULL DEFAULT 0 COMMENT '库存数量',
  `sales_count` INT NOT NULL DEFAULT 0 COMMENT '销量',
  `description` TEXT NULL COMMENT '商品描述',
  `specifications` TEXT NULL COMMENT '商品规格说明',
  `images` JSON NULL COMMENT '商品图片 (JSON数组格式，存储图片URL)',
  `carousel_images` JSON NULL COMMENT '商品轮播图 (JSON数组格式，存储图片URL)',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态 (0-下架 1-上架)',
  `sort_order` INT NOT NULL DEFAULT 0 COMMENT '排序值 (数字越小越靠前)',
  `is_recommended` TINYINT NOT NULL DEFAULT 0 COMMENT '是否推荐 (0-否 1-是)',
  `related_project_ids` JSON NULL COMMENT '关联的项目ID (仅项目礼卡使用，JSON数组格式)',
  `created_by` VARCHAR(100) NULL COMMENT '创建人',
  `updated_by` VARCHAR(100) NULL COMMENT '更新人',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标志 (0-未删除 1-已删除)',
  PRIMARY KEY (`id`),
  INDEX `idx_type` (`type`),
  INDEX `idx_status` (`status`),
  INDEX `idx_sort_order` (`sort_order`),
  INDEX `idx_is_recommended` (`is_recommended`),
  INDEX `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品表';

-- 插入示例数据 (电子礼卡)
INSERT INTO `t_product` (`type`, `name`, `price`, `original_price`, `face_value`, `valid_days`, `stock`, `sales_count`, `description`, `specifications`, `images`, `carousel_images`, `status`, `sort_order`, `is_recommended`)
VALUES
('ELECTRONIC_CARD', '常乐按摩100元电子礼卡', 95.00, 100.00, 100.00, 365, 1000, 0, '常乐按摩100元电子礼卡，可用于预约任意按摩服务，有效期365天', '面额：100元\n有效期：365天\n使用范围：全部门店\n可否转赠：是', '["https://example.com/gift-card-100.jpg"]', '["https://example.com/gift-card-100-banner.jpg"]', 1, 1, 1),
('ELECTRONIC_CARD', '常乐按摩200元电子礼卡', 190.00, 200.00, 200.00, 365, 1000, 0, '常乐按摩200元电子礼卡，可用于预约任意按摩服务，有效期365天', '面额：200元\n有效期：365天\n使用范围：全部门店\n可否转赠：是', '["https://example.com/gift-card-200.jpg"]', '["https://example.com/gift-card-200-banner.jpg"]', 1, 2, 1),
('ELECTRONIC_CARD', '常乐按摩500元电子礼卡', 475.00, 500.00, 500.00, 365, 1000, 0, '常乐按摩500元电子礼卡，可用于预约任意按摩服务，有效期365天', '面额：500元\n有效期：365天\n使用范围：全部门店\n可否转赠：是', '["https://example.com/gift-card-500.jpg"]', '["https://example.com/gift-card-500-banner.jpg"]', 1, 3, 1);

-- 插入示例数据 (项目礼卡)
INSERT INTO `t_product` (`type`, `name`, `price`, `original_price`, `face_value`, `valid_days`, `stock`, `sales_count`, `description`, `specifications`, `images`, `carousel_images`, `status`, `sort_order`, `is_recommended`, `related_project_ids`)
VALUES
('PROJECT_CARD', '全身按摩3次卡', 285.00, 300.00, 300.00, 90, 500, 0, '全身按摩3次卡，可预约全身按摩服务3次，有效期90天', '次数：3次\n有效期：90天\n使用范围：全身按摩项目\n可否转赠：否', '["https://example.com/project-card-massage-3.jpg"]', '["https://example.com/project-card-massage-3-banner.jpg"]', 1, 4, 1, '[1, 2, 3]');

-- 插入示例数据 (实物商品)
INSERT INTO `t_product` (`type`, `name`, `price`, `original_price`, `stock`, `sales_count`, `description`, `specifications`, `images`, `carousel_images`, `status`, `sort_order`, `is_recommended`)
VALUES
('PHYSICAL_PRODUCT', '驱寒理疗包', 99.00, 128.00, 200, 0, '驱寒理疗包，含艾草包、艾灸贴等理疗用品，帮助驱寒暖身', '包含：艾草包x2、艾灸贴x10\n尺寸：20cm x 15cm\n重量：500g\n产地：中国', '["https://example.com/product-therapy-pack.jpg"]', '["https://example.com/product-therapy-pack-banner.jpg"]', 1, 5, 1),
('PHYSICAL_PRODUCT', '按摩精油套装', 158.00, 198.00, 150, 0, '按摩精油套装，包含薰衣草、玫瑰、茶树等多种精油', '包含：精油5瓶 x 10ml\n成分：天然植物萃取\n功效：舒缓放松、改善睡眠\n保质期：2年', '["https://example.com/product-oil-set.jpg"]', '["https://example.com/product-oil-set-banner.jpg"]', 1, 6, 1);
