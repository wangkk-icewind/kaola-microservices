# 考拉推拿微服务架构

## 项目简介

本项目是考拉推拿系统的微服务架构版本，从单体应用重构而来，采用 Spring Cloud + Spring Cloud Alibaba 技术栈。

## 技术栈

- **服务框架**: Spring Boot 3.2.0 + Spring Cloud 2023.0.0
- **服务注册**: Nacos 2.3.0
- **API 网关**: Spring Cloud Gateway
- **配置中心**: Nacos Config
- **负载均衡**: Spring Cloud LoadBalancer
- **服务调用**: OpenFeign
- **熔断限流**: Sentinel
- **链路追踪**: Micrometer Tracing + Zipkin
- **数据库**: MySQL 8.0
- **缓存**: Redis
- **构建工具**: Maven 3.8+
- **JDK**: Java 17+

## 项目结构

```
kaola-microservices/
├── kaola-common/                    # 公共模块
│   ├── kaola-common-core/           # 核心公共类
│   ├── kaola-common-model/          # 公共数据模型
│   ├── kaola-common-util/           # 工具类
│   ├── kaola-common-redis/          # Redis 配置
│   └── kaola-common-security/       # 安全配置
│
├── kaola-gateway/                   # API 网关 (端口: 8080)
│
├── kaola-auth-service/              # 认证授权服务 (端口: 8081)
│
├── kaola-user-service/              # 用户服务 (端口: 8082)
│
├── kaola-store-service/             # 门店服务 (端口: 8083)
│
├── kaola-masseur-service/           # 技师服务 (端口: 8084)
│
├── kaola-product-service/           # 产品服务 (端口: 8085)
│
├── kaola-order-service/             # 订单服务 (端口: 8086)
│
├── kaola-payment-service/           # 支付服务 (端口: 8087)
│
├── kaola-marketing-service/         # 营销服务 (端口: 8088)
│
├── kaola-review-service/            # 评价服务 (端口: 8089)
│
├── kaola-complaint-service/         # 投诉服务 (端口: 8090)
│
├── kaola-schedule-service/          # 排班服务 (端口: 8091)
│
├── kaola-earning-service/           # 收益服务 (端口: 8092)
│
├── kaola-file-service/              # 文件服务 (端口: 8093)
│
├── kaola-notification-service/      # 通知服务 (端口: 8094)
│
└── kaola-admin-service/             # 管理后台服务 (端口: 8095)
```

## 服务端口分配

| 服务名称 | 端口 | 描述 |
|---------|------|------|
| kaola-gateway | 8080 | API 网关（保持原端口） |
| kaola-auth-service | 8081 | 认证授权服务 |
| kaola-user-service | 8082 | 用户服务 |
| kaola-store-service | 8083 | 门店服务 |
| kaola-masseur-service | 8084 | 技师服务 |
| kaola-product-service | 8085 | 产品服务 |
| kaola-order-service | 8086 | 订单服务 |
| kaola-payment-service | 8087 | 支付服务 |
| kaola-marketing-service | 8088 | 营销服务 |
| kaola-review-service | 8089 | 评价服务 |
| kaola-complaint-service | 8090 | 投诉服务 |
| kaola-schedule-service | 8091 | 排班服务 |
| kaola-earning-service | 8092 | 收益服务 |
| kaola-file-service | 8093 | 文件服务 |
| kaola-notification-service | 8094 | 通知服务 |
| kaola-admin-service | 8095 | 管理后台服务 |
| Nacos Server | 8848 | 服务注册中心 |

## 快速开始

### 前置要求

- JDK 17+
- Maven 3.8+
- MySQL 8.0+
- Redis 6.0+
- Nacos 2.3.0+

### 环境准备

1. **启动 MySQL**
```bash
# 确保 MySQL 服务运行在 3306 端口
mysql -u root -p
```

2. **启动 Redis**
```bash
# 启动 Redis 服务
redis-server
```

3. **启动 Nacos**
```bash
# 下载 Nacos 2.3.0
# 启动 Nacos (单机模式)
sh startup.sh -m standalone
```

### 构建项目

```bash
# 克隆项目
git clone <repository-url>
cd kaola-microservices

# 构建所有模块
mvn clean install -DskipTests
```

### 启动服务

**推荐启动顺序**:

1. 启动公共服务（Nacos, MySQL, Redis）
2. 启动 Gateway
3. 启动 Auth Service
4. 启动其他业务服务

```bash
# 启动 Gateway
cd kaola-gateway
mvn spring-boot:run

# 启动 Auth Service
cd ../kaola-auth-service
mvn spring-boot:run

# 启动其他服务...
```

