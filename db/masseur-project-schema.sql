-- 技师-项目关联表
-- 用于管理技师和项目（服务）之间的多对多关系

CREATE TABLE IF NOT EXISTS `masseur_project` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `masseur_id` BIGINT NOT NULL COMMENT '技师ID',
  `project_id` BIGINT NOT NULL COMMENT '项目ID（关联product表）',
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_masseur_project` (`masseur_id`, `project_id`) COMMENT '技师和项目的唯一约束',
  KEY `idx_masseur_id` (`masseur_id`) COMMENT '技师ID索引',
  KEY `idx_project_id` (`project_id`) COMMENT '项目ID索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='技师-项目关联表';

-- 插入示例数据（假设已有技师ID: 1,2,3 和项目ID: 1,2,3,4,5）
-- 技师1可做项目1,2,3
INSERT INTO `masseur_project` (`masseur_id`, `project_id`) VALUES
(1, 1),
(1, 2),
(1, 3);

-- 技师2可做项目2,3,4
INSERT INTO `masseur_project` (`masseur_id`, `project_id`) VALUES
(2, 2),
(2, 3),
(2, 4);

-- 技师3可做项目1,3,5
INSERT INTO `masseur_project` (`masseur_id`, `project_id`) VALUES
(3, 1),
(3, 3),
(3, 5);
