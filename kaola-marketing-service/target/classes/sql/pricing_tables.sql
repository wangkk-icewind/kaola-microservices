-- 定价配置表创建脚本
-- 用于kaola-marketing-service微服务

-- 1. 技师等级定价表
CREATE TABLE IF NOT EXISTS `masseur_level_pricing` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `level` INT(11) NOT NULL COMMENT '技师等级 (1-初级 2-中级 3-高级 4-特级)',
  `level_name` VARCHAR(50) NOT NULL COMMENT '等级名称',
  `multiplier` DECIMAL(10, 2) NOT NULL COMMENT '价格倍率',
  `status` TINYINT(4) NOT NULL DEFAULT 1 COMMENT '状态 (1-启用 0-禁用)',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_level` (`level`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='技师等级定价表';

-- 2. 时段定价表
CREATE TABLE IF NOT EXISTS `time_slot_pricing` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `slot_type` INT(11) NOT NULL COMMENT '时段类型 (1-正常时段 2-高峰时段 3-深夜时段)',
  `slot_name` VARCHAR(50) NOT NULL COMMENT '时段名称',
  `start_time` VARCHAR(10) NOT NULL COMMENT '开始时间 (HH:mm格式)',
  `end_time` VARCHAR(10) NOT NULL COMMENT '结束时间 (HH:mm格式)',
  `multiplier` DECIMAL(10, 2) NOT NULL COMMENT '价格倍率',
  `status` TINYINT(4) NOT NULL DEFAULT 1 COMMENT '状态 (1-启用 0-禁用)',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_slot_type` (`slot_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='时段定价表';

-- 3. 门店定价表
CREATE TABLE IF NOT EXISTS `store_pricing` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `store_id` BIGINT(20) NOT NULL COMMENT '门店ID',
  `store_name` VARCHAR(100) NOT NULL COMMENT '门店名称',
  `multiplier` DECIMAL(10, 2) NOT NULL COMMENT '价格倍率',
  `status` TINYINT(4) NOT NULL DEFAULT 1 COMMENT '状态 (1-启用 0-禁用)',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_store_id` (`store_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='门店定价表';

-- 插入默认数据

-- 技师等级定价默认数据
INSERT INTO `masseur_level_pricing` (`level`, `level_name`, `multiplier`, `status`) VALUES
(1, '初级技师', 0.90, 1),
(2, '中级技师', 1.00, 1),
(3, '高级技师', 1.20, 1),
(4, '特级技师', 1.50, 1)
ON DUPLICATE KEY UPDATE `level_name` = VALUES(`level_name`), `multiplier` = VALUES(`multiplier`);

-- 时段定价默认数据
INSERT INTO `time_slot_pricing` (`slot_type`, `slot_name`, `start_time`, `end_time`, `multiplier`, `status`) VALUES
(1, '正常时段', '09:00', '21:00', 1.00, 1),
(2, '高峰时段', '18:00', '21:00', 1.20, 1),
(3, '深夜时段', '21:00', '02:00', 1.30, 1)
ON DUPLICATE KEY UPDATE `slot_name` = VALUES(`slot_name`), `start_time` = VALUES(`start_time`), `end_time` = VALUES(`end_time`), `multiplier` = VALUES(`multiplier`);

-- 门店定价默认数据（示例）
INSERT INTO `store_pricing` (`store_id`, `store_name`, `multiplier`, `status`) VALUES
(1, '深圳南山店', 1.00, 1),
(2, '深圳福田店', 1.10, 1),
(3, '深圳罗湖店', 0.95, 1)
ON DUPLICATE KEY UPDATE `store_name` = VALUES(`store_name`), `multiplier` = VALUES(`multiplier`);
