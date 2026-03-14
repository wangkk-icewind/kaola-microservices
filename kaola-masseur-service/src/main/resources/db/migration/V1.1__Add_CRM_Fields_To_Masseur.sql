-- 为技师表添加CRM相关字段，用于人才储备和BD拓展

-- 添加年龄字段
ALTER TABLE t_masseur ADD COLUMN age INT DEFAULT NULL COMMENT '年龄';

-- 添加籍贯字段
ALTER TABLE t_masseur ADD COLUMN hometown VARCHAR(100) DEFAULT NULL COMMENT '籍贯（省市区）';

-- 添加期望薪资范围字段
ALTER TABLE t_masseur ADD COLUMN salary_range VARCHAR(50) DEFAULT NULL COMMENT '期望薪资范围（如：8000-12000）';

-- 添加意向信息字段（JSON格式）
ALTER TABLE t_masseur ADD COLUMN intention TEXT DEFAULT NULL COMMENT '意向信息（JSON格式，包含意向城市、意向岗位、可入职时间等）';

-- 添加索引以优化查询性能
CREATE INDEX idx_hometown ON t_masseur(hometown);
CREATE INDEX idx_age ON t_masseur(age);
