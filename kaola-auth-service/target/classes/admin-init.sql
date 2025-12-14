-- 管理后台初始化数据
-- 用于 admin-web 运营管理系统

-- =============================================
-- 创建管理员角色表
-- =============================================
CREATE TABLE IF NOT EXISTS t_admin_role (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL COMMENT '角色名称',
    description VARCHAR(200) COMMENT '角色描述',
    permissions TEXT COMMENT '权限列表(JSON数组)',
    status TINYINT DEFAULT 1 COMMENT '状态 0-禁用 1-启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除标志'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='管理员角色表';

-- =============================================
-- 创建管理员用户表
-- =============================================
CREATE TABLE IF NOT EXISTS t_admin_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码(BCrypt加密)',
    nickname VARCHAR(50) COMMENT '昵称',
    avatar VARCHAR(255) COMMENT '头像',
    phone VARCHAR(20) COMMENT '手机号',
    email VARCHAR(100) COMMENT '邮箱',
    role_id BIGINT COMMENT '角色ID',
    store_id BIGINT COMMENT '关联门店ID(门店管理员专用，NULL表示可管理所有门店)',
    status TINYINT DEFAULT 1 COMMENT '状态 0-禁用 1-启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除标志'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='管理员用户表';

-- 如果表已存在，添加 store_id 字段（忽略已存在的错误）
-- 注意：如果 store_id 字段已存在，此语句会报错，可以忽略
-- ALTER TABLE t_admin_user ADD COLUMN store_id BIGINT COMMENT '关联门店ID(门店管理员专用，NULL表示可管理所有门店)' AFTER role_id;

-- =============================================
-- 角色权限说明
-- =============================================
-- dashboard  - 仪表盘(数据概览)
-- store      - 门店管理(门店信息的增删改查)
-- store:own  - 本门店管理(仅管理自己关联的门店)
-- masseur    - 技师管理(技师信息的增删改查)
-- masseur:own - 本门店技师管理(仅管理自己门店的技师)
-- project    - 项目管理(服务项目的增删改查)
-- schedule   - 排班管理(排班相关)
-- schedule:own - 本门店排班管理
-- order      - 订单管理(所有订单)
-- order:own  - 本门店订单管理
-- promotion  - 促销管理(活动、优惠券)
-- review     - 评价管理(所有评价)
-- review:own - 本门店评价管理
-- complaint  - 投诉管理
-- finance    - 财务管理(收益、提现)
-- system     - 系统设置(用户、角色、配置)

-- =============================================
-- 初始化角色数据
-- =============================================
INSERT INTO t_admin_role (id, name, description, permissions, status, create_time, update_time, deleted) VALUES
(1, '超级管理员', '拥有所有权限，可管理整个平台', '["dashboard","store","masseur","project","schedule","order","promotion","review","complaint","finance","system"]', 1, NOW(), NOW(), 0),
(2, '运营管理员', '负责平台日常运营，可管理所有门店', '["dashboard","store","masseur","project","schedule","order","promotion","review","complaint"]', 1, NOW(), NOW(), 0),
(3, '财务管理员', '负责财务相关，查看收益和审批提现', '["dashboard","finance"]', 1, NOW(), NOW(), 0),
(4, '门店管理员', '管理本门店的技师、排班、订单、评价', '["dashboard","store:own","masseur:own","schedule:own","order:own","review:own"]', 1, NOW(), NOW(), 0)
ON DUPLICATE KEY UPDATE name=VALUES(name), description=VALUES(description), permissions=VALUES(permissions);

-- =============================================
-- 初始化管理员用户
-- 默认密码: admin123 (BCrypt加密后)
-- =============================================
INSERT INTO t_admin_user (id, username, password, nickname, avatar, phone, email, role_id, store_id, status, create_time, update_time, deleted) VALUES
(1, 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '超级管理员', '/images/avatars/admin.png', '13800000000', 'admin@kaola.com', 1, NULL, 1, NOW(), NOW(), 0),
(2, 'store_guomao', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '国贸店店长', '/images/avatars/store1.png', '13800000001', 'guomao@kaola.com', 4, 1, 1, NOW(), NOW(), 0),
(3, 'store_sanlitun', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '三里屯店店长', '/images/avatars/store2.png', '13800000002', 'sanlitun@kaola.com', 4, 2, 1, NOW(), NOW(), 0),
(4, 'store_wangjing', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '望京店店长', '/images/avatars/store3.png', '13800000003', 'wangjing@kaola.com', 4, 3, 1, NOW(), NOW(), 0)
ON DUPLICATE KEY UPDATE username=VALUES(username);
