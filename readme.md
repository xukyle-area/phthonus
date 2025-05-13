# Gantenx Phthonus

## 项目简介
Gantenx Phthonus 是一个基于 Java 和 Spring Boot 的项目，旨在提供高效的加密货币交易历史数据处理和 WebSocket 订阅服务。

## 技术栈
- **语言**: Java
- **构建工具**: Maven
- **框架**: Spring Boot
- **依赖库**:
    - OkHttp (用于 HTTP 客户端)
    - Lombok (简化 Java 开发)
    - Spring Framework (依赖注入和配置管理)

## 项目结构
    
    infrastructure/ 
    ├── commons/ │ 
    ├── enums/ # 枚举类 │ 
    ├── model/ # 数据模型 │ 
    ├── utils/ # 工具类 │ 
    └── config/ # 配置类 
    ├── history/ # 历史数据处理模块 
    └── websocket/ # WebSocket 订阅模块
    
## 核心功能
1. **历史数据处理**:
    - 提供对加密货币交易历史数据的处理和分析。
    - 使用 `ThreadPool` 进行任务调度。

2. **WebSocket 订阅**:
    - 支持对多个交易对的实时数据订阅。
    - 使用 `HttpFactory` 管理 HTTP 客户端。

3. **工具类**:
    - `TimestampUtils`: 提供时间戳相关的工具方法。
    - `ThreadPool`: 提供线程池管理功能。

## 快速开始

### 环境要求
- JDK 17+
- Maven 3.8+
- MySQL (可选，若涉及数据库)

### 本地运行
1. 克隆项目：
   ```bash
   git clone https://github.com/xuzhongjian/gantenx-phthonus.git
   cd gantenx-phthonus
    ```
2. 构建项目：
    ```bash
    mvn clean install
    ```
3. 启动应用：  
    ```bash
    mvn spring-boot:run
    ```
4. 访问应用：

配置文件： 

确保在 application.properties 或 application.yml 中正确配置以下内容：
crypto.history.enabled=true