# Kaola Gateway - API 网关服务

## 概述

Kaola Gateway 是考拉推拿预约系统的统一 API 网关，基于 Spring Cloud Gateway 构建，提供路由转发、负载均衡、跨域处理等功能。

## 核心功能

- **统一入口**: 所有客户端请求通过网关统一访问，保持 `/api` 路径兼容性
- **服务路由**: 基于路径的智能路由转发到后端微服务
- **负载均衡**: 集成 Spring Cloud LoadBalancer 自动负载均衡
- **服务发现**: 通过 Nacos 自动发现和注册服务
- **跨域处理**: 统一处理 CORS 跨域请求
- **配置中心**: 集成 Nacos Config 实现动态配置管理

## 技术栈

- Spring Boot 3.2.0
- Spring Cloud Gateway 2023.0.0
- Spring Cloud Alibaba Nacos 2023.0.1.0
- Spring Cloud LoadBalancer
- Java 17

## 路由配置

网关配置了 40+ 条路由规则，覆盖以下服务：

### 用户端 API

| 路径前缀 | 目标服务 | 说明 |
|---------|---------|------|
| `/api/user/**` | kaola-user-service | 用户服务 |
| `/api/store/**` | kaola-store-service | 门店服务 |
| `/api/masseur/**` | kaola-masseur-service | 技师服务 |
| `/api/project/**` | kaola-product-service | 项目服务 |
| `/api/category/**` | kaola-product-service | 分类服务 |
| `/api/order/**` | kaola-order-service | 订单服务 |
| `/api/cart/**` | kaola-order-service | 购物车服务 |
| `/api/payment/**` | kaola-payment-service | 支付服务 |
| `/api/promotion/**` | kaola-marketing-service | 促销服务 |
| `/api/coupon/**` | kaola-marketing-service | 优惠券服务 |
| `/api/review/**` | kaola-review-service | 评价服务 |
| `/api/complaint/**` | kaola-complaint-service | 投诉服务 |
| `/api/upload/**` | kaola-file-service | 文件上传服务 |

### 管理后台 API

| 路径前缀 | 目标服务 | 说明 |
|---------|---------|------|
| `/api/admin/auth/**` | kaola-auth-service | 管理员认证 |
| `/api/admin/role/**` | kaola-auth-service | 角色管理 |
| `/api/admin/dashboard/**` | kaola-admin-service | 仪表盘 |
| `/api/admin/settings/**` | kaola-admin-service | 系统设置 |
| `/api/admin/user/**` | kaola-user-service | 用户管理 |
| `/api/admin/store/**` | kaola-store-service | 门店管理 |
| `/api/admin/masseur/**` | kaola-masseur-service | 技师管理 |
| `/api/admin/schedule/**` | kaola-schedule-service | 排班管理 |
| `/api/admin/leave/**` | kaola-schedule-service | 请假管理 |
| `/api/admin/project/**` | kaola-product-service | 项目管理 |
| `/api/admin/category/**` | kaola-product-service | 分类管理 |
| `/api/admin/order/**` | kaola-order-service | 订单管理 |
| `/api/admin/promotion/**` | kaola-marketing-service | 促销管理 |
| `/api/admin/coupon/**` | kaola-marketing-service | 优惠券管理 |
| `/api/admin/review/**` | kaola-review-service | 评价管理 |
| `/api/admin/complaint/**` | kaola-complaint-service | 投诉管理 |
| `/api/admin/earning/**` | kaola-earning-service | 收益管理 |
| `/api/admin/withdrawal/**` | kaola-earning-service | 提现管理 |

## 前置条件

在启动 Gateway 之前，请确保：

1. **Nacos 服务已启动**
   ```bash
   # 使用 Docker（推荐）
   docker run -d \
     --name nacos-server \
     -p 8848:8848 \
     -p 9848:9848 \
     -e MODE=standalone \
     nacos/nacos-server:v2.3.0

   # 或使用 Homebrew（macOS）
   brew install nacos
   brew services start nacos
   ```

2. **验证 Nacos 可访问**
   - 访问: http://localhost:8848/nacos
   - 默认账号: nacos / nacos

3. **Maven 依赖已安装**
   ```bash
   # 在项目根目录安装 common 模块
   cd kaola-microservices
   mvn clean install -pl kaola-common -am
   ```

## 启动方式

### 方式 1: Maven 启动（开发环境）

```bash
cd kaola-gateway
mvn spring-boot:run
```

### 方式 2: IDE 启动

在 IntelliJ IDEA 或 Eclipse 中：
1. 打开 `GatewayApplication.java`
2. 右键选择 "Run" 或 "Debug"

### 方式 3: 打包运行（生产环境）

```bash
# 打包
mvn clean package -DskipTests

# 运行
java -jar target/kaola-gateway-1.0.0.jar
```

## 验证启动

### 1. 查看启动日志

成功启动后会显示：
```
=================================
   Kaola Gateway 启动成功！
   端口: 8080
=================================
```

### 2. 验证 Nacos 注册

访问 Nacos 控制台：http://localhost:8848/nacos
- 服务列表中应显示 `kaola-gateway`

### 3. 健康检查

```bash
# 检查网关健康状态
curl http://localhost:8080/actuator/health

# 查看网关路由信息
curl http://localhost:8080/actuator/gateway/routes
```

