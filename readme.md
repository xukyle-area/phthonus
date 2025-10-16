# Phthonus - 加密货币实时数据处理平台

## 📋 项目简介

Phthonus 是一个高性能的加密货币实时数据处理平台，基于 Spring Boot 构建。该项目专注于从多个交易所（Binance、Crypto.com、HashKey 等）获取实时交易数据，并通过 WebSocket 连接和 Kafka 消息队列进行数据流处理。

## 🚀 核心特性

- **多交易所支持**: 集成 Binance、Crypto.com、HashKey 等主流交易所
- **实时数据流**: 基于 WebSocket 的实时数据订阅
- **消息队列**: 使用 Kafka 进行数据流处理和分发
- **历史数据处理**: 支持历史交易数据的获取和分析
- **灵活配置**: 支持多种配置参数和部署选项
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

- **JDK**: 8+
- **Maven**: 3.6+
- **Kafka**: 用于消息队列（可选）

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

## ⚙️ 配置说明

项目配置主要通过 `application.yaml` 文件进行管理。

如需启用 Kafka 功能，请确保 Kafka 服务正在运行并配置正确的服务器地址。

## 🔍 主要功能模块

- **WebSocket 客户端**: 支持 Binance、Crypto.com、HashKey 等交易所的实时数据订阅
- **历史数据处理**: 获取和分析历史交易数据
- **Kafka 集成**: 消息队列处理和数据分发
- **配置管理**: 灵活的应用配置和参数管理

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

### 添加新交易所支持

1. 在 `model/websocket/` 下创建消息模型
2. 继承 `BaseSocketClient` 创建 Socket 客户端
3. 在 `enums/Market.java` 中添加市场定义
4. 实现相应的历史数据处理器

## 🤝 贡献

欢迎提交 Issues 和 Pull Requests！

## 👥 维护者

[@xukyle-area](https://github.com/xukyle-area)

---

⭐ 如果这个项目对你有帮助，请给它一个 Star！