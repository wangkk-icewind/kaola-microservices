# Nacos 服务注册中心部署指南

## 方案一：Docker 部署（推荐）

### 1. 拉取 Nacos 镜像

```bash
# 使用最新稳定版本
docker pull nacos/nacos-server:v2.3.0

# 或使用阿里云镜像加速（如果 Docker Hub 访问慢）
docker pull registry.cn-hangzhou.aliyuncs.com/nacos/nacos-server:v2.3.0
docker tag registry.cn-hangzhou.aliyuncs.com/nacos/nacos-server:v2.3.0 nacos/nacos-server:v2.3.0
```

### 2. 启动 Nacos（standalone 模式）

```bash
docker run -d \
  --name nacos-server \
  -e MODE=standalone \
  -e PREFER_HOST_MODE=hostname \
  -p 8848:8848 \
  -p 9848:9848 \
  nacos/nacos-server:v2.3.0
```

### 3. 验证启动

```bash
# 查看容器状态
docker ps | grep nacos

# 查看日志
docker logs -f nacos-server

# 等待启动完成（大约 30 秒）
# 访问控制台：http://localhost:8848/nacos
# 默认用户名/密码：nacos/nacos
```

---

## 方案二：本地安装部署

### 1. 下载 Nacos

```bash
# 访问官网下载
# https://github.com/alibaba/nacos/releases

# 或使用 wget 下载
wget https://github.com/alibaba/nacos/releases/download/2.3.0/nacos-server-2.3.0.zip

# 解压
unzip nacos-server-2.3.0.zip
cd nacos
```

### 2. 启动 Nacos

**macOS/Linux**:
```bash
# standalone 模式启动
sh bin/startup.sh -m standalone
```

**Windows**:
```bash
# standalone 模式启动
cmd bin/startup.cmd -m standalone
```

### 3. 验证启动

```bash
# 查看日志
tail -f logs/start.out

# 访问控制台：http://localhost:8848/nacos
# 默认用户名/密码：nacos/nacos
```

### 4. 关闭 Nacos

**macOS/Linux**:
```bash
sh bin/shutdown.sh
```

**Windows**:
```bash
cmd bin/shutdown.cmd
```

---

## 方案三：使用 Homebrew（仅 macOS）

```bash
# 安装 Nacos
brew install nacos

# 启动服务
nacos -m standalone

# 或作为后台服务
brew services start nacos
```

---

## Nacos 配置

### 访问地址

- **控制台**: http://localhost:8848/nacos
- **服务注册**: http://localhost:8848
- **默认账号**: nacos / nacos

### 创建命名空间（可选）

1. 登录 Nacos 控制台
2. 点击"命名空间"菜单
3. 点击"新建命名空间"
4. 输入命名空间 ID: `kaola-microservices`
5. 输入命名空间名: `考拉推拿微服务`
6. 点击"确认"

### 创建配置

#### 1. 公共配置（kaola-common.yml）

在 Nacos 控制台创建配置：

- **Data ID**: `kaola-common.yml`
- **Group**: `DEFAULT_GROUP`
- **配置格式**: `YAML`
- **配置内容**:

```yaml
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5

  redis:
    host: localhost
    port: 6379
    database: 0
    timeout: 3000ms
    lettuce:
      pool:
        max-active: 8
        max-idle: 8
        min-idle: 0
        max-wait: -1ms

jwt:
  secret: kaola-massage-secret-key-2024-extended-secure
  expiration: 86400000
  header: Authorization
  prefix: Bearer

logging:
  level:
    com.kaola: DEBUG
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} - %msg%n"
```

#### 2. Gateway 配置（kaola-gateway.yml）

- **Data ID**: `kaola-gateway.yml`
- **Group**: `DEFAULT_GROUP`
- **配置格式**: `YAML`
- **配置内容**:

```yaml
server:
  port: 8080

spring:
  application:
    name: kaola-gateway

  cloud:
    gateway:
      discovery:
        locator:
          enabled: true
          lower-case-service-id: true

      globalcors:
        cors-configurations:
          '[/**]':
            allowed-origins: "*"
            allowed-methods: "*"
            allowed-headers: "*"
            allow-credentials: true

      routes:
        # 用户服务
        - id: user-service
          uri: lb://kaola-user-service
          predicates:
            - Path=/api/user/**

        # 门店服务
        - id: store-service
          uri: lb://kaola-store-service
          predicates:
            - Path=/api/store/**

        # 产品服务
        - id: product-service
          uri: lb://kaola-product-service
          predicates:
            - Path=/api/project/**,/api/category/**

        # 订单服务
        - id: order-service
          uri: lb://kaola-order-service
          predicates:
            - Path=/api/order/**,/api/cart/**

        # 管理后台 - 认证
        - id: admin-auth
          uri: lb://kaola-auth-service
          predicates:
            - Path=/api/admin/auth/**

        # 管理后台 - 仪表盘
        - id: admin-dashboard
          uri: lb://kaola-admin-service
          predicates:
            - Path=/api/admin/dashboard/**
```

---

## 常见问题

### 1. 端口被占用

```bash
# 检查端口占用
lsof -i :8848

# 杀死占用进程
kill -9 <PID>
```

### 2. 启动失败

```bash
# 查看日志
docker logs nacos-server

# 或本地安装查看
tail -f nacos/logs/start.out
```

### 3. 无法访问控制台

- 确认 Nacos 已启动成功
- 检查防火墙设置
- 确认端口 8848 未被其他程序占用

### 4. 服务注册失败

- 确认 Nacos 服务正常运行
- 检查微服务配置中的 Nacos 地址
- 查看微服务日志

---

## 健康检查

```bash
# 检查 Nacos 健康状态
curl http://localhost:8848/nacos/v1/console/health/liveness

# 查看已注册服务
curl http://localhost:8848/nacos/v1/ns/service/list?pageNo=1&pageSize=10
```

---

## Docker Compose 部署（推荐用于生产）

创建 `docker-compose.yml`:

```yaml
version: '3.8'

services:
  nacos:
    image: nacos/nacos-server:v2.3.0
    container_name: nacos-server
    environment:
      - MODE=standalone
      - PREFER_HOST_MODE=hostname
    ports:
      - "8848:8848"
      - "9848:9848"
    volumes:
      - ./nacos/logs:/home/nacos/logs
      - ./nacos/data:/home/nacos/data
    restart: always
```

启动：
```bash
docker-compose up -d
```

停止：
```bash
docker-compose down
```

---

## 生产环境配置

生产环境建议使用集群模式，需要配置 MySQL 持久化。

参考官方文档：https://nacos.io/zh-cn/docs/deployment.html

---

**文档版本**: v1.0
**最后更新**: 2025-11-28
