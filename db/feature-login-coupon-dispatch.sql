-- 系统设置表
CREATE TABLE IF NOT EXISTS t_system_setting (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  setting_key VARCHAR(100) NOT NULL UNIQUE COMMENT '配置键',
  setting_value TEXT COMMENT '配置值',
  setting_group VARCHAR(50) DEFAULT 'general' COMMENT '分组',
  description VARCHAR(200) COMMENT '描述',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) COMMENT='系统配置表';

INSERT IGNORE INTO t_system_setting (setting_key, setting_value, setting_group, description) VALUES
('wx_app_id', '', 'wechat', '微信小程序AppID'),
('wx_app_secret', '', 'wechat', '微信小程序AppSecret'),
('wx_login_mode', 'mock', 'wechat', '登录模式: mock=Mock登录, real=真实微信登录'),
('sms_provider', '', 'sms', '短信服务商: aliyun/tencent'),
('sms_access_key', '', 'sms', '短信AccessKey'),
('sms_secret', '', 'sms', '短信Secret'),
('sms_sign_name', '考拉推拿', 'sms', '短信签名'),
('sms_template_coupon', '', 'sms', '优惠券发放短信模板ID'),
('new_customer_max_orders', '0', 'coupon', '新客判断最大完成订单数（0=从未下单）');

-- 优惠券表增加新老客字段（兼容MySQL 8.0，先忽略已存在的列错误）
ALTER TABLE t_coupon ADD COLUMN customer_type TINYINT DEFAULT 0 COMMENT '适用客群: 0=全部 1=新客 2=老客';

-- 优惠券发送任务表
CREATE TABLE IF NOT EXISTS t_coupon_dispatch (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  coupon_id BIGINT NOT NULL COMMENT '优惠券ID',
  dispatch_name VARCHAR(100) NOT NULL COMMENT '发送任务名称',
  rules TEXT COMMENT '规则JSON数组',
  phone_list TEXT COMMENT '指定手机号列表（逗号分隔）',
  target_count INT DEFAULT 0 COMMENT '目标人数',
  sent_count INT DEFAULT 0 COMMENT '实际发送人数',
  status TINYINT DEFAULT 0 COMMENT '0=草稿 1=执行中 2=完成 3=失败',
  sms_notify TINYINT DEFAULT 0 COMMENT '是否短信通知',
  operator VARCHAR(50) COMMENT '操作人',
  remark VARCHAR(200) COMMENT '备注',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted TINYINT DEFAULT 0
) COMMENT='优惠券发送任务表';