## 配置说明

### Nacos 配置

所有服务的配置统一在 Nacos 配置中心管理。

**配置 DataId 命名规范**:
- 公共配置: `kaola-common.yml`
- 服务配置: `${spring.application.name}.yml`

**示例**:
```yaml
# kaola-common.yml (公共配置)
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/${db.name}?useUnicode=true&characterEncoding=utf8

  redis:
    host: localhost
    port: 6379

jwt:
  secret: kaola-massage-secret-key-2024
  expiration: 86400000
```

```yaml
# kaola-order-service.yml (订单服务配置)
server:
  port: 8086

db:
  name: kaola_massage_order
```

## API 网关路由

所有客户端请求统一通过 API Gateway (端口 8080) 进入，保持原有 `/api` 路径不变：

- 用户端 API: `/api/user/**`, `/api/store/**`, `/api/project/**`, etc.
- 管理后台 API: `/api/admin/**`

**示例请求**:
```bash
# 用户登录
POST http://localhost:8080/api/user/login

# 管理员登录
POST http://localhost:8080/api/admin/auth/login

# 获取门店列表
GET http://localhost:8080/api/store/list

# 管理后台仪表盘
GET http://localhost:8080/api/admin/dashboard/overview
```

## 数据库设计

每个微服务使用独立的数据库 schema：

```
kaola_massage_auth         # 认证授权服务
kaola_massage_user         # 用户服务
kaola_massage_store        # 门店服务
kaola_massage_masseur      # 技师服务
kaola_massage_product      # 产品服务
kaola_massage_order        # 订单服务
kaola_massage_payment      # 支付服务
kaola_massage_marketing    # 营销服务
kaola_massage_review       # 评价服务
kaola_massage_complaint    # 投诉服务
kaola_massage_schedule     # 排班服务
kaola_massage_earning      # 收益服务
```

## 迁移指南

从单体应用迁移到微服务架构，请参考 `../server/MICROSERVICES_ARCHITECTURE.md` 中的详细迁移计划。

### 迁移阶段

1. **Phase 1**: 基础设施准备 (Nacos, Gateway, Common 模块)
2. **Phase 2**: 核心服务拆分 (Auth, User, Order, Payment, Product)
3. **Phase 3**: 业务服务拆分 (Store, Masseur, Marketing, Review, 等)
4. **Phase 4**: 管理后台服务
5. **Phase 5**: 测试和优化
6. **Phase 6**: 灰度发布

### 兼容性保证

- **API 路径**: 保持原有 `/api` 路径不变
- **响应格式**: 保持原有 `Result<T>` 响应格式
- **认证方式**: 保持原有 JWT 认证方式
- **数据库**: 初期共用原数据库，验证后再拆分

## 监控和运维

### 健康检查

所有服务提供健康检查端点:
```bash
GET http://localhost:{port}/actuator/health
```

### 服务注册状态

查看 Nacos 控制台:
```
http://localhost:8848/nacos
```

### API 文档

每个服务提供 Swagger UI:
```
http://localhost:{port}/swagger-ui.html
```

## 开发指南

### 新增服务

1. 在父 POM 中添加模块声明
2. 创建服务目录和 pom.xml
3. 添加 Nacos 服务注册配置
4. 实现业务逻辑
5. 在 Gateway 中配置路由

### 服务间调用

使用 OpenFeign 进行服务间调用:

```java
@FeignClient(name = "kaola-product-service")
public interface ProductServiceClient {
    @GetMapping("/internal/project/{id}")
    ProjectDTO getProjectById(@PathVariable Long id);
}
```

## 常见问题

### Q: 如何确保数据一致性？

A:
- 弱一致性场景: 使用 RocketMQ 实现最终一致性
- 强一致性场景: 使用 Seata 分布式事务

### Q: 如何处理服务故障？

A:
- 使用 Sentinel 实现熔断降级
- 配置合理的超时时间
- 实现优雅降级逻辑

### Q: 如何进行灰度发布？

A:
- 使用 Gateway 路由权重配置
- 基于请求头或参数进行流量切换

## 贡献指南

1. Fork 项目
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启 Pull Request

## 许可证

[MIT License](LICENSE)

## 联系方式

- 项目负责人: Kaola Team
- 邮箱: dev@kaola-massage.com

---

## 重要启动说明 (2026-01-26更新)

### 关键依赖修复

在首次启动前，必须先构建 kaola-common 模块。该模块的 POM 文件已修复以下依赖版本问题：

**kaola-common-util/pom.xml**:
- JWT 依赖版本: 0.12.3 (必须使用此版本，0.11.5 不兼容)

