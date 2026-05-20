#!/bin/bash
# 考拉推拿 · 本地微服务一键启动脚本
# 用法：
#   ./start-local.sh          # 启动全部（已在运行的跳过）
#   ./start-local.sh stop     # 停止全部
#   ./start-local.sh restart  # 重启全部
#   ./start-local.sh status   # 查看各服务状态
#   ./start-local.sh build    # 重新编译全部再启动
#   ./start-local.sh logs [service-name]  # 追踪日志（默认 gateway）

MICROSERVICES_DIR="$(cd "$(dirname "$0")" && pwd)"
LOG_DIR="$MICROSERVICES_DIR/.logs"
JAVA="/usr/local/opt/openjdk@17/bin/java"
JVM_OPTS="-Xms64m -Xmx256m -XX:+UseSerialGC -XX:MaxMetaspaceSize=128m"

SPRING_OPTS=(
  "-Dspring.datasource.url=jdbc:mysql://localhost:3306/kaola_massage?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&useSSL=false&allowPublicKeyRetrieval=true"
  "-Dspring.datasource.username=root"
  "-Dspring.datasource.password=kaola123456"
  "-Dspring.redis.host=localhost"
  "-Dspring.redis.port=6379"
  "-Dspring.redis.password="
  "-Dspring.cloud.nacos.discovery.enabled=false"
  "-Dspring.cloud.nacos.config.enabled=false"
  "-Dspring.config.import="
)

# gateway 最后启动，确保其他服务先就绪
SERVICES=(
  "kaola-auth-service:8081"
  "kaola-user-service:8082"
  "kaola-store-service:8083"
  "kaola-masseur-service:8084"
  "kaola-product-service:8085"
  "kaola-order-service:8086"
  "kaola-payment-service:8087"
  "kaola-schedule-service:8091"
  "kaola-marketing-service:8092"
  "kaola-review-service:8093"
  "kaola-complaint-service:8094"
  "kaola-admin-service:8095"
  "kaola-earning-service:8096"
  "kaola-gateway:8090"
)

mkdir -p "$LOG_DIR"

pid_file()  { echo "$LOG_DIR/$1.pid"; }

is_running() {
  local pf; pf=$(pid_file "$1")
  [[ -f "$pf" ]] && kill -0 "$(cat "$pf")" 2>/dev/null
}

start_service() {
  local name=$1 port=$2
  local jar="$MICROSERVICES_DIR/$name/target/$name-1.0.0.jar"

  if is_running "$name"; then
    printf "  [SKIP]  %-35s 已运行 PID=%s\n" "$name" "$(cat "$(pid_file "$name")")"
    return
  fi
  if [[ ! -f "$jar" ]]; then
    printf "  [WARN]  %-35s JAR 不存在，跳过\n" "$name"
    return
  fi

  printf "  [START] %-35s :%s\n" "$name" "$port"
  nohup "$JAVA" $JVM_OPTS "${SPRING_OPTS[@]}" \
    -Dserver.port="$port" \
    -jar "$jar" \
    > "$LOG_DIR/$name.log" 2>&1 &
  echo $! > "$(pid_file "$name")"
}

stop_service() {
  local name=$1
  local pf; pf=$(pid_file "$name")
  if is_running "$name"; then
    printf "  [STOP]  %-35s PID=%s\n" "$name" "$(cat "$pf")"
    kill "$(cat "$pf")" 2>/dev/null
  fi
  rm -f "$pf"
}

show_status() {
  echo ""
  printf "  %-35s %-6s %s\n" "服务" "端口" "状态"
  printf "  %s\n" "$(printf '%.0s-' {1..60})"
  for entry in "${SERVICES[@]}"; do
    local name="${entry%%:*}" port="${entry##*:}"
    if is_running "$name"; then
      printf "  %-35s %-6s \033[32m[UP]\033[0m   PID=%s\n" "$name" ":$port" "$(cat "$(pid_file "$name")")"
    else
      printf "  %-35s %-6s \033[31m[DOWN]\033[0m\n" "$name" ":$port"
    fi
  done
  echo ""
}

ACTION="${1:-start}"

case "$ACTION" in

  start)
    echo "=== 启动考拉本地微服务 ==="
    for entry in "${SERVICES[@]}"; do
      start_service "${entry%%:*}" "${entry##*:}"
    done
    echo ""
    echo "  等待 gateway (8090) 就绪..."
    local i=0
    until lsof -i :8090 2>/dev/null | grep -q LISTEN; do
      sleep 3; i=$((i+3))
      if [[ $i -ge 120 ]]; then
        echo "  [TIMEOUT] gateway 启动超时，请查看: $LOG_DIR/kaola-gateway.log"
        break
      fi
    done
    lsof -i :8090 2>/dev/null | grep -q LISTEN && echo "  [OK] Gateway → http://127.0.0.1:8090"
    show_status
    ;;

  stop)
    echo "=== 停止考拉本地微服务 ==="
    for entry in "${SERVICES[@]}"; do
      stop_service "${entry%%:*}"
    done
    echo "=== 已全部停止 ==="
    ;;

  restart)
    "$0" stop
    sleep 2
    "$0" start
    ;;

  status)
    show_status
    ;;

  build)
    echo "=== 编译全部服务（跳过测试）==="
    cd "$MICROSERVICES_DIR"
    mvn package -DskipTests -q --no-transfer-progress
    echo "=== 编译完成 ==="
    "$0" start
    ;;

  logs)
    SERVICE="${2:-kaola-gateway}"
    LOG="$LOG_DIR/$SERVICE.log"
    if [[ ! -f "$LOG" ]]; then
      echo "日志不存在: $LOG"
      exit 1
    fi
    tail -f "$LOG"
    ;;

  *)
    echo "用法: $0 {start|stop|restart|status|build|logs [service-name]}"
    exit 1
    ;;
esac
