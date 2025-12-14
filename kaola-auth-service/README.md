# Kaola Auth Service - 认证授权服务

## 概述

Kaola Auth Service 是考拉推拿微服务架构中的认证授权服务，负责管理员登录、JWT认证、角色和权限管理。

## 核心功能

- **管理员登录**: 用户名密码登录，生成JWT Token
- **JWT认证**: Token生成和验证
- **角色管理**: 角色的增删改查
- **权限管理**: 基于角色的权限控制
- **用户管理**: 管理员用户的基础信息管理

## 技术栈

- Spring Boot 3.2.0
- Spring Cloud Alibaba Nacos 2023.0.1.0
- MyBatis Plus 3.5.9
- MySQL 8.0
- Redis
- JWT (JSON Web Token)
- BCrypt (密码加密)

## 数据库表

### t_admin_user (管理员用户表)
- 用户基本信息（用户名、密码、昵称、头像等）
- 关联角色
- 可选关联门店（门店管理员专用）
- 状态管理

### t_admin_role (管理员角色表)
- 角色名称和描述
- 权限列表（JSON数组）
- 状态管理

## 预置角色

1. **超级管理员**: 拥有所有权限，可管理整个平台
2. **运营管理员**: 负责平台日常运营，可管理所有门店
3. **财务管理员**: 负责财务相关，查看收益和审批提现
4. **门店管理员**: 管理本门店的技师、排班、订单、评价

## API 路径

通过 Gateway 访问时使用以下路径（Gateway会自动转发到本服务）:

- `POST /api/admin/auth/login` - 管理员登录
- `POST /api/admin/auth/logout` - 管理员登出
- `GET /api/admin/auth/userInfo` - 获取当前用户信息
- `GET /api/admin/role/list` - 分页查询角色列表
- `GET /api/admin/role/all` - 获取所有角色（下拉选择用）
- `GET /api/admin/role/detail/{id}` - 获取角色详情
- `POST /api/admin/role/create` - 创建角色
- `PUT /api/admin/role/update` - 更新角色
- `DELETE /api/admin/role/delete/{id}` - 删除角色
- `PUT /api/admin/role/updateStatus` - 更新角色状态

## 前置条件

在启动服务之前，请确保：

1. **MySQL 数据库已启动**
   ```bash
   # 确保 MySQL 运行在 localhost:3306
   # 数据库名称: kaola_massage
   ```

2. **Redis 已启动**
   ```bash
   # 确保 Redis 运行在 localhost:6379
   redis-server
   ```

3. **Nacos 已启动**
   ```bash
   # 确保 Nacos 运行在 localhost:8848
   docker run -d --name nacos-server -p 8848:8848 -p 9848:9848 -e MODE=standalone nacos/nacos-server:v2.3.0
   # 或使用 Homebrew
   brew services start nacos
   ```

4. **初始化数据库**
   ```bash
   # 在MySQL中执行初始化脚本
   mysql -u root -p kaola_massage < src/main/resources/admin-init.sql
   ```

5. **安装 Common 模块**
   ```bash
   cd /Users/icewind/Documents/workspaces/kaola-microservices
   mvn clean install -pl kaola-common -am
   ```

## 启动方式

### 方式 1: Maven 启动（开发环境）

```bash
cd kaola-auth-service
mvn spring-boot:run
```

### 方式 2: IDE 启动

在 IntelliJ IDEA 中：
1. 打开 `AuthServiceApplication.java`
2. 右键选择 "Run" 或 "Debug"

### 方式 3: 打包运行（生产环境）

```bash
# 打包
mvn clean package -DskipTests

# 运行
java -jar target/kaola-auth-service-1.0.0.jar
```

## 验证启动

### 1. 查看启动日志

成功启动后会显示：
```
===================================
  Kaola Auth Service 启动成功！
  端口: 8081
  文档: http://localhost:8081/doc.html
===================================
```

### 2. 验证 Nacos 注册

访问 Nacos 控制台：http://localhost:8848/nacos
- 服务列表中应显示 `kaola-auth-service`

### 3. 访问API文档

访问 Knife4j 文档：http://localhost:8081/doc.html

### 4. 测试登录接口

```bash
# 测试管理员登录
curl -X POST http://localhost:8081/admin/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'

# 预期返回包含token的JSON数据
```

## 默认账号

| 用户名 | 密码 | 角色 | 说明 |
|--------|------|------|------|
| admin | admin123 | 超级管理员 | 拥有所有权限 |
| store_guomao | admin123 | 门店管理员 | 国贸店店长 |
| store_sanlitun | admin123 | 门店管理员 | 三里屯店店长 |
| store_wangjing | admin123 | 门店管理员 | 望京店店长 |

## 配置说明

### 端口配置
- **服务端口**: 8081
- **访问地址**: http://localhost:8081

### 数据库配置

在 `application.yml` 中配置：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/kaola_massage
    username: root
    password: 123456
```

### JWT配置

```yaml
jwt:
  secret: kaola-massage-secret-key-2024-for-admin-authentication
  expiration: 86400000  # 24小时
```

## 依赖服务

本服务依赖以下服务：

- **MySQL**: 存储管理员和角色数据
- **Redis**: Token缓存（可选）
- **Nacos**: 服务注册和配置管理

待 kaola-store-service 创建后，将集成门店信息查询功能（通过 OpenFeign）。

## 常见问题

### 1. 启动失败: 无法连接 Nacos

**问题**: `Unable to connect to Nacos Server`

**解决**:
- 确认 Nacos 已启动: `curl http://localhost:8848/nacos`
- 检查 bootstrap.yml 中的 server-addr 配置

### 2. 启动失败: 无法连接数据库

**问题**: `Cannot create PoolableConnectionFactory`

**解决**:
- 确认 MySQL 已启动且可访问
- 检查数据库名称、用户名、密码是否正确
- 确认已执行 admin-init.sql 初始化脚本

### 3. 登录失败: 用户名或密码错误

**问题**: 使用默认账号登录失败

**解决**:
- 确认已执行数据库初始化脚本
- 检查数据库中是否有管理员用户数据
- 确认密码是否为 `admin123`

### 4. 端口冲突

**问题**: 8081 端口已被占用

**解决**:
```bash
# 方式 1: 修改配置
# 在 application.yml 中修改 server.port

# 方式 2: 停止占用端口的进程
lsof -ti:8081 | xargs kill -9
```

## 开发计划

### 已完成

- [x] 管理员登录认证
- [x] JWT Token 生成和验证
- [x] 角色管理 CRUD
- [x] 密码 BCrypt 加密
- [x] MyBatis Plus 集成
- [x] Swagger/Knife4j 文档

### 待完成

- [ ] 集成 kaola-store-service（OpenFeign 调用获取门店信息）
- [ ] 添加权限验证拦截器
- [ ] Token 刷新机制
- [ ] 登录日志记录
- [ ] 密码重置功能
- [ ] 双因素认证（2FA）

## 测试

```bash
# 运行单元测试
mvn test

# 运行集成测试
mvn verify
```

## 监控

服务暴露了以下 Actuator 端点：

| 端点 | 说明 |
|-----|------|
| `/actuator/health` | 健康检查 |
| `/actuator/info` | 服务信息 |
| `/actuator/metrics` | 性能指标 |

## 相关文档

- [微服务架构设计](../MICROSERVICES_ARCHITECTURE.md)
- [Gateway 配置](../kaola-gateway/README.md)
- [Nacos 部署指南](../NACOS_SETUP_GUIDE.md)

---

**版本**: 1.0.0
**端口**: 8081
**维护团队**: Kaola Team
