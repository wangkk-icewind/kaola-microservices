#!/bin/bash
# 本地开发启动脚本
# 依赖：本机 MySQL（root/kaola123456，kaola_massage 库）、本机 Redis（127.0.0.1:6379）
# Nacos 可选，不启动也能运行（spring.config.import=optional:nacos:）

JAVA=/usr/local/opt/openjdk@17/bin/java
SERVICES_DIR="$(cd "$(dirname "$0")" && pwd)"
LOG_DIR="$SERVICES_DIR/.local-logs"
JVM_OPTS="-Xms64m -Xmx256m -XX:+UseSerialGC -XX:MaxMetaspaceSize=128m"

mkdir -p "$LOG_DIR"

start_svc() {
  local name=$1
  local port=$2
  local jar="$SERVICES_DIR/$name/target/$name-1.0.0.jar"
  local pid_file="$LOG_DIR/$name.pid"
  local log_file="$LOG_DIR/$name.log"

  if [ ! -f "$jar" ]; then
    echo "[SKIP] $name — JAR 不存在，请先 mvn package"
    return
  fi

  if [ -f "$pid_file" ] && kill -0 "$(cat "$pid_file")" 2>/dev/null; then
    echo "[UP]   $name 已在运行 (PID $(cat "$pid_file")) :$port"
    return
  fi

  echo "[START] $name :$port ..."
  nohup $JAVA $JVM_OPTS \
    -Dserver.port=$port \
    -Dspring.datasource.url="jdbc:mysql://localhost:3306/kaola_massage?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&useSSL=false&allowPublicKeyRetrieval=true" \
    -Dspring.datasource.username=root \
    -Dspring.datasource.password=kaola123456 \
    -Dspring.redis.host=localhost \
    -Dspring.redis.port=6379 \
    -Dspring.redis.password= \
    -Dspring.cloud.nacos.discovery.enabled=false \
    -Dspring.cloud.nacos.config.enabled=false \
    -jar "$jar" \
    > "$log_file" 2>&1 &
  echo $! > "$pid_file"
  echo "       PID=$! log=$log_file"
}

stop_svc() {
  local name=$1
  local pid_file="$LOG_DIR/$name.pid"
  if [ -f "$pid_file" ]; then
    local pid
    pid=$(cat "$pid_file")
    if kill -0 "$pid" 2>/dev/null; then
      echo "[STOP] $name (PID $pid)"
      kill "$pid"
    fi
    rm -f "$pid_file"
  fi
}

status_svc() {
  local name=$1
  local port=$2
  local pid_file="$LOG_DIR/$name.pid"
  if [ -f "$pid_file" ] && kill -0 "$(cat "$pid_file")" 2>/dev/null; then
    echo "[UP]   $name :$port (PID $(cat "$pid_file"))"
  else
    echo "[DOWN] $name :$port"
  fi
}

# 核心服务（按启动顺序）
SERVICES=(
  "kaola-auth-service:8081"
  "kaola-user-service:8082"
  "kaola-store-service:8083"
  "kaola-masseur-service:8084"
  "kaola-product-service:8085"
  "kaola-order-service:8086"
  "kaola-payment-service:8087"
  "kaola-marketing-service:8092"
  "kaola-admin-service:8095"
  "kaola-gateway:8090"
)

ACTION=${1:-start}

case $ACTION in
  start)
    echo "=== 启动本地服务 (MySQL: localhost, Redis: localhost) ==="
    for entry in "${SERVICES[@]}"; do
      name="${entry%%:*}"; port="${entry##*:}"
      start_svc "$name" "$port"
      sleep 1
    done
    echo "=== 完成，等待服务就绪约 30s ==="
    echo "    Gateway: http://localhost:8090"
    echo "    日志目录: $LOG_DIR"
    ;;
  stop)
    echo "=== 停止所有本地服务 ==="
    for entry in "${SERVICES[@]}"; do stop_svc "${entry%%:*}"; done
    ;;
  restart)
    $0 stop; sleep 3; $0 start
    ;;
  status)
    echo "=== 本地服务状态 ==="
    for entry in "${SERVICES[@]}"; do
      status_svc "${entry%%:*}" "${entry##*:}"
    done
    ;;
  log)
    # 用法: ./start-local.sh log kaola-order-service
    tail -f "$LOG_DIR/${2:-kaola-gateway}.log"
    ;;
esac
