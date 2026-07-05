#!/bin/bash

# Flink Job 重新部署脚本
# 用途：停止旧 Job，重新打包，提交新 Job

set -e

echo "=========================================="
echo "  Flink Job 重新部署脚本"
echo "=========================================="

# 配置
FLINK_HOME="/opt/module/flink"
PROJECT_DIR="$(cd "$(dirname "$0")" && pwd)"
JAR_NAME="flink-job-1.0-SNAPSHOT.jar"

echo ""
echo "1. 停止旧的 Flink Job..."
JOB_ID=$($FLINK_HOME/bin/flink list 2>/dev/null | grep "Traffic Realtime Compute Job" | awk '{print $4}' | head -1)

if [ -n "$JOB_ID" ]; then
    echo "   找到 Job ID: $JOB_ID"
    $FLINK_HOME/bin/flink cancel $JOB_ID
    echo "   Job 已停止"
    sleep 3
else
    echo "   没有找到运行中的 Job"
fi

echo ""
echo "2. 重新打包 Flink Job..."
cd "$PROJECT_DIR"
mvn clean package -DskipTests
echo "   打包完成"

echo ""
echo "3. 提交新的 Flink Job..."
$FLINK_HOME/bin/flink run -d "$PROJECT_DIR/target/$JAR_NAME"
echo "   Job 已提交"

echo ""
echo "4. 验证 Job 状态..."
sleep 2
$FLINK_HOME/bin/flink list

echo ""
echo "=========================================="
echo "  部署完成！"
echo "=========================================="
echo ""
echo "验证步骤："
echo "1. 查看 Flink Web UI: http://hadoop102:8081"
echo "2. 等待 30 秒让 Mock 更新数据"
echo "3. 检查 Redis: redis-cli -h hadoop102"
echo "   > HGETALL traffic:road_condition"
echo ""