**kaola-common-model/pom.xml**:
- MyBatis Plus 版本: 3.5.9

**构建命令**:
```bash
cd kaola-common
mvn clean install -DskipTests
```

### 正确的端口配置

**注意**: Gateway 实际运行在 **8090** 端口，而非文档中的 8080。

| 服务名称 | 实际端口 | 说明 |
|---------|---------|------|
| kaola-gateway | **8090** | API 网关 (已更新) |
| kaola-auth-service | 8081 | 认证授权服务 |
| kaola-user-service | 8082 | 用户服务 |
| kaola-store-service | 8083 | 门店服务 |
| kaola-masseur-service | 8084 | 技师服务 |
| kaola-product-service | 8085 | 产品服务 |
| kaola-order-service | 8086 | 订单服务 |
| kaola-admin-service | 8095 | 管理后台服务 |
| Nacos Server | 8848/9848 | 服务注册中心 |

### 完整启动流程

#### 1. 启动基础设施

```bash
# 启动 Nacos (Docker 方式)
docker run -d --name nacos-server \
  -e MODE=standalone \
  -p 8848:8848 -p 9848:9848 \
  nacos/nacos-server:v2.3.0

# 验证 Nacos 启动
curl http://localhost:8848/nacos
```

#### 2. 构建 kaola-common 模块

```bash
cd /path/to/kaola-microservices/kaola-common
mvn clean install -DskipTests
```

#### 3. 启动微服务 (推荐顺序)

```bash
# 启动 Gateway
cd ../kaola-gateway
mvn spring-boot:run -DskipTests &

# 启动核心服务
cd ../kaola-product-service
mvn spring-boot:run -DskipTests &

cd ../kaola-auth-service
mvn spring-boot:run -DskipTests &

cd ../kaola-user-service
mvn spring-boot:run -DskipTests &

cd ../kaola-store-service
mvn spring-boot:run -DskipTests &

cd ../kaola-masseur-service
mvn spring-boot:run -DskipTests &

cd ../kaola-order-service
mvn spring-boot:run -DskipTests &

cd ../kaola-admin-service
mvn spring-boot:run -DskipTests &
```

#### 4. 启动前端

```bash
cd /path/to/kaola/admin-web
npm run dev
# 访问: http://localhost:3002
```

### 验证服务状态

```bash
# 检查所有服务端口
lsof -i :8081 -i :8082 -i :8083 -i :8084 -i :8085 -i :8086 -i :8090 -i :8095 | grep LISTEN

# 查看 Nacos 注册的服务
curl http://localhost:8848/nacos/v1/ns/instance/list?serviceName=kaola-gateway

# 测试 Gateway 路由
curl http://localhost:8090/api/admin/project/list
```

### 前端配置

admin-web 的 vite.config.ts 已配置为通过 Gateway 访问所有 API:

```typescript
server: {
  port: 3002,
  proxy: {
    '/api': {
      target: 'http://localhost:8090',  // Gateway 端口
      changeOrigin: true,
    },
  },
}
```

### 常见启动问题

**问题 1: kaola-common 依赖找不到**
```
解决: 先执行 cd kaola-common && mvn clean install -DskipTests
```

**问题 2: JWT 版本不兼容错误**
```
错误: 找不到符号 verifyWith(javax.crypto.SecretKey)
解决: 确保 kaola-common-util/pom.xml 中 JWT 版本为 0.12.3
```

**问题 3: Nacos 连接失败**
```
错误: Connection refused: localhost:9848
解决: 启动 Nacos Docker 容器或本地 Nacos 服务
```

**问题 4: 端口被占用**
```
解决: 使用 lsof -i :端口号 查找占用进程，kill 后重启
```

### 快速重启脚本

创建 `start-all.sh`:
```bash
#!/bin/bash

# 启动 Nacos
docker start nacos-server || docker run -d --name nacos-server -e MODE=standalone -p 8848:8848 -p 9848:9848 nacos/nacos-server:v2.3.0

# 等待 Nacos 启动
sleep 10

# 启动所有微服务
cd /path/to/kaola-microservices

services=(
  "kaola-gateway"
  "kaola-product-service"
  "kaola-auth-service"
  "kaola-user-service"
  "kaola-store-service"
  "kaola-masseur-service"
  "kaola-order-service"
  "kaola-admin-service"
)

for service in "${services[@]}"; do
  echo "Starting $service..."
  cd $service
  mvn spring-boot:run -DskipTests > /dev/null 2>&1 &
  cd ..
  sleep 5
done

echo "All services started!"
```

---

**文档版本**: v1.1
**最后更新**: 2026-01-26
