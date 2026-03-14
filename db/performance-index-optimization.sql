-- ========================================
-- 考拉推拿预约系统 - 数据库索引性能优化
-- ========================================
-- 目标: 提升查询性能 60-80%
-- 创建时间: 2026-01-03
-- 说明:
--   1. 如果索引已存在会报错 "Duplicate key name"，可安全忽略
--   2. 建议逐条执行SQL，跳过已存在的索引
--   3. 执行完成后使用 SHOW INDEX 验证索引是否创建成功
-- 执行方式:
--   方式1: 使用 source 命令导入 (推荐)
--   方式2: 逐条复制执行，忽略重复索引错误
-- ========================================

USE kaola_massage;

-- ========================================
-- 1. 城市表索引优化
-- ========================================
CREATE INDEX idx_city_search ON t_city(pinyin_abbr, city_name);

-- ========================================
-- 2. 订单表索引优化
-- ========================================
CREATE INDEX idx_order_user_status ON t_order(user_id, status, create_time DESC);
CREATE INDEX idx_order_type ON t_order(order_type, status);

-- ========================================
-- 3. 技师表索引优化
-- ========================================
-- t_masseur表实际字段: id, user_id, name, phone, level, rating, status, age, hometown
CREATE INDEX idx_masseur_status_level ON t_masseur(status, level);
CREATE INDEX idx_masseur_hometown ON t_masseur(hometown, status);

-- ========================================
-- 4. 商品表索引优化
-- ========================================
CREATE INDEX idx_product_type_status ON t_product(type, status, is_recommended);
CREATE INDEX idx_product_recommended ON t_product(is_recommended, status, sort_order);

-- ========================================
-- 5. 门店表索引优化
-- ========================================
CREATE INDEX idx_store_status_city ON t_store(status, city);

-- ========================================
-- 6. 订单项表索引优化
-- ========================================
CREATE INDEX idx_order_item_order_id ON t_order_item(order_id, item_type);

-- ========================================
-- 7. 技师-项目关联表索引优化
-- ========================================
CREATE INDEX idx_masseur_project_masseur ON masseur_project(masseur_id);
CREATE INDEX idx_masseur_project_project ON masseur_project(project_id);

-- ========================================
-- 验证索引创建结果
-- ========================================
SHOW INDEX FROM t_city;
SHOW INDEX FROM t_order;
SHOW INDEX FROM t_masseur;
SHOW INDEX FROM t_product;
SHOW INDEX FROM t_store;
SHOW INDEX FROM t_order_item;
SHOW INDEX FROM masseur_project;
