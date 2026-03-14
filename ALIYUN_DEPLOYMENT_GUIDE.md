# 考拉推拿预约系统 - 阿里云部署方案

## 📋 目录
- [1. 系统架构概览](#1-系统架构概览)
- [2. 阿里云产品选型](#2-阿里云产品选型)
- [3. 资源配置方案](#3-资源配置方案)
- [4. 成本评估](#4-成本评估)
- [5. 部署架构设计](#5-部署架构设计)
- [6. 部署步骤](#6-部署步骤)
- [7. 监控与运维](#7-监控与运维)
- [8. 优化建议](#8-优化建议)

---

## 1. 系统架构概览

### 1.1 微服务清单

| 服务名称 | 端口 | 职责 | 实例数 |
|---------|------|------|--------|
| kaola-gateway | 8090 | API网关 | 2 |
| kaola-user-service | 8082 | 用户管理 | 2 |
| kaola-store-service | 8083 | 门店管理 | 2 |
| kaola-masseur-service | 8084 | 技师管理 | 2 |
| kaola-product-service | 8085 | 项目/产品管理 | 2 |
| kaola-order-service | 8086 | 订单管理 | 3 |
| kaola-schedule-service | 8091 | 排班管理 | 2 |
| kaola-marketing-service | 8092 | 营销管理 | 2 |
| kaola-review-service | 8093 | 评价管理 | 1 |
| kaola-complaint-service | 8094 | 投诉管理 | 1 |
| kaola-admin-service | 8095 | 管理后台 | 2 |
| kaola-earning-service | 8096 | 收益管理 | 2 |

**总计**: 12个微服务，建议部署23个实例

### 1.2 基础设施

- **服务注册与配置中心**: Nacos
- **数据库**: MySQL 8.0
- **缓存**: Redis
- **对象存储**: 阿里云OSS
- **CDN**: 阿里云CDN（前端静态资源）
- **负载均衡**: 阿里云SLB

### 1.3 前端应用

- **管理后台** (admin-web): Vue.js + Element Plus
- **小程序** (miniprogram-user): 微信小程序

---

## 2. 阿里云产品选型

### 2.1 计算资源

**推荐方案**: 容器服务 Kubernetes (ACK)

**原因**:
- 自动弹性伸缩
- 服务健康检查与自愈
- 灰度发布支持
- 便于管理多个微服务实例
- 降低运维成本

**备选方案**: ECS云服务器 + 自建Kubernetes

### 2.2 数据库

**推荐**: 云数据库RDS MySQL 8.0

**配置建议**:
- 高可用版（主备架构）
- 自动备份
- 读写分离（后期优化）

### 2.3 缓存

**推荐**: 云数据库Redis版

**配置建议**:
- 主从版（双副本）
- 自动容灾
- 性能增强版

### 2.4 负载均衡

**推荐**: 应用型负载均衡ALB

**功能**:
- 七层负载均衡
- SSL卸载
- 健康检查
- 会话保持

### 2.5 存储

**推荐**: 对象存储OSS

**用途**:
- 图片存储（门店、技师、项目图片）
- 用户头像
- 静态资源

### 2.6 CDN

**推荐**: 阿里云CDN

**用途**:
- 管理后台静态资源加速
- OSS图片加速
- 小程序API加速

---

## 3. 资源配置方案

### 3.1 方案A - 初创版（月成本约2,500元）

适合初期测试、小规模运营（日订单量 < 200）

#### 计算资源
- **ECS实例**: 2台 × ecs.c6.xlarge（4核8G）
  - 1台运行所有微服务（轻量部署）
  - 1台运行Nacos + 备用
  - 价格: 2 × ¥400/月 = ¥800/月

#### 数据库
- **RDS MySQL**: 基础版，2核4G，100GB存储
  - 价格: ¥350/月

#### 缓存
- **Redis**: 标准版，1G内存
  - 价格: ¥120/月

#### 负载均衡
- **SLB**: 性能保障型slb.s1.small
  - 价格: ¥150/月

#### 存储
- **OSS**: 标准存储，50GB
  - 价格: ¥10/月

#### CDN
- **CDN**: 流量计费
  - 预估: ¥200/月

#### 带宽
- **公网带宽**: 10Mbps
  - 价格: ¥720/月

#### 其他
- **域名** + **SSL证书**: ¥100/月
- **监控告警**: ¥50/月

**总计**: 约¥2,500/月

---

### 3.2 方案B - 标准版（月成本约6,000元）⭐ 推荐

适合中等规模运营（日订单量 200-1000）

#### 计算资源 - ACK集群
- **ACK托管版**:
  - Worker节点: 3台 × ecs.c6.2xlarge（8核16G）
  - 价格: 3 × ¥850/月 = ¥2,550/月
  - ACK管理费: ¥0（托管版免费）

#### 数据库
- **RDS MySQL**: 高可用版，4核16G，500GB SSD存储
  - 价格: ¥1,200/月
  - **备份**: 500GB × ¥0.5/GB = ¥250/月

#### 缓存
- **Redis**: 主从版，8G内存
  - 价格: ¥600/月

#### 负载均衡
- **ALB**: 应用型负载均衡
  - 实例费: ¥200/月
  - 规格费: ¥300/月
  - LCU费用: ¥200/月

#### 存储
- **OSS**: 标准存储，200GB
  - 存储: ¥40/月
  - 请求: ¥50/月

#### CDN
- **CDN**: 流量计费
  - 预估: ¥500/月

#### 带宽
- **公网带宽**: 50Mbps（弹性）
  - 价格: ¥200/月（按流量计费）

#### 监控与日志
- **日志服务SLS**:
  - 价格: ¥200/月
- **云监控**:
  - 价格: ¥50/月

#### 其他
- **域名** + **SSL证书**: ¥100/月
- **NAT网关**: ¥200/月

**总计**: 约¥6,040/月（年付可享8.5折，约¥61,600/年）

---

### 3.3 方案C - 企业版（月成本约15,000元）

适合大规模运营（日订单量 > 1000）

#### 计算资源 - ACK集群
- **ACK专业版**:
  - Worker节点: 6台 × ecs.c6.4xlarge（16核32G）
  - 价格: 6 × ¥1,650/月 = ¥9,900/月
  - ACK专业版管理费: ¥700/月

#### 数据库
- **RDS MySQL**: 集群版（一主多从），8核32G，2TB SSD存储
  - 主实例: ¥2,500/月
  - 只读实例 × 2: 2 × ¥1,200 = ¥2,400/月
  - 备份: 2TB × ¥0.5/GB = ¥1,000/月

#### 缓存
- **Redis**: 集群版，32G内存
  - 价格: ¥2,400/月

#### 负载均衡
- **ALB**: 高性能
  - 实例费: ¥500/月
  - 规格费: ¥800/月
  - LCU费用: ¥800/月

#### 存储
- **OSS**: 标准存储，1TB
  - 存储: ¥200/月
  - 请求: ¥200/月

#### CDN
- **CDN**: 包年包月
  - 价格: ¥2,000/月

#### 带宽
- **公网带宽**: 200Mbps
  - 价格: ¥1,000/月

#### 监控与日志
- **日志服务SLS**: ¥500/月
- **ARMS应用监控**: ¥600/月
- **云监控**: ¥100/月

#### 安全
- **Web应用防火墙WAF**: ¥2,500/月
- **DDoS防护**: ¥500/月

#### 其他
- **域名** + **高级SSL证书**: ¥300/月
- **NAT网关**: ¥500/月
- **消息队列MQ**: ¥300/月

**总计**: 约¥15,100/月（年付可享7折，约¥126,840/年）

---

## 4. 成本评估

### 4.1 年度成本对比

| 项目 | 初创版 | 标准版⭐ | 企业版 |
|------|--------|---------|--------|
| 月度成本 | ¥2,500 | ¥6,040 | ¥15,100 |
| 年付折扣 | 9折 | 8.5折 | 7折 |
| 年度成本 | ¥27,000 | ¥61,600 | ¥126,840 |
| 适用规模 | <200单/天 | 200-1000单/天 | >1000单/天 |

### 4.2 成本优化建议

1. **预留实例券**
   - 购买1年期或3年期预留实例券可节省30-50%

2. **按量付费资源**
   - 非核心时段可缩减实例数量
   - 使用自动伸缩策略

3. **存储优化**
   - 对不常访问的数据使用低频存储（IA）
   - OSS生命周期管理

4. **CDN优化**
   - 使用包年包月套餐（比流量计费便宜20%）
   - 合理设置缓存规则

5. **带宽优化**
   - 考虑使用NAT网关共享带宽
   - 按流量计费（对于流量不稳定的业务）

### 4.3 隐藏成本

需要额外考虑的成本：
- **备份存储**: RDS备份、快照备份（约10%）
- **跨区域流量**: 如果有多地域部署（约5%）
- **日志存储**: 长期日志存储（约3%）
- **短信服务**: 验证码、通知（约¥300-500/月）
- **人力成本**: 运维人员工资

**建议预算**: 在方案基础上增加15-20%作为缓冲

---

## 5. 部署架构设计

### 5.1 网络架构

```
                          Internet
                              |
                    [阿里云CDN + WAF]
                              |
                    [ALB负载均衡 - 公网]
                              |
        +--------------------+--------------------+
        |                                         |
   [Gateway容器组]                          [Admin-Web]
     (多实例)                               (静态托管)
        |
   [Service Mesh]
        |
   +----+----+----+----+----+----+----+----+
   |    |    |    |    |    |    |    |    |
 [微服务容器组 - 按业务域划分]
   |    |    |    |    |    |    |    |    |
   +---------+---------+---------+-----------+
             |                    |
        [RDS MySQL]          [Redis集群]
        (主备架构)           (主从架构)
             |                    |
        [备份存储]            [持久化]
```

### 5.2 VPC网络规划

**VPC**: kaola-production-vpc (10.0.0.0/16)

**子网规划**:
- **公网子网** (10.0.1.0/24): ALB、NAT网关
- **应用子网** (10.0.10.0/23): K8s节点、微服务
- **数据子网** (10.0.20.0/24): RDS、Redis
- **管理子网** (10.0.30.0/24): 堡垒机、监控

**安全组规则**:
```yaml
# ALB安全组
入站:
  - 80/443 from 0.0.0.0/0  # HTTP/HTTPS公网访问

# K8s节点安全组
入站:
  - 8080-9000 from ALB-SG  # 来自ALB
  - 所有端口 from K8s-SG    # 集群内通信

# RDS安全组
入站:
  - 3306 from K8s-SG       # 仅允许K8s访问

# Redis安全组
入站:
  - 6379 from K8s-SG       # 仅允许K8s访问
```

### 5.3 Kubernetes资源配置

#### 5.3.1 Namespace划分
```yaml
- kaola-gateway      # 网关层
- kaola-business     # 业务服务
- kaola-support      # 支撑服务（Nacos等）
- kaola-monitoring   # 监控组件
```

#### 5.3.2 微服务资源配置示例

**网关服务** (kaola-gateway):
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: kaola-gateway
  namespace: kaola-gateway
spec:
  replicas: 2
  selector:
    matchLabels:
      app: kaola-gateway
  template:
    metadata:
      labels:
        app: kaola-gateway
    spec:
      containers:
      - name: gateway
        image: registry.cn-hangzhou.aliyuncs.com/kaola/gateway:latest
        ports:
        - containerPort: 8090
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: "prod"
        - name: NACOS_SERVER_ADDR
          value: "nacos.kaola-support:8848"
        resources:
          requests:
            cpu: 500m
            memory: 1Gi
          limits:
            cpu: 2000m
            memory: 2Gi
        livenessProbe:
          httpGet:
            path: /actuator/health
            port: 8090
          initialDelaySeconds: 60
          periodSeconds: 10
        readinessProbe:
          httpGet:
            path: /actuator/health
            port: 8090
          initialDelaySeconds: 30
          periodSeconds: 5
---
apiVersion: v1
kind: Service
metadata:
  name: kaola-gateway
  namespace: kaola-gateway
spec:
  type: ClusterIP
  ports:
  - port: 8090
    targetPort: 8090
  selector:
    app: kaola-gateway
---
apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
  name: kaola-gateway-hpa
  namespace: kaola-gateway
spec:
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: kaola-gateway
  minReplicas: 2
  maxReplicas: 10
  metrics:
  - type: Resource
    resource:
      name: cpu
      target:
        type: Utilization
        averageUtilization: 70
  - type: Resource
    resource:
      name: memory
      target:
        type: Utilization
        averageUtilization: 80
```

**业务服务** (以order-service为例):
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: kaola-order-service
  namespace: kaola-business
spec:
  replicas: 3  # 订单服务建议3个副本
  selector:
    matchLabels:
      app: kaola-order-service
  template:
    metadata:
      labels:
        app: kaola-order-service
    spec:
      containers:
      - name: order-service
        image: registry.cn-hangzhou.aliyuncs.com/kaola/order-service:latest
        ports:
        - containerPort: 8086
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: "prod"
        - name: NACOS_SERVER_ADDR
          value: "nacos.kaola-support:8848"
        - name: MYSQL_HOST
          valueFrom:
            configMapKeyRef:
              name: db-config
              key: mysql.host
        - name: MYSQL_PASSWORD
          valueFrom:
            secretKeyRef:
              name: db-secret
              key: mysql.password
        - name: REDIS_HOST
          valueFrom:
            configMapKeyRef:
              name: redis-config
              key: redis.host
        resources:
          requests:
            cpu: 300m
            memory: 512Mi
          limits:
            cpu: 1000m
            memory: 1Gi
        livenessProbe:
          httpGet:
            path: /actuator/health
            port: 8086
          initialDelaySeconds: 90
          periodSeconds: 10
        readinessProbe:
          httpGet:
            path: /actuator/health/readiness
            port: 8086
          initialDelaySeconds: 60
          periodSeconds: 5
```

#### 5.3.3 ConfigMap配置

```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: db-config
  namespace: kaola-business
data:
  mysql.host: "rm-xxx.mysql.rds.aliyuncs.com"
  mysql.port: "3306"
  mysql.database: "kaola_db"
---
apiVersion: v1
kind: ConfigMap
metadata:
  name: redis-config
  namespace: kaola-business
data:
  redis.host: "r-xxx.redis.rds.aliyuncs.com"
  redis.port: "6379"
---
apiVersion: v1
kind: ConfigMap
metadata:
  name: oss-config
  namespace: kaola-business
data:
  oss.endpoint: "https://oss-cn-hangzhou.aliyuncs.com"
  oss.bucket: "kaola-images"
```

#### 5.3.4 Secret配置

```yaml
apiVersion: v1
kind: Secret
metadata:
  name: db-secret
  namespace: kaola-business
type: Opaque
data:
  mysql.username: cm9vdA==  # base64编码
  mysql.password: eW91cnBhc3N3b3Jk  # base64编码
---
apiVersion: v1
kind: Secret
metadata:
  name: redis-secret
  namespace: kaola-business
type: Opaque
data:
  redis.password: cmVkaXNwYXNzd29yZA==
---
apiVersion: v1
kind: Secret
metadata:
  name: oss-secret
  namespace: kaola-business
type: Opaque
data:
  oss.accessKeyId: QUtJRHh4eHh4
  oss.accessKeySecret: c2VjcmV0eHh4eA==
```

---

## 6. 部署步骤

### 6.1 准备阶段

#### 6.1.1 创建阿里云账号并实名认证
```bash
# 1. 注册阿里云账号
https://account.aliyun.com/register

# 2. 完成实名认证（个人或企业）
https://account.console.aliyun.com/v2/#/authc/home

# 3. 充值（建议预充值3-6个月费用）
```

#### 6.1.2 开通必要的云产品
```bash
# 以下产品需要在控制台开通：
1. 容器服务ACK
2. 云数据库RDS MySQL
3. 云数据库Redis
4. 负载均衡ALB/SLB
5. 对象存储OSS
6. CDN
7. 日志服务SLS
8. 容器镜像服务ACR
```

#### 6.1.3 创建VPC和子网
```bash
# 1. 创建VPC
aliyun vpc CreateVpc \
  --RegionId cn-hangzhou \
  --CidrBlock 10.0.0.0/16 \
  --VpcName kaola-production-vpc

# 2. 创建交换机（子网）
# 公网子网
aliyun vpc CreateVSwitch \
  --VpcId vpc-xxx \
  --ZoneId cn-hangzhou-h \
  --CidrBlock 10.0.1.0/24 \
  --VSwitchName public-subnet

# 应用子网
aliyun vpc CreateVSwitch \
  --VpcId vpc-xxx \
  --ZoneId cn-hangzhou-h \
  --CidrBlock 10.0.10.0/23 \
  --VSwitchName app-subnet

# 数据子网
aliyun vpc CreateVSwitch \
  --VpcId vpc-xxx \
  --ZoneId cn-hangzhou-h \
  --CidrBlock 10.0.20.0/24 \
  --VSwitchName data-subnet
```

### 6.2 基础设施部署

#### 6.2.1 创建ACK集群

**方式一：控制台创建**
1. 登录容器服务管理控制台
2. 选择 "集群" > "创建集群" > "标准托管集群"
3. 配置：
   - 地域：华东1（杭州）
   - 可用区：cn-hangzhou-h
   - VPC：选择已创建的VPC
   - Worker节点：3台 ecs.c6.2xlarge
   - 登录方式：密钥对
   - Pod网络CIDR：172.20.0.0/16
   - Service网络CIDR：172.21.0.0/20

**方式二：命令行创建**
```bash
# 安装aliyun CLI
brew install aliyun-cli  # macOS
# 或
wget https://aliyuncli.alicdn.com/aliyun-cli-linux-latest-amd64.tgz

# 配置认证
aliyun configure

# 创建ACK集群
aliyun cs POST /clusters \
  --header "Content-Type=application/json" \
  --body '{
    "cluster_type": "ManagedKubernetes",
    "name": "kaola-production",
    "region_id": "cn-hangzhou",
    "kubernetes_version": "1.26.3-aliyun.1",
    "vpcid": "vpc-xxx",
    "vswitchids": ["vsw-xxx"],
    "worker_instance_types": ["ecs.c6.2xlarge"],
    "num_of_nodes": 3,
    "worker_system_disk_category": "cloud_essd",
    "worker_system_disk_size": 120
  }'

# 获取kubeconfig
aliyun cs GET /k8s/{ClusterId}/user_config > ~/.kube/config
```

#### 6.2.2 部署RDS MySQL

```bash
# 1. 创建RDS实例
aliyun rds CreateDBInstance \
  --RegionId cn-hangzhou \
  --Engine MySQL \
  --EngineVersion 8.0 \
  --DBInstanceClass rds.mysql.s3.large \
  --DBInstanceStorage 500 \
  --DBInstanceNetType Intranet \
  --DBInstanceStorageType cloud_essd \
  --PayType Postpaid \
  --SecurityIPList "10.0.10.0/23" \
  --VPCId vpc-xxx \
  --VSwitchId vsw-xxx

# 2. 创建数据库账号
aliyun rds CreateAccount \
  --DBInstanceId rm-xxx \
  --AccountName kaola_admin \
  --AccountPassword 'YourStrongPassword123!' \
  --AccountType Super

# 3. 创建数据库
aliyun rds CreateDatabase \
  --DBInstanceId rm-xxx \
  --DBName kaola_db \
  --CharacterSetName utf8mb4

# 4. 导入初始数据
mysql -h rm-xxx.mysql.rds.aliyuncs.com \
  -u kaola_admin \
  -p \
  kaola_db < ./sql/init-schema.sql
```

#### 6.2.3 部署Redis

```bash
# 创建Redis实例
aliyun r-kvstore CreateInstance \
  --RegionId cn-hangzhou \
  --InstanceClass redis.master.small.default \
  --InstanceName kaola-redis \
  --Password 'YourRedisPassword123!' \
  --VpcId vpc-xxx \
  --VSwitchId vsw-xxx \
  --PrivateIpAddress 10.0.20.10
```

#### 6.2.4 创建OSS Bucket

```bash
# 1. 创建Bucket
aliyun oss mb oss://kaola-images \
  --storage-class Standard \
  --acl public-read

# 2. 配置CORS
cat > cors.xml << EOF
<?xml version="1.0" encoding="UTF-8"?>
<CORSConfiguration>
  <CORSRule>
    <AllowedOrigin>*</AllowedOrigin>
    <AllowedMethod>GET</AllowedMethod>
    <AllowedMethod>POST</AllowedMethod>
    <AllowedMethod>PUT</AllowedMethod>
    <AllowedHeader>*</AllowedHeader>
  </CORSRule>
</CORSConfiguration>
EOF

aliyun oss cors --method put \
  oss://kaola-images \
  cors.xml

# 3. 配置生命周期（30天后转低频存储）
cat > lifecycle.xml << EOF
<?xml version="1.0" encoding="UTF-8"?>
<LifecycleConfiguration>
  <Rule>
    <ID>转换为IA</ID>
    <Prefix>images/</Prefix>
    <Status>Enabled</Status>
    <Transition>
      <Days>30</Days>
      <StorageClass>IA</StorageClass>
    </Transition>
  </Rule>
</LifecycleConfiguration>
EOF

aliyun oss lifecycle --method put \
  oss://kaola-images \
  lifecycle.xml
```

### 6.3 应用部署

#### 6.3.1 构建Docker镜像

```bash
# 1. 登录阿里云容器镜像服务
docker login --username=your-email@example.com \
  registry.cn-hangzhou.aliyuncs.com

# 2. 构建所有服务镜像
#!/bin/bash
REGISTRY="registry.cn-hangzhou.aliyuncs.com/kaola"
VERSION="1.0.0"

services=(
  "gateway"
  "user-service"
  "store-service"
  "masseur-service"
  "product-service"
  "order-service"
  "schedule-service"
  "marketing-service"
  "review-service"
  "complaint-service"
  "admin-service"
  "earning-service"
)

for service in "${services[@]}"; do
  echo "构建 kaola-${service}..."
  cd kaola-${service}

  # Maven打包
  mvn clean package -DskipTests

  # 构建Docker镜像
  docker build -t ${REGISTRY}/${service}:${VERSION} .
  docker tag ${REGISTRY}/${service}:${VERSION} ${REGISTRY}/${service}:latest

  # 推送镜像
  docker push ${REGISTRY}/${service}:${VERSION}
  docker push ${REGISTRY}/${service}:latest

  cd ..
done
```

**Dockerfile示例** (每个服务类似):
```dockerfile
FROM openjdk:17-jdk-slim

WORKDIR /app

# 复制jar包
COPY target/*.jar app.jar

# 暴露端口
EXPOSE 8080

# JVM参数优化
ENV JAVA_OPTS="-Xms512m -Xmx1g -XX:+UseG1GC -XX:+HeapDumpOnOutOfMemoryError"

# 启动命令
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
```

#### 6.3.2 部署Nacos

```bash
# 使用Helm部署Nacos
helm repo add nacos https://nacos-group.github.io/nacos-k8s/

helm install nacos nacos/nacos \
  --namespace kaola-support \
  --create-namespace \
  --set replicaCount=3 \
  --set persistence.enabled=true \
  --set persistence.data.storageClassName=alicloud-disk-essd \
  --set mysql.host=rm-xxx.mysql.rds.aliyuncs.com \
  --set mysql.port=3306 \
  --set mysql.database=nacos_config \
  --set mysql.username=nacos \
  --set mysql.password=nacos123
```

#### 6.3.3 部署微服务

```bash
# 1. 创建命名空间
kubectl create namespace kaola-gateway
kubectl create namespace kaola-business

# 2. 创建ConfigMap和Secret
kubectl apply -f k8s/configmaps/
kubectl apply -f k8s/secrets/

# 3. 部署服务
kubectl apply -f k8s/deployments/gateway/
kubectl apply -f k8s/deployments/services/

# 4. 验证部署
kubectl get pods -n kaola-gateway
kubectl get pods -n kaola-business

# 5. 查看日志
kubectl logs -f deployment/kaola-gateway -n kaola-gateway
```

#### 6.3.4 配置Ingress

```yaml
# k8s/ingress/alb-ingress.yaml
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: kaola-ingress
  namespace: kaola-gateway
  annotations:
    kubernetes.io/ingress.class: "alb"
    alb.ingress.kubernetes.io/scheme: "internet-facing"
    alb.ingress.kubernetes.io/target-type: "ip"
    alb.ingress.kubernetes.io/backend-protocol: "HTTP"
    alb.ingress.kubernetes.io/healthcheck-enabled: "true"
    alb.ingress.kubernetes.io/healthcheck-path: "/actuator/health"
    alb.ingress.kubernetes.io/certificate-arn: "arn:acs:slb:cn-hangzhou:xxx:certificate/xxx"
spec:
  tls:
  - hosts:
    - api.kaola-massage.com
    secretName: kaola-tls-secret
  rules:
  - host: api.kaola-massage.com
    http:
      paths:
      - path: /
        pathType: Prefix
        backend:
          service:
            name: kaola-gateway
            port:
              number: 8090
```

```bash
# 应用Ingress
kubectl apply -f k8s/ingress/alb-ingress.yaml

# 获取ALB地址
kubectl get ingress -n kaola-gateway
```

### 6.4 前端部署

#### 6.4.1 管理后台 (admin-web)

```bash
# 1. 构建生产版本
cd admin-web
npm run build

# 2. 上传到OSS
aliyun oss cp -r dist/ oss://kaola-admin-web/ \
  --update \
  --recursive

# 3. 配置静态网站托管
aliyun oss website --method put \
  oss://kaola-admin-web \
  index.html \
  error.html

# 4. 配置CDN加速
aliyun cdn AddCdnDomain \
  --DomainName admin.kaola-massage.com \
  --CdnType web \
  --SourceType oss \
  --SourcePort 80 \
  --SourceProtocol http \
  --Sources '[{"content":"kaola-admin-web.oss-cn-hangzhou.aliyuncs.com","type":"oss","priority":"20"}]'
```

#### 6.4.2 小程序

```bash
# 1. 配置生产环境API地址
# miniprogram-user/utils/config.js
const config = {
  apiUrl: 'https://api.kaola-massage.com',
  ossUrl: 'https://cdn.kaola-massage.com'
}

# 2. 使用微信开发者工具上传代码
# 3. 在微信公众平台提交审核
# 4. 审核通过后发布
```

### 6.5 域名与SSL配置

```bash
# 1. 在阿里云购买域名
# kaola-massage.com

# 2. 申请SSL证书
aliyun cas CreateCertificateRequest \
  --ProductCode cas \
  --Domain "*.kaola-massage.com" \
  --ValidateType DNS

# 3. 配置域名解析
# API域名
aliyun alidns AddDomainRecord \
  --DomainName kaola-massage.com \
  --RR api \
  --Type CNAME \
  --Value xxx.cn-hangzhou.alb.aliyuncs.com

# 管理后台域名
aliyun alidns AddDomainRecord \
  --DomainName kaola-massage.com \
  --RR admin \
  --Type CNAME \
  --Value xxx.w.kunluncan.com  # CDN域名

# OSS CDN域名
aliyun alidns AddDomainRecord \
  --DomainName kaola-massage.com \
  --RR cdn \
  --Type CNAME \
  --Value xxx.w.kunluncan.com
```

---

## 7. 监控与运维

### 7.1 监控体系

#### 7.1.1 基础监控（云监控）

```bash
# 监控项：
- ECS/ACK节点：CPU、内存、磁盘、网络
- RDS：连接数、QPS、慢查询、锁等待
- Redis：内存使用、命中率、QPS
- ALB：请求数、响应时间、5xx错误率
```

**告警规则配置**:
```yaml
# CPU使用率告警
规则名称: ACK-CPU-High
监控项: CPUUtilization
统计方法: Average
比较符: >=
阈值: 80%
连续次数: 3
通知方式: 短信 + 邮件 + 钉钉

# 内存使用率告警
规则名称: ACK-Memory-High
监控项: MemoryUtilization
统计方法: Average
比较符: >=
阈值: 85%
连续次数: 3
通知方式: 短信 + 邮件 + 钉钉

# 数据库连接数告警
规则名称: RDS-Connection-High
监控项: ConnectionUsage
统计方法: Average
比较符: >=
阈值: 80%
连续次数: 2
通知方式: 短信 + 邮件

# ALB 5xx错误率告警
规则名称: ALB-5xx-High
监控项: StatusCode5xx
统计方法: Sum
比较符: >=
阈值: 100
连续次数: 1
通知方式: 短信 + 邮件 + 钉钉
```

#### 7.1.2 应用监控（ARMS）

```bash
# 1. 开通ARMS应用监控
# 2. 在pom.xml添加探针依赖
<dependency>
    <groupId>com.alibaba.arms.apm</groupId>
    <artifactId>arms-agent-attach</artifactId>
    <version>1.7.3</version>
</dependency>

# 3. 在Dockerfile中添加Agent
ENV JAVA_AGENT_PATH=/app/arms-agent.jar
ENV ARMS_APP_NAME=kaola-order-service
ENV ARMS_LICENSE_KEY=xxx

ENTRYPOINT ["sh", "-c", "java -javaagent:${JAVA_AGENT_PATH} -Darms.appName=${ARMS_APP_NAME} -Darms.licenseKey=${ARMS_LICENSE_KEY} $JAVA_OPTS -jar app.jar"]
```

**监控指标**:
- 应用吞吐量（TPS）
- 平均响应时间（RT）
- 错误率
- SQL调用统计
- 外部调用统计
- JVM监控（堆内存、GC）

#### 7.1.3 日志监控（SLS）

**日志采集配置**:
```yaml
# 1. 创建Logstore
Project: kaola-logs
Logstore:
  - application-logs  # 应用日志
  - access-logs       # 访问日志
  - error-logs        # 错误日志
  - slow-sql-logs     # 慢SQL日志

# 2. 配置Logtail采集
# 安装Logtail DaemonSet
kubectl apply -f https://raw.githubusercontent.com/AliyunContainerService/log-pilot/master/assets/log-pilot.yaml

# 3. 配置日志采集注解
apiVersion: apps/v1
kind: Deployment
metadata:
  name: kaola-order-service
spec:
  template:
    metadata:
      annotations:
        aliyun.logs.stdout: "kaola-logs/application-logs"
        aliyun.logs.errorlog: "kaola-logs/error-logs"
```

**日志分析查询**:
```sql
-- 错误日志统计
* | SELECT
  date_format(__time__, '%Y-%m-%d %H:%i') as time,
  COUNT(*) as error_count
FROM log
WHERE level = 'ERROR'
GROUP BY time
ORDER BY time DESC

-- 慢接口分析
* | SELECT
  request_uri,
  AVG(request_time) as avg_time,
  MAX(request_time) as max_time,
  COUNT(*) as count
WHERE request_time > 1000
GROUP BY request_uri
ORDER BY avg_time DESC
LIMIT 20

-- 异常统计
* | SELECT
  exception_class,
  COUNT(*) as count
WHERE level = 'ERROR'
GROUP BY exception_class
ORDER BY count DESC
```

### 7.2 备份策略

#### 7.2.1 数据库备份

```bash
# RDS自动备份配置
aliyun rds ModifyBackupPolicy \
  --DBInstanceId rm-xxx \
  --PreferredBackupTime "02:00Z-03:00Z" \
  --PreferredBackupPeriod "Monday,Tuesday,Wednesday,Thursday,Friday,Saturday,Sunday" \
  --BackupRetentionPeriod 30 \
  --LogBackupRetentionPeriod 7

# 手动备份脚本
#!/bin/bash
DATE=$(date +%Y%m%d)
BACKUP_DIR="/data/backups/mysql"

# 创建备份
mysqldump -h rm-xxx.mysql.rds.aliyuncs.com \
  -u kaola_admin \
  -p'password' \
  --all-databases \
  --single-transaction \
  --quick \
  --lock-tables=false \
  > ${BACKUP_DIR}/kaola_db_${DATE}.sql

# 压缩
gzip ${BACKUP_DIR}/kaola_db_${DATE}.sql

# 上传到OSS
aliyun oss cp ${BACKUP_DIR}/kaola_db_${DATE}.sql.gz \
  oss://kaola-backups/mysql/

# 删除本地备份（保留7天）
find ${BACKUP_DIR} -name "*.sql.gz" -mtime +7 -delete
```

#### 7.2.2 Redis备份

```bash
# Redis自动备份（已包含在Redis实例中）
# 手动触发备份
aliyun r-kvstore CreateBackup \
  --InstanceId r-xxx

# 下载备份
aliyun r-kvstore DescribeBackups \
  --InstanceId r-xxx

# 备份恢复
aliyun r-kvstore RestoreInstance \
  --InstanceId r-xxx \
  --BackupId backup-xxx
```

#### 7.2.3 OSS数据备份

```bash
# 跨区域复制（容灾）
aliyun oss replication --method put \
  oss://kaola-images \
  replication.xml

# replication.xml内容
<?xml version="1.0" encoding="UTF-8"?>
<ReplicationConfiguration>
  <Rule>
    <ID>backup-to-beijing</ID>
    <Prefix></Prefix>
    <Destination>
      <Bucket>kaola-images-backup</Bucket>
      <Location>oss-cn-beijing</Location>
    </Destination>
    <HistoricalObjectReplication>enabled</HistoricalObjectReplication>
  </Rule>
</ReplicationConfiguration>
```

### 7.3 灾难恢复

#### 7.3.1 应用恢复

```bash
# 1. 回滚到上一版本
kubectl rollout undo deployment/kaola-order-service -n kaola-business

# 2. 恢复到指定版本
kubectl rollout undo deployment/kaola-order-service -n kaola-business --to-revision=3

# 3. 查看回滚状态
kubectl rollout status deployment/kaola-order-service -n kaola-business
```

#### 7.3.2 数据库恢复

```bash
# 1. 恢复到指定时间点（PITR）
aliyun rds RestoreDBInstance \
  --DBInstanceId rm-xxx \
  --BackupId backup-xxx \
  --RestoreTime "2024-01-01T12:00:00Z"

# 2. 克隆实例
aliyun rds CloneDBInstance \
  --DBInstanceId rm-xxx \
  --BackupId backup-xxx \
  --PayType Postpaid
```

### 7.4 自动化运维

#### 7.4.1 自动扩缩容

**HPA配置** (已在6.3节配置):
```yaml
# CPU/内存自动扩缩容
apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
  name: kaola-order-service-hpa
  namespace: kaola-business
spec:
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: kaola-order-service
  minReplicas: 2
  maxReplicas: 10
  metrics:
  - type: Resource
    resource:
      name: cpu
      target:
        type: Utilization
        averageUtilization: 70
  - type: Resource
    resource:
      name: memory
      target:
        type: Utilization
        averageUtilization: 80
  behavior:
    scaleDown:
      stabilizationWindowSeconds: 300  # 缩容稳定窗口5分钟
      policies:
      - type: Percent
        value: 50
        periodSeconds: 60
    scaleUp:
      stabilizationWindowSeconds: 0    # 立即扩容
      policies:
      - type: Percent
        value: 100
        periodSeconds: 30
```

**定时扩缩容** (应对业务高峰):
```yaml
# 使用CronHPA
apiVersion: autoscaling.alibabacloud.com/v1beta1
kind: CronHorizontalPodAutoscaler
metadata:
  name: kaola-order-service-cron-hpa
  namespace: kaola-business
spec:
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: kaola-order-service
  jobs:
  - name: scale-up-evening
    schedule: "0 18 * * *"  # 每天18:00扩容
    targetSize: 5
  - name: scale-down-night
    schedule: "0 1 * * *"   # 每天凌晨1:00缩容
    targetSize: 2
```

#### 7.4.2 健康检查

```yaml
# Liveness Probe（存活探针）
livenessProbe:
  httpGet:
    path: /actuator/health/liveness
    port: 8080
  initialDelaySeconds: 60
  periodSeconds: 10
  timeoutSeconds: 5
  failureThreshold: 3

# Readiness Probe（就绪探针）
readinessProbe:
  httpGet:
    path: /actuator/health/readiness
    port: 8080
  initialDelaySeconds: 30
  periodSeconds: 5
  timeoutSeconds: 3
  failureThreshold: 3
```

#### 7.4.3 滚动更新

```yaml
# Deployment更新策略
spec:
  strategy:
    type: RollingUpdate
    rollingUpdate:
      maxSurge: 1        # 最多可以多1个Pod
      maxUnavailable: 0  # 最少不可用Pod数
```

**灰度发布**:
```bash
# 1. 创建Canary版本
kubectl apply -f k8s/deployments/services/order-service-canary.yaml

# 2. 配置流量分配（使用Istio或Nginx）
# 90%流量到稳定版本，10%流量到Canary版本

# 3. 观察指标（错误率、响应时间）

# 4. 全量发布或回滚
kubectl apply -f k8s/deployments/services/order-service-v2.yaml
```

---

## 8. 优化建议

### 8.1 性能优化

#### 8.1.1 数据库优化

**慢查询优化**:
```sql
-- 1. 开启慢查询日志
SET GLOBAL slow_query_log = 'ON';
SET GLOBAL long_query_time = 1;

-- 2. 创建必要索引
-- 订单表
CREATE INDEX idx_user_id_create_time ON t_order(user_id, create_time);
CREATE INDEX idx_status_create_time ON t_order(status, create_time);

-- 预约表
CREATE INDEX idx_store_masseur_time ON t_appointment(store_id, masseur_id, appointment_time);

-- 3. 分区表（历史订单）
ALTER TABLE t_order PARTITION BY RANGE (YEAR(create_time)) (
  PARTITION p2023 VALUES LESS THAN (2024),
  PARTITION p2024 VALUES LESS THAN (2025),
  PARTITION p2025 VALUES LESS THAN (2026)
);
```

**读写分离**:
```yaml
# application-prod.yml
spring:
  datasource:
    dynamic:
      primary: master
      datasource:
        master:
          url: jdbc:mysql://rm-xxx-master.mysql.rds.aliyuncs.com:3306/kaola_db
          username: kaola_admin
          password: ${DB_PASSWORD}
        slave1:
          url: jdbc:mysql://rm-xxx-readonly1.mysql.rds.aliyuncs.com:3306/kaola_db
          username: kaola_readonly
          password: ${DB_PASSWORD}
        slave2:
          url: jdbc:mysql://rm-xxx-readonly2.mysql.rds.aliyuncs.com:3306/kaola_db
          username: kaola_readonly
          password: ${DB_PASSWORD}
```

#### 8.1.2 缓存优化

**多级缓存**:
```java
@Configuration
public class CacheConfig {

    // L1缓存：本地缓存（Caffeine）
    @Bean
    public Cache<String, Object> localCache() {
        return Caffeine.newBuilder()
            .maximumSize(10_000)
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .build();
    }

    // L2缓存：Redis
    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        // Redis配置
    }
}

// 热点数据缓存
@Cacheable(value = "stores", key = "#storeId", unless = "#result == null")
public Store getStoreById(Long storeId) {
    return storeMapper.selectById(storeId);
}
```

**缓存预热**:
```java
@Component
public class CacheWarmer {

    @PostConstruct
    @Scheduled(cron = "0 0 6 * * ?")  // 每天6点预热
    public void warmUpCache() {
        // 预热热门门店
        List<Store> hotStores = storeService.getHotStores();
        hotStores.forEach(store ->
            redisTemplate.opsForValue().set(
                "store:" + store.getId(),
                store,
                1,
                TimeUnit.HOURS
            )
        );

        // 预热热门项目
        List<Product> hotProducts = productService.getHotProducts();
        // ...
    }
}
```

#### 8.1.3 CDN优化

```bash
# 1. 开启Gzip压缩
aliyun cdn SetOptimizeConfig \
  --DomainName cdn.kaola-massage.com \
  --Enable on

# 2. 配置缓存规则
# 静态资源缓存30天
Path: *.js,*.css,*.jpg,*.png
TTL: 2592000

# HTML文件缓存1小时
Path: *.html
TTL: 3600

# 3. 预热热点资源
aliyun cdn PushObjectCache \
  --ObjectPath https://cdn.kaola-massage.com/assets/logo.png
```

### 8.2 安全加固

#### 8.2.1 网络安全

```bash
# 1. 配置Web应用防火墙（WAF）
- SQL注入防护
- XSS攻击防护
- CC攻击防护
- 爬虫防护

# 2. 配置DDoS防护
- 基础防护：免费（5Gbps）
- DDoS高防：按需开通

# 3. 安全组规则
# 只允许ALB访问应用层
入站规则：
- 源地址：ALB安全组
- 端口：8080-8096
- 协议：TCP

# 只允许应用层访问数据库
入站规则：
- 源地址：应用安全组
- 端口：3306
- 协议：TCP
```

#### 8.2.2 应用安全

**JWT Token**:
```java
@Configuration
public class SecurityConfig {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private Long jwtExpiration;  // 2小时

    // Token签发
    public String generateToken(User user) {
        return Jwts.builder()
            .setSubject(user.getId().toString())
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
            .signWith(SignatureAlgorithm.HS512, jwtSecret)
            .compact();
    }

    // Token验证
    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
```

**API限流**:
```java
@Configuration
public class RateLimitConfig {

    @Bean
    public RedisRateLimiter rateLimiter() {
        return new RedisRateLimiter(
            100,  // replenishRate: 每秒填充100个令牌
            200   // burstCapacity: 桶容量200
        );
    }
}

// Gateway中配置
spring:
  cloud:
    gateway:
      routes:
        - id: order-service
          filters:
            - name: RequestRateLimiter
              args:
                redis-rate-limiter.replenishRate: 100
                redis-rate-limiter.burstCapacity: 200
                key-resolver: "#{@userKeyResolver}"
```

**敏感信息加密**:
```java
// 数据库字段加密
@Component
public class EncryptionHandler implements TypeHandler<String> {

    @Autowired
    private AESEncryption aesEncryption;

    @Override
    public void setParameter(PreparedStatement ps, int i, String parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, aesEncryption.encrypt(parameter));
    }

    @Override
    public String getResult(ResultSet rs, String columnName) throws SQLException {
        return aesEncryption.decrypt(rs.getString(columnName));
    }
}

// 在实体类中使用
@TableField(typeHandler = EncryptionHandler.class)
private String phone;  // 手机号加密存储
```

### 8.3 成本优化

#### 8.3.1 资源优化

**按量付费 + 预留实例**:
```bash
# 1. 核心服务使用预留实例（节省30-50%）
- Gateway: 预留2台实例
- Order Service: 预留2台实例
- User Service: 预留2台实例

# 2. 其他服务按量付费
- 根据负载自动扩缩容
- 非高峰期自动缩容

# 3. 使用Spot实例（抢占式实例）
- 测试环境使用Spot实例（节省90%）
- 非核心服务使用Spot实例
```

**定时任务优化**:
```yaml
# 非业务高峰期执行
apiVersion: batch/v1
kind: CronJob
metadata:
  name: data-cleanup
spec:
  schedule: "0 3 * * *"  # 凌晨3点执行
  jobTemplate:
    spec:
      template:
        spec:
          containers:
          - name: cleanup
            image: kaola/data-cleanup:latest
            resources:
              requests:
                cpu: 100m
                memory: 256Mi
```

#### 8.3.2 存储优化

```bash
# 1. OSS生命周期管理
- 30天后转低频存储（节省50%）
- 90天后转归档存储（节省80%）
- 365天后删除

# 2. 图片压缩
- 使用OSS图片处理服务
- 自动压缩、格式转换
- WebP格式（节省30-50%体积）

# 3. 日志优化
- SLS日志保留30天
- 重要日志归档到OSS
- 使用日志清理策略
```

#### 8.3.3 带宽优化

```bash
# 1. 使用CDN
- 减少源站带宽消耗
- CDN流量费用更低

# 2. 共享带宽包
- 多个ECS共享带宽
- 按实际用量计费

# 3. 启用Gzip压缩
- 减少传输数据量
- 降低带宽成本
```

---

## 9. 常见问题与解决方案

### 9.1 部署问题

**Q1: Pod启动失败，报CrashLoopBackOff**

A: 检查步骤：
```bash
# 1. 查看Pod详情
kubectl describe pod <pod-name> -n <namespace>

# 2. 查看容器日志
kubectl logs <pod-name> -n <namespace>

# 3. 常见原因：
- 镜像拉取失败：检查镜像地址和权限
- 配置错误：检查ConfigMap和Secret
- 资源不足：检查节点资源和资源限制
- 健康检查失败：调整initialDelaySeconds
```

**Q2: 服务无法访问RDS/Redis**

A: 检查网络配置：
```bash
# 1. 检查安全组规则
- RDS安全组是否允许K8s节点IP
- Redis白名单是否包含K8s节点IP

# 2. 测试连接
kubectl run -it --rm debug --image=mysql:8.0 --restart=Never -- \
  mysql -h rm-xxx.mysql.rds.aliyuncs.com -u kaola_admin -p

# 3. 检查DNS解析
kubectl run -it --rm debug --image=busybox --restart=Never -- \
  nslookup rm-xxx.mysql.rds.aliyuncs.com
```

### 9.2 性能问题

**Q3: 接口响应慢**

A: 排查步骤：
```bash
# 1. 查看ARMS应用监控
- 慢接口追踪
- SQL调用分析
- 外部调用分析

# 2. 查看数据库慢查询
SELECT * FROM mysql.slow_log
WHERE query_time > 1
ORDER BY query_time DESC
LIMIT 10;

# 3. 查看Redis性能
redis-cli --latency
redis-cli --stat

# 4. 优化措施
- 添加索引
- 优化SQL
- 增加缓存
- 扩容实例
```

**Q4: 数据库连接池耗尽**

A: 优化连接池配置：
```yaml
spring:
  datasource:
    hikari:
      minimum-idle: 10
      maximum-pool-size: 50
      connection-timeout: 30000
      idle-timeout: 600000
      max-lifetime: 1800000
```

### 9.3 运维问题

**Q5: 如何快速回滚？**

A: 回滚步骤：
```bash
# 1. 查看历史版本
kubectl rollout history deployment/kaola-order-service -n kaola-business

# 2. 回滚到上一版本
kubectl rollout undo deployment/kaola-order-service -n kaola-business

# 3. 回滚到指定版本
kubectl rollout undo deployment/kaola-order-service \
  -n kaola-business \
  --to-revision=3

# 4. 验证回滚状态
kubectl rollout status deployment/kaola-order-service -n kaola-business
```

**Q6: 如何处理数据库锁等待？**

A: 排查和处理：
```sql
-- 1. 查看当前锁等待
SELECT * FROM information_schema.innodb_lock_waits;

-- 2. 查看锁定的事务
SELECT * FROM information_schema.innodb_trx
WHERE trx_state = 'LOCK WAIT';

-- 3. 杀死锁定的进程（谨慎操作）
KILL <trx_mysql_thread_id>;

-- 4. 预防措施
- 优化事务大小
- 减少事务持有时间
- 使用合适的隔离级别
```

---

## 10. 总结

### 10.1 推荐方案

根据业务规模，推荐采用**方案B - 标准版**：
- **月度成本**: ¥6,040
- **年度成本**: ¥61,600（年付8.5折）
- **适用规模**: 日订单量200-1000
- **高可用**: RDS主备、Redis主从、多副本部署
- **可扩展**: 支持自动弹性伸缩

### 10.2 实施路线图

**Phase 1: 基础设施搭建（1-2周）**
- 创建VPC、子网、安全组
- 部署ACK集群
- 部署RDS MySQL、Redis
- 创建OSS Bucket

**Phase 2: 应用部署（2-3周）**
- 构建Docker镜像
- 部署Nacos
- 部署微服务
- 配置Ingress和负载均衡

**Phase 3: 前端部署（1周）**
- 部署管理后台
- 发布小程序
- 配置CDN加速

**Phase 4: 监控运维（1周）**
- 配置监控告警
- 配置日志采集
- 配置备份策略
- 压力测试

**Phase 5: 优化与上线（持续）**
- 性能优化
- 安全加固
- 成本优化

**总计**: 6-8周完成全部部署

### 10.3 关键注意事项

1. **数据安全**
   - 定期备份（RDS自动备份 + 手动备份）
   - 数据加密（传输加密 + 存储加密）
   - 访问控制（最小权限原则）

2. **成本控制**
   - 使用预留实例券
   - 合理配置自动扩缩容
   - 定期清理无用资源
   - 使用成本分析工具

3. **性能保障**
   - 数据库优化（索引、慢查询）
   - 缓存策略（多级缓存）
   - CDN加速
   - 负载均衡

4. **运维效率**
   - 自动化部署（CI/CD）
   - 监控告警（及时发现问题）
   - 日志分析（快速定位问题）
   - 灾难恢复（快速恢复服务）

---

## 附录

### A. 相关链接

- 阿里云官网: https://www.aliyun.com
- ACK文档: https://help.aliyun.com/product/85222.html
- RDS文档: https://help.aliyun.com/product/26090.html
- Redis文档: https://help.aliyun.com/product/26340.html
- OSS文档: https://help.aliyun.com/product/31815.html
- CDN文档: https://help.aliyun.com/product/27099.html

### B. 技术支持

- 工单系统: https://selfservice.console.aliyun.com/ticket
- 技术论坛: https://developer.aliyun.com/ask
- 钉钉技术支持群: 搜索"阿里云容器服务"

### C. 成本计算器

使用阿里云成本计算器估算费用:
https://www.aliyun.com/pricing-calculator

---

**文档版本**: v1.0
**更新日期**: 2024-12-15
**维护者**: Kaola开发团队
