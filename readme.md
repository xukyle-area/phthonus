# Phthonus 🚀

[![Java](https://img.shields.io/badge/Java-8+-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-2.7.18-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

> 高性能加密货币实时数据处理平台

## 📋 项目概述

**Phthonus** 是一个专业的加密货币数据处理平台，基于 Spring Boot 架构设计。项目致力于提供稳定、高效的多交易所数据采集和处理服务，支持实时数据流和历史数据分析。

### 🎯 设计目标
- **高性能**: 基于异步处理和线程池优化
- **可扩展**: 模块化设计，易于添加新的交易所支持
- **稳定性**: 完善的错误处理和重连机制
- **易用性**: 灵活的配置管理和监控支持

## ✨ 核心功能

| 功能模块           | 描述                              | 状态   |
| ------------------ | --------------------------------- | ------ |
| 🏪 **多交易所集成** | 支持 Binance、Crypto.com、HashKey | ✅      |
| 📡 **实时数据流**   | WebSocket 实时价格订阅            | ✅      |
| 📚 **历史数据处理** | K线数据获取与分析                 | ✅      |
| 🔄 **消息队列**     | Kafka 数据流处理                  | ⚠️ 可选 |
| ⚙️ **配置管理**     | YAML 配置热更新                   | ✅      |
| 🧵 **并发处理**     | 高性能线程池管理                  | ✅      |

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
    ├── HttpFactory.java            # HTTP 客户端工厂
    ├── HttpRequestBuilder.java     # HTTP 请求构建器
    ├── JsonUtils.java              # JSON 处理工具
    ├── ThreadPool.java             # 线程池管理
    └── TimestampUtils.java         # 时间戳工具
```

## �️ 技术架构

### 后端技术栈
- **核心框架**: Spring Boot 2.7.18 + Java 8
- **HTTP 客户端**: OkHttp 3.x（高性能网络请求）
- **WebSocket**: 原生实现（实时数据订阅）
- **消息队列**: Apache Kafka（可选，数据流处理）
- **序列化**: Jackson + Protocol Buffers
- **构建工具**: Maven 3.6+

### 系统要求
- **JDK**: 8 或更高版本
- **内存**: 建议 1GB+ 可用内存
- **网络**: 稳定的互联网连接（访问交易所 API）

## 🚀 快速开始

### 安装部署

```bash 
# 1. 克隆项目
git clone https://github.com/xukyle-area/phthonus.git
cd phthonus

# 2. 编译构建
mvn clean install

# 3. 启动应用
mvn spring-boot:run
```

### 配置说明

在 `src/main/resources/application.yaml` 中配置：

```yaml
# 交易所配置
binance.history:
  socket:
    enabled: true    # 启用 WebSocket 连接
  history:
    enabled: true    # 启用历史数据处理

crypto.history.enabled: true  # 启用 Crypto.com 历史数据

# Socket 任务配置（可选）
socket:
  binance:
    enabled: false   # 控制 Binance Socket 任务
  crypto:
    enabled: false   # 控制 Crypto.com Socket 任务  
  hashkey:
    enabled: true    # 控制 HashKey Socket 任务
```

## � 交易所支持

| 交易所         | 实时数据 | 历史数据 | WebSocket | API 文档                                            |
| -------------- | -------- | -------- | --------- | --------------------------------------------------- |
| **Binance**    | ✅        | ✅        | ✅         | [官方文档](https://binance-docs.github.io/apidocs/) |
| **Crypto.com** | ✅        | ✅        | ✅         | [官方文档](https://exchange-docs.crypto.com/)       |
| **HashKey**    | ✅        | ✅        | ✅         | [官方文档](https://hashkeypro-apidoc.readme.io/)    |

### 数据类型支持
- 📈 **实时行情**: Ticker 数据、深度数据
- 📊 **K线数据**: 1分钟、1小时、1天等多种周期
- 💰 **交易数据**: 成交记录、订单簿变化
- 📉 **历史数据**: 历史价格、交易量统计

## 🔧 核心组件

### HTTP 请求构建器
```java
// 使用链式调用构建 HTTP 请求
Request request = new HttpRequestBuilder("https://api.example.com/data")
    .addParam("symbol", "BTCUSDT")
    .addParam("interval", "1d")
    .addHeader("User-Agent", "Phthonus/1.0")
    .buildGetRequest();
```

### WebSocket 客户端
```java
// 创建交易所 WebSocket 连接
SocketTask task = new SocketTask(Market.BINANCE);
// Socket 自动管理连接、重连和数据处理
```

### 数据模型
- `DayQuote`: 日线数据模型
- `RealTimeQuote`: 实时报价模型
- 各交易所专用消息模型

## � 故障排除

<details>
<summary>🔍 常见问题解决方案</summary>

### WebSocket 连接问题
```bash
# 检查网络连接
ping api.binance.com

# 检查端口是否开放
telnet stream.binance.com 9443
```

**解决方案**:
- 确认防火墙设置允许 HTTPS/WSS 连接
- 检查代理配置
- 验证交易所 API 状态

### 历史数据获取失败
**常见错误**: `Not supported symbols`

**解决方案**:
- 检查交易对符号格式（如 `BTCUSDT` vs `BTC_USDT`）
- 确认交易所支持该交易对
- 查看 API 限频设置

### 内存使用优化
```bash
# 启动时设置 JVM 参数
java -Xms512m -Xmx2g -jar phthonus-1.0-SNAPSHOT.jar
```

</details>

## 📈 性能监控

### 关键指标
- **WebSocket 连接状态**: 实时监控连接健康度
- **数据处理延迟**: 从接收到处理完成的时间
- **内存使用率**: JVM 堆内存使用情况
- **线程池状态**: 活跃线程数和队列长度

### 日志配置
```yaml
logging:
  level:
    com.gantenx.phthonus: INFO
    com.gantenx.phthonus.socket: DEBUG  # WebSocket 详细日志
```

## �️ 发展规划

### 短期目标
- [ ] **更多交易所**: 添加 OKX、Gate.io 支持
- [ ] **数据存储**: 集成 InfluxDB 时序数据库
- [ ] **监控面板**: Web 管理界面

### 长期规划  
- [ ] **策略引擎**: 内置量化交易策略框架
- [ ] **风控模块**: 实时风险监控和预警
- [ ] **微服务化**: 拆分为独立的微服务架构

## 🤝 参与贡献

### 开发流程
1. **Fork** 项目到您的 GitHub
2. **Clone** 到本地开发环境
3. 创建特性分支: `git checkout -b feature/amazing-feature`
4. 提交代码: `git commit -m 'Add amazing feature'`
5. 推送分支: `git push origin feature/amazing-feature`
6. 提交 **Pull Request**

### 代码规范
- 遵循 Java 编码规范
- 添加必要的注释和文档
- 确保测试覆盖率
- 提交前运行 `mvn clean test`

## 📄 许可证

本项目采用 [MIT License](LICENSE) 开源协议。

## 👥 贡献者

<a href="https://github.com/xukyle-area/phthonus/graphs/contributors">
  <img src="https://contrib.rocks/image?repo=xukyle-area/phthonus" />
</a>

感谢所有为项目做出贡献的开发者！

---

<div align="center">

**🌟 如果这个项目对您有帮助，请点击 Star 支持我们！**

[![GitHub stars](https://img.shields.io/github/stars/xukyle-area/phthonus.svg?style=social&label=Star)](https://github.com/xukyle-area/phthonus)
[![GitHub forks](https://img.shields.io/github/forks/xukyle-area/phthonus.svg?style=social&label=Fork)](https://github.com/xukyle-area/phthonus/fork)

</div>