### 4. 测试路由转发

```bash
# 测试用户服务路由（需要 kaola-user-service 已启动）
curl http://localhost:8080/api/user/info

# 测试管理后台路由（需要 kaola-admin-service 已启动）
curl http://localhost:8080/api/admin/dashboard/stats
```

## 配置说明

### 端口配置

- **服务端口**: 8080
- **上下文路径**: `/api`
- **完整访问地址**: `http://localhost:8080/api/**`

### Nacos 配置

bootstrap.yml 中配置了 Nacos 连接信息：

```yaml
spring:
  cloud:
    nacos:
      discovery:
        server-addr: localhost:8848  # Nacos 服务地址
        namespace: public            # 命名空间
        group: DEFAULT_GROUP         # 分组
      config:
        server-addr: localhost:8848
        file-extension: yml
        shared-configs:
          - data-id: kaola-common.yml  # 共享配置
            refresh: true
```

### 路由规则

每条路由规则包含：
- **id**: 路由唯一标识
- **uri**: 目标服务地址（使用 `lb://` 前缀启用负载均衡）
- **predicates**: 路由匹配条件（基于路径）
- **filters**: 请求/响应过滤器（StripPrefix 去除 /api 前缀）

示例：
```yaml
- id: user-service
  uri: lb://kaola-user-service
  predicates:
    - Path=/api/user/**
  filters:
    - StripPrefix=1  # 转发到服务时去除 /api
```

## 日志配置

Gateway 配置了详细的调试日志：

```yaml
logging:
  level:
    org.springframework.cloud.gateway: DEBUG
    com.kaola: DEBUG
```

查看日志可帮助排查路由问题。

## 常见问题

### 1. 启动失败: 无法连接 Nacos

**问题**: `Unable to connect to Nacos Server`

**解决**:
- 确认 Nacos 已启动: `curl http://localhost:8848/nacos`
- 检查 bootstrap.yml 中的 server-addr 配置
- 查看防火墙是否阻止了 8848 端口

### 2. 路由 404: Service Unavailable

**问题**: 请求返回 503 或 404

**解决**:
- 确认目标微服务已启动并注册到 Nacos
- 在 Nacos 控制台查看服务列表
- 检查路由配置中的 service-id 是否正确

### 3. CORS 跨域问题

**问题**: 前端请求被 CORS 策略拦截

**解决**:
- Gateway 已配置 CorsConfig，允许所有域名
- 检查是否有多个服务配置了 CORS（可能冲突）
- 确认请求头包含正确的 Origin

### 4. 端口冲突

**问题**: 8080 端口已被占用

**解决**:
```bash
# 方式 1: 修改配置
# 在 application.yml 中修改 server.port

# 方式 2: 停止占用端口的进程
lsof -ti:8080 | xargs kill -9
```

## 监控和管理

### Actuator 端点

Gateway 暴露了以下管理端点：

| 端点 | 说明 |
|-----|------|
| `/actuator/health` | 健康检查 |
| `/actuator/info` | 服务信息 |
| `/actuator/gateway/routes` | 查看所有路由 |
| `/actuator/gateway/routes/{id}` | 查看单个路由 |
| `/actuator/gateway/refresh` | 刷新路由 |

### 动态路由刷新

如果在 Nacos 中修改了路由配置，可以通过以下命令刷新：

```bash
curl -X POST http://localhost:8080/actuator/gateway/refresh
```

## 性能优化

### 1. 连接池配置

在 application.yml 中可配置 HTTP 客户端连接池：

```yaml
spring:
  cloud:
    gateway:
      httpclient:
        pool:
          max-connections: 1000
          max-pending-acquires: 2000
```

### 2. 限流配置

可添加 RequestRateLimiter 过滤器实现限流：

```yaml
filters:
  - name: RequestRateLimiter
    args:
      redis-rate-limiter.replenishRate: 10
      redis-rate-limiter.burstCapacity: 20
```

## 下一步

完成 Gateway 配置后，可以开始实施以下工作：

1. **创建业务微服务**
   - 参考 MICROSERVICES_ARCHITECTURE.md 中的服务列表
   - 推荐从 kaola-auth-service 或 kaola-product-service 开始

2. **添加认证过滤器**
   - 在 kaola-common-security 模块中创建 JWT 过滤器
   - 在 Gateway 中添加全局认证拦截

3. **配置熔断降级**
   - 集成 Sentinel 实现熔断保护
   - 配置降级策略

4. **集成链路追踪**
   - 添加 Sleuth + Zipkin 实现分布式追踪
   - 便于问题排查和性能分析

## 相关文档

- [微服务架构设计](../MICROSERVICES_ARCHITECTURE.md)
- [Nacos 部署指南](../NACOS_SETUP_GUIDE.md)
- [实施计划](../MICROSERVICES_IMPLEMENTATION_SUMMARY.md)
- [Spring Cloud Gateway 官方文档](https://spring.io/projects/spring-cloud-gateway)

## 技术支持

如遇到问题，请检查：
1. Nacos 控制台服务注册情况
2. Gateway 启动日志
3. 目标服务是否正常运行
4. 网络连接是否正常

---

**版本**: 1.0.0
**更新时间**: 2024-11
**维护团队**: Kaola Team
