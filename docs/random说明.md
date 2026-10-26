# Random 数据说明

## 1. 作用

项目中的 `random` 主要用于 Demo 和本地测试，生成或更新交通大数据中的模拟指标。它不是业务真实数据，也不是安全随机数，不能用于密码、令牌或其他安全场景。

整体数据链路如下：

```text
Mock 随机数据 → MySQL → Maxwell → Kafka → Flink → Redis → 主 Spring Boot → 前端大屏
```

## 2. Mock 模块：生成和更新随机数据

代码位置：

```text
mock/src/main/java/com/example/mock/generator/
mock/src/main/java/com/example/mock/scheduler/RealTimeDataUpdater.java
```

### 2.1 初始数据生成

`com.example.mock.mock.Main` 会生成基础维度和统计数据，包括：

- 城市、道路类型、车辆类型、违规类型
- 时间维度
- 交通流量和车辆流量
- 路况指标
- 进豫、出豫车辆数据
- 收费站收费数据
- 交通违规数据
- 河南省城市车辆统计数据

各生成器使用 Java `Random` 产生不同的数值。例如：

```java
int vehicleTotal = 5000 + random.nextInt(15000);
```

表示生成 `5000` 到 `19999` 之间的车辆总数。

### 2.2 实时随机更新

`RealTimeDataUpdater` 使用 Spring 定时任务更新 MySQL 中的实时数据：

```java
@Scheduled(fixedRate = 5000)
```

也就是每 5 秒执行一次。主要更新：

- 路况警情、拥堵指数、通行效率、平均速度
- 收费站收费金额
- 城市车辆总数、在线数、告警数
- 客运量和货运量
- 进豫、出豫车辆数量
- 交通流量
- 车辆类型流量
- 交通违规数量

这些更新会触发 Maxwell 监听的 MySQL Binlog，之后进入 Kafka。

## 3. 主 Spring Boot 服务

代码位置：

```text
src/main/java/com/example/one/
src/main/resources/application.yml
```

主服务本身不负责生成随机数据，主要负责：

1. 接收前端登录和注册请求；
2. 校验 JWT；
3. 从 MySQL 查询统计数据；
4. 从 Redis 查询 Flink 计算后的实时数据；
5. 将数据返回给前端 ECharts 大屏。

启动命令：

```powershell
mvn spring-boot:run
```

默认端口：

```text
http://localhost:8080
```

访问页面：

```text
http://localhost:8080/login2.html
```

## 4. Flink 实时计算任务

代码位置：

```text
flink-job/src/main/java/com/traffic/flink/TrafficRealtimeJob.java
```

Flink 不负责产生随机数据，它负责消费 Kafka 中的 Maxwell 变更消息并进行实时计算：

1. 从 Kafka 的 Maxwell Topic 消费 MySQL 变更消息；
2. 解析交通流量、车辆、违规等数据；
3. 按业务维度进行聚合；
4. 将实时结果写入 Redis；
5. 主 Spring Boot 服务从 Redis 读取结果并返回给前端。

打包命令：

```powershell
mvn -f flink-job/pom.xml clean package -DskipTests
```

生成的任务包位于：

```text
flink-job/target/traffic-flink-job-1.0.0.jar
```

提交到 Flink 集群：

```bash
flink run -c com.traffic.flink.TrafficRealtimeJob flink-job/target/traffic-flink-job-1.0.0.jar
```

启动 Flink 任务前，需要确认 Kafka 和 Redis 地址与 `TrafficRealtimeJob.java` 中的配置一致。

## 5. 本地启动顺序

如果需要运行完整实时链路，建议按以下顺序启动：

### 第一步：确认基础服务

确认 MySQL、Redis、ZooKeeper、Kafka 和 Flink 已启动。

### 第二步：生成初始数据

运行 `com.example.mock.mock.Main`，生成基础测试数据。

也可以启动 Mock 服务：

```powershell
mvn -f mock/pom.xml spring-boot:run
```

Mock 服务默认端口为 `8081`。

### 第三步：启动 Maxwell

Maxwell 监听 MySQL Binlog，并把变更发送到 Kafka。

### 第四步：启动 Flink 任务

```bash
flink run -c com.traffic.flink.TrafficRealtimeJob flink-job/target/traffic-flink-job-1.0.0.jar
```

### 第五步：启动主服务

```powershell
mvn spring-boot:run
```

### 第六步：打开大屏

```text
http://localhost:8080/login2.html
```

## 6. 注意事项

- `Random` 生成的数据只用于 Demo 展示和链路测试。
- 随机数据每次启动或更新时可能不同，不应作为真实统计结果。
- 主服务启动时不自动启动 Flink。
- 如果只验证登录页面或静态页面，可以只启动主 Spring Boot 服务。
- 如果只验证数据库中的模拟数据，可以运行 Mock 数据生成器，不需要启动 Flink。
- 完整实时链路需要 MySQL、Maxwell、Kafka、Flink 和 Redis 同时可用。
