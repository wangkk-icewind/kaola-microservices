# 考拉礼卡订单创建失败 - 调试指南

## 问题概述

**错误信息**:
```
POST http://localhost:8090/api/order/create 500 (Internal Server Error)
```

**后端日志中的根本原因**:
```
java.sql.SQLSyntaxErrorException: Unknown column 'product_id' in 'field list'
```

## 问题分析

### 根本原因
数据库表 `t_order_item` 缺少 `product_id` 和 `product_name` 字段，导致礼卡订单（GIFT_CARD类型）创建失败。

这些字段应该在数据库迁移脚本 `/db/order-schema-migration.sql` 中添加，但该脚本可能还未执行。

### 涉及的文件
- **数据库迁移脚本**: `kaola-microservices/db/order-schema-migration.sql`
- **实体类**: `kaola-order-service/src/main/java/com/kaola/order/model/entity/OrderItem.java:49-58`
- **服务实现**: `kaola-order-service/src/main/java/com/kaola/order/service/impl/OrderServiceImpl.java:161`

## 日志位置

### 1. 订单服务日志
订单服务运行在端口 **8086**，日志输出到控制台。

**查看日志命令**:
```bash
# 查看订单服务进程
lsof -i:8086

# 如果通过 mvn spring-boot:run 启动，日志在命令行窗口
cd /Users/icewind/Documents/workspaces/kaola-microservices/kaola-order-service
mvn spring-boot:run -DskipTests

# 或者查看最新错误日志（如果有日志文件）
tail -f kaola-order-service/logs/kaola-order-service.log
```

### 2. 网关日志
网关运行在端口 **8090**，所有API请求都通过网关转发。

### 3. 关键日志过滤
```bash
# 过滤包含错误的日志
grep -E "ERROR|Exception|SQLException" 日志文件

# 过滤礼卡相关日志
grep -E "GIFT_CARD|product_id" 日志文件
```

## 详细调试步骤

### 步骤 1: 检查数据库表结构

```bash
# 连接到 MySQL 数据库
mysql -uroot -pkaola123456

# 切换到订单数据库
USE kaola_massage;

# 查看 t_order_item 表结构
DESCRIBE t_order_item;

# 或者查看完整建表语句
SHOW CREATE TABLE t_order_item\G
```

**预期结果**:
表中应该包含以下字段：
- `id`
- `order_id`
- `item_type` (VARCHAR(20))
- `masseur_id`
- `project_id`
- **`product_id`** (BIGINT, 缺少此字段会报错)
- **`product_name`** (VARCHAR(200), 缺少此字段也会影响功能)
- **`quantity`** (INT, 商品数量)
- `price`
- `duration`
- `status`
- `create_time`
- `update_time`
- `deleted`

**如果缺少字段，执行步骤 2**

### 步骤 2: 执行数据库迁移脚本

```bash
# 执行迁移脚本
cd /Users/icewind/Documents/workspaces/kaola-microservices
mysql -uroot -pkaola123456 kaola_massage < db/order-schema-migration.sql
```

**或者手动执行 SQL**:
```sql
-- 连接数据库
mysql -uroot -pkaola123456

USE kaola_massage;

-- 修改 t_order 表，添加商品订单支持字段
ALTER TABLE `t_order`
ADD COLUMN `order_type` VARCHAR(20) NOT NULL DEFAULT 'SERVICE' COMMENT '订单类型 (SERVICE-服务订单 PRODUCT-商品订单 GIFT_CARD-礼卡订单)' AFTER `remark`,
ADD COLUMN `receiver_name` VARCHAR(50) NULL COMMENT '收货人姓名 (商品订单使用)' AFTER `order_type`,
ADD COLUMN `receiver_phone` VARCHAR(20) NULL COMMENT '收货人电话 (商品订单使用)' AFTER `receiver_name`,
ADD COLUMN `receiver_address` VARCHAR(500) NULL COMMENT '收货地址 (商品订单使用)' AFTER `receiver_phone`,
ADD COLUMN `tracking_no` VARCHAR(100) NULL COMMENT '物流单号 (商品订单使用)' AFTER `receiver_address`,
ADD INDEX `idx_order_type` (`order_type`);

-- 修改 t_order_item 表，添加商品项支持字段
ALTER TABLE `t_order_item`
ADD COLUMN `item_type` VARCHAR(20) NOT NULL DEFAULT 'SERVICE' COMMENT '订单项类型 (SERVICE-服务项 PRODUCT-商品项 GIFT_CARD-礼卡项)' AFTER `order_id`,
ADD COLUMN `product_id` BIGINT NULL COMMENT '商品ID (商品订单/礼卡订单使用)' AFTER `project_id`,
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

-- 更新现有订单数据
UPDATE `t_order` SET `order_type` = 'SERVICE' WHERE `order_type` IS NULL OR `order_type` = '';
UPDATE `t_order_item` SET `item_type` = 'SERVICE' WHERE `item_type` IS NULL OR `item_type` = '';
```

**注意**: 如果某些字段已经存在，执行时会报错。可以跳过已存在字段的 ALTER 语句。

### 步骤 3: 验证表结构更新

```sql
-- 再次查看表结构
DESCRIBE t_order_item;

-- 确认 product_id、product_name、quantity 字段已添加
SELECT COLUMN_NAME, DATA_TYPE, IS_NULLABLE, COLUMN_COMMENT
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_SCHEMA = 'kaola_massage'
  AND TABLE_NAME = 't_order_item'
  AND COLUMN_NAME IN ('product_id', 'product_name', 'quantity', 'item_type');
```

### 步骤 4: 重启订单服务

```bash
# 找到订单服务进程并杀掉
lsof -ti:8086 | xargs kill -9

# 等待3秒
sleep 3

# 重新启动订单服务
cd /Users/icewind/Documents/workspaces/kaola-microservices/kaola-order-service
mvn spring-boot:run -DskipTests &
```

