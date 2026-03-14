# 优惠券系统业务需求文档

## 1. 业务概述

优惠券系统为考拉推拿预约平台提供营销促销功能，支持用户领取、使用优惠券，帮助提升用户活跃度和订单转化率。

## 2. 优惠券类型

### 2.1 满减券 (FULL_REDUCTION, type=1)
- **规则**: 订单金额满足最低消费金额(minAmount)时，减免固定金额(value)
- **示例**: 满100元减20元
- **计算**: 最终金额 = 订单金额 - value (当订单金额 >= minAmount)

### 2.2 折扣券 (DISCOUNT, type=2)
- **规则**: 订单金额满足最低消费金额(minAmount)时，按折扣比例计算
- **示例**: 满50元享8折 (value=0.8)
- **计算**: 最终金额 = 订单金额 × value (当订单金额 >= minAmount)

### 2.3 代金券 (VOUCHER, type=3)
- **规则**: 无门槛直接抵扣固定金额(value)
- **示例**: 20元代金券
- **计算**: 最终金额 = 订单金额 - value (无minAmount限制)

## 3. 优惠券状态

### 3.1 优惠券主表状态 (Coupon.status)
- **0**: 禁用 - 不可领取和使用
- **1**: 启用 - 可正常领取和使用

### 3.2 用户优惠券状态 (UserCoupon.status)
- **1**: 未使用 (UNUSED) - 可以使用
- **2**: 已使用 (USED) - 已在订单中使用
- **3**: 已过期 (EXPIRED) - 超过有效期

## 4. 核心业务规则

### 4.1 优惠券领取规则
1. **库存检查**: usedCount < totalCount
2. **状态检查**: coupon.status = 1 (启用状态)
3. **时间检查**: 当前时间在 [startTime, endTime] 范围内
4. **重复领取**: 同一用户可以多次领取同一优惠券(根据业务需求)
5. **领取成功**: 创建UserCoupon记录，status=1(未使用)

### 4.2 优惠券使用规则
1. **用户拥有**: 用户必须拥有该优惠券(UserCoupon记录存在)
2. **状态检查**: userCoupon.status = 1 (未使用)
3. **时间检查**: 当前时间在优惠券有效期内
4. **金额检查**: 订单金额满足minAmount要求(代金券除外)
5. **规则检查**: 满足rules中定义的限制条件(门店、项目等)

### 4.3 优惠券过期处理
- **定时任务**: 每天凌晨检查所有未使用的优惠券
- **过期条件**: 当前时间 > endTime 且 status = 1
- **处理方式**: 更新 userCoupon.status = 3 (已过期)

## 5. API接口需求

### 5.1 GET /coupon/my-list
**功能**: 获取用户的优惠券列表

**请求参数**:
- userId: Long (从token获取)
- status: Integer (可选, 1-未使用 2-已使用 3-已过期)

**响应数据**:
```json
{
  "code": 0,
  "data": [
    {
      "userCouponId": 1,
      "couponId": 10,
      "name": "新用户专享券",
      "type": 1,
      "value": 20.00,
      "minAmount": 100.00,
      "startTime": "2026-01-01 00:00:00",
      "endTime": "2026-12-31 23:59:59",
      "status": 1,
      "useTime": null,
      "orderId": null
    }
  ]
}
```

### 5.2 POST /coupon/{id}/claim
**功能**: 领取优惠券

**请求参数**:
- id: Long (优惠券ID, 路径参数)
- userId: Long (从token获取)

**业务逻辑**:
1. 检查优惠券是否存在且启用
2. 检查库存是否充足
3. 检查是否在有效期内
4. 创建UserCoupon记录
5. 更新优惠券usedCount(可选,领取时不增加,使用时才增加)

**响应数据**:
```json
{
  "code": 0,
  "message": "领取成功",
  "data": true
}
```

### 5.3 POST /coupon/available
**功能**: 获取订单可用的优惠券列表

**请求参数**:
```json
{
  "userId": 1,
  "orderAmount": 150.00,
  "storeId": 1,
  "projectIds": [1, 2]
}
```

**业务逻辑**:
1. 查询用户所有未使用的优惠券
2. 过滤已过期的优惠券
3. 检查订单金额是否满足minAmount
4. 检查rules限制(门店、项目)
5. 计算每个优惠券的优惠金额
6. 按优惠金额降序排序

**响应数据**:
```json
{
  "code": 0,
  "data": [
    {
      "userCouponId": 1,
      "couponId": 10,
      "name": "满100减20",
      "type": 1,
      "value": 20.00,
      "minAmount": 100.00,
      "discountAmount": 20.00,
      "canUse": true,
      "reason": ""
    }
  ]
}
```

### 5.4 POST /coupon/validate
**功能**: 验证优惠券是否可用

**请求参数**:
```json
{
  "userCouponId": 1,
  "userId": 1,
  "orderAmount": 150.00,
  "storeId": 1,
  "projectIds": [1, 2]
}
```

**响应数据**:
```json
{
  "code": 0,
  "data": {
    "valid": true,
    "discountAmount": 20.00,
    "finalAmount": 130.00,
    "reason": ""
  }
}
```

## 6. 数据模型

### 6.1 Rules字段格式 (JSON)
```json
{
  "storeIds": [1, 2, 3],
  "projectIds": [10, 20, 30],
  "excludeStoreIds": [],
  "excludeProjectIds": []
}
```

## 7. 非功能需求

### 7.1 并发控制
- 领取优惠券时需要使用乐观锁或分布式锁防止超发
- 使用数据库事务保证数据一致性

### 7.2 性能要求
- 查询用户优惠券列表响应时间 < 500ms
- 验证优惠券可用性响应时间 < 200ms

### 7.3 安全要求
- 所有接口需要用户认证
- 防止恶意刷券行为
