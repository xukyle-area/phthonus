# Phthonus - 加密货币实时数据处理平台

## 📋 项目简介

Phthonus 是一个高性能的加密货币实时数据处理平台，基于 Spring Boot 构建。该项目专注于从多个交易所（Binance、Crypto.com、HashKey 等）获取实时交易数据，并通过 WebSocket 连接和 Kafka 消息队列进行数据流处理。

## 🚀 核心特性

- **多交易所支持**: 集成 Binance、Crypto.com、HashKey 等主流交易所
- **实时数据流**: 基于 WebSocket 的实时数据订阅
- **消息队列**: 使用 Kafka 进行数据流处理和分发
- **历史数据处理**: 支持历史交易数据的获取和分析
- **多环境配置**: 支持 AWS、本地等多种部署环境
- **高并发处理**: 基于线程池的异步数据处理

## 🛠️ 技术栈

- **语言**: Java 8
- **框架**: Spring Boot 2.7.18
- **构建工具**: Maven
- **消息队列**: Apache Kafka
- **WebSocket**: 原生 WebSocket 客户端
- **HTTP 客户端**: OkHttp
- **序列化**: Jackson, Protocol Buffers
- **日志**: SLF4J + Logback
- **其他**: Lombok

## 📁 项目结构

```
src/main/java/com/gantenx/phthonus/
├── PhthonusApplication.java          # 应用程序入口
├── configuration/                    # 配置类
│   └── SocketConfiguration.java      # WebSocket 配置
├── constants/                        # 常量定义
│   └── Constant.java                 # 系统常量
├── enums/                           # 枚举类
│   ├── Environment.java             # 环境枚举
│   ├── Market.java                  # 市场枚举
│   └── Symbol.java                  # 交易对枚举
├── history/                         # 历史数据处理
│   ├── BinanceHandler.java          # Binance 历史数据处理
│   ├── CryptoHandler.java           # Crypto.com 历史数据处理
│   └── HistoryQuoteHandler.java     # 历史报价处理器
├── model/                          # 数据模型
│   ├── common/                     # 通用模型
│   │   ├── DayQuote.java           # 日线数据模型
│   │   └── RealTimeQuote.java      # 实时报价模型
│   └── websocket/                  # WebSocket 消息模型
│       ├── binance/                # Binance 消息模型
│       ├── crypto/                 # Crypto.com 消息模型
│       └── hashkey/                # HashKey 消息模型
├── socket/                         # WebSocket 客户端
│   ├── BaseSocketClient.java       # 基础 Socket 客户端
│   ├── BinanceSocketClient.java    # Binance Socket 客户端
│   ├── CryptoSocketClient.java     # Crypto.com Socket 客户端
│   ├── HashkeySocketClient.java    # HashKey Socket 客户端
│   └── SocketTask.java             # Socket 任务
└── utils/                          # 工具类
    ├── HttpFactory.java            # HTTP 工厂类
    ├── JsonUtils.java              # JSON 工具类
    ├── KafkaMessageViewer.java     # Kafka 消息查看器
    ├── PropertyUtils.java          # 属性工具类
    ├── ProtoSerializer.java        # Protocol Buffers 序列化
    ├── ThreadPool.java             # 线程池管理
    └── TimestampUtils.java         # 时间戳工具类
```

## 🔧 环境要求

- **JDK**: 8 或更高版本
- **Maven**: 3.6+ 
- **Kafka**: 用于消息队列（可选）
- **内存**: 建议 2GB 以上

## 🚀 快速开始

### 1. 克隆项目

```bash
git clone https://github.com/xukyle-area/phthonus.git
cd phthonus
```

### 2. 构建项目

```bash
mvn clean install
```

### 3. 配置文件

编辑 `src/main/resources/application.yaml`：

```yaml
binance.history:
  socket:
    enabled: true
  history:
    enabled: true
crypto.history.enabled: true
```

### 4. 启动应用

```bash
mvn spring-boot:run
```

或者运行编译后的 JAR：

```bash
java -jar target/phthonus-1.0-SNAPSHOT.jar
```

## ⚙️ 配置说明

### 环境配置

项目支持多环境配置，可在 `Environment` 枚举中配置不同环境的参数：

- `AWS1`: AWS 环境 1
- `AWS2`: AWS 环境 2  
- `LOCAL`: 本地环境

### Kafka 配置

如需启用 Kafka 功能，请确保：

1. Kafka 服务正在运行
2. 在 `Environment` 枚举中配置正确的 Kafka 服务器地址
3. 确保主题已创建或开启自动创建主题功能

## 🔍 使用示例

### 查看 Kafka 消息

```java
// 查看指定主题的消息
KafkaMessageViewer.viewKafkaMessages(
    Environment.AWS1.getKafkaBootstrapServers(), 
    Constant.ONGOING_KAFKA_TOPIC
);
```

### 创建 WebSocket 连接

```java
// 创建 Binance WebSocket 连接
BinanceSocketClient client = new BinanceSocketClient();
client.connect();
```

## 📊 支持的交易所

| 交易所     | 实时数据 | 历史数据 | WebSocket |
| ---------- | -------- | -------- | --------- |
| Binance    | ✅        | ✅        | ✅         |
| Crypto.com | ✅        | ✅        | ✅         |
| HashKey    | ✅        | ❌        | ✅         |

## 🐛 故障排除

### 常见问题

1. **WebSocket 连接失败**
   - 检查网络连接
   - 验证交易所 API 是否可用
   - 检查防火墙设置

2. **Kafka 连接失败**
   - 确认 Kafka 服务是否启动
   - 检查 Kafka 服务器地址配置
   - 验证网络连通性

3. **内存不足**
   - 增加 JVM 堆内存: `-Xmx2g`
   - 调整线程池大小配置

## 📝 开发指南

### 添加新的交易所支持

1. 在 `model/websocket/` 下创建新的消息模型
2. 继承 `BaseSocketClient` 创建新的 Socket 客户端
3. 在 `enums/Market.java` 中添加新的市场定义
4. 实现相应的历史数据处理器

### 贡献代码

1. Fork 项目
2. 创建特性分支: `git checkout -b feature/AmazingFeature`
3. 提交变更: `git commit -m 'Add some AmazingFeature'`
4. 推送分支: `git push origin feature/AmazingFeature`
5. 提交 Pull Request

## 📄 许可证

此项目采用 MIT 许可证 - 详情请查看 [LICENSE](LICENSE) 文件。

## 👥 维护者

- [@xukyle-area](https://github.com/xukyle-area)

## 🤝 贡献

欢迎提交 Issues 和 Pull Requests！

---

⭐ 如果这个项目对你有帮助，请给它一个 Star！