**查看启动日志**:
```
2025-12-27T19:10:18.827+08:00  INFO 49647 --- [kaola-order-service] [main] c.kaola.order.OrderServiceApplication : Started OrderServiceApplication in 4.852 seconds
  Kaola Order Service 启动成功！
```

### 步骤 5: 重新测试礼卡订单创建

#### 5.1 准备测试数据
确保有可用的礼卡商品：
```sql
SELECT * FROM t_product WHERE product_type = 'GIFT_CARD' AND status = 1;
```

#### 5.2 通过小程序测试
1. 打开微信开发者工具
2. 进入"礼卡商城"页面
3. 选择一张礼卡，点击"立即购买"
4. 填写收货信息（姓名、电话、地址）
5. 点击"提交订单"

#### 5.3 观察日志输出
```bash
# 实时查看订单服务日志
tail -f kaola-order-service/logs/*.log

# 或者在运行 mvn spring-boot:run 的终端查看
```

**成功日志示例**:
```
DEBUG [nio-8086-exec-1] c.k.o.s.i.OrderServiceImpl : 创建订单请求: orderType=GIFT_CARD, items=[{itemType=GIFT_CARD, productId=1, quantity=1, price=500}]
DEBUG [nio-8086-exec-1] c.k.o.mapper.OrderMapper : ==> Preparing: INSERT INTO t_order (order_no, user_id, total_amount, ...) VALUES (?, ?, ?, ...)
DEBUG [nio-8086-exec-1] c.k.o.mapper.OrderItemMapper : ==> Preparing: INSERT INTO t_order_item (order_id, item_type, product_id, quantity, price, ...) VALUES (?, ?, ?, ?, ?, ...)
INFO  [nio-8086-exec-1] c.k.o.s.i.OrderServiceImpl : 订单创建成功: orderId=123, orderNo=ORDER202512271930001
```

**失败日志示例（仍然有问题）**:
```
ERROR [nio-8086-exec-1] c.k.o.s.i.OrderServiceImpl : 创建订单失败
java.sql.SQLSyntaxErrorException: Unknown column 'XXX' in 'field list'
```

### 步骤 6: 使用 Postman/curl 测试 API

```bash
# 测试创建礼卡订单 API
curl -X POST http://localhost:8090/api/order/create \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{
    "userId": 1,
    "storeId": 1,
    "orderType": "PRODUCT",
    "items": [
      {
        "itemType": "GIFT_CARD",
        "productId": 1,
        "productName": "500元礼卡",
        "quantity": 1,
        "price": 500.00
      }
    ],
    "receiverName": "测试用户",
    "receiverPhone": "13800138000",
    "receiverAddress": "广东省深圳市南山区科技园",
    "remark": "测试礼卡订单"
  }'
```

**预期响应 (成功)**:
```json
{
  "code": 0,
  "msg": "success",
  "data": {
    "id": 123,
    "orderNo": "ORDER202512271930001",
    "status": 0,
    "totalAmount": 500.00,
    "payAmount": 500.00
  }
}
```

**预期响应 (失败)**:
```json
{
  "code": 500,
  "msg": "订单创建失败",
  "data": null
}
```

## 常见问题排查

### Q1: 执行迁移脚本时报错 "Duplicate column name"
**原因**: 字段已经存在
**解决**: 忽略该错误，或者先检查字段是否存在再执行

```sql
-- 检查字段是否存在
SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_SCHEMA = 'kaola_massage'
  AND TABLE_NAME = 't_order_item'
  AND COLUMN_NAME = 'product_id';
-- 返回 0 表示不存在，返回 1 表示已存在
```

### Q2: 订单创建成功但没有数据
**原因**: MyBatis-Plus 插入时字段为 NULL
**解决**: 检查实体类字段映射和请求参数

```sql
-- 查看最新创建的订单明细
SELECT * FROM t_order_item ORDER BY id DESC LIMIT 5;
```

### Q3: 礼卡订单类型不对
**原因**: 前端传递的 `orderType` 或 `itemType` 不正确
**解决**:
- 商品订单: `orderType=PRODUCT`, `itemType=PRODUCT`
- 礼卡订单: `orderType=PRODUCT`, `itemType=GIFT_CARD`
- 服务订单: `orderType=SERVICE`, `itemType=SERVICE`

### Q4: MySQL 连接失败
**错误**: `Access denied for user 'root'@'localhost'`
**解决**: 检查 MySQL 用户密码
```bash
# 重置 MySQL root 密码（如果忘记）
mysql -uroot -p
ALTER USER 'root'@'localhost' IDENTIFIED BY 'kaola123456';
FLUSH PRIVILEGES;
```

## 验证检查清单

- [ ] 数据库表 `t_order_item` 包含 `product_id` 字段
- [ ] 数据库表 `t_order_item` 包含 `product_name` 字段
- [ ] 数据库表 `t_order_item` 包含 `quantity` 字段
- [ ] 数据库表 `t_order_item` 包含 `item_type` 字段
- [ ] 数据库表 `t_order` 包含 `order_type` 字段
- [ ] 订单服务已重启并成功启动
- [ ] 小程序可以成功创建礼卡订单
- [ ] 订单详情可以正确显示礼卡信息

## 相关文档

- **数据库迁移脚本**: `/Users/icewind/Documents/workspaces/kaola-microservices/db/order-schema-migration.sql`
- **订单实体类**: `kaola-order-service/src/main/java/com/kaola/order/model/entity/OrderItem.java`
- **订单服务实现**: `kaola-order-service/src/main/java/com/kaola/order/service/impl/OrderServiceImpl.java`

## 最后更新时间
2025-12-27
