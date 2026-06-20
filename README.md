# 校园二手物品智能匹配平台

基于多特征融合推荐算法的校园二手物品交易与智能匹配平台。

## 项目简介

本平台是一个面向高校学生的二手物品交易与智能匹配平台。不同于传统商城系统，平台支持用户发布闲置商品、发布求购需求，并基于**多特征融合推荐算法**，从商品分类、关键词、价格、新旧程度、交易地点、用户行为、商品热度等多个维度，自动计算商品与求购需求的匹配度，直观展示匹配分数和匹配原因，提高校园二手交易的效率与体验。

## 技术栈

| 模块 | 技术 |
|------|------|
| 后端 | Java 17 + Spring Boot + MyBatis-Plus + MySQL 8 + JWT + Maven + Lombok |
| Web 用户端 | Vue 3 + Vite + Element Plus + Axios + Pinia + Vue Router |
| Web 管理后台 | Vue 3 + Vite + Element Plus + Axios + Pinia + Vue Router + ECharts |
| Android 端 | 原生 Android + Java + Retrofit/OkHttp + RecyclerView + SharedPreferences |
| 部署 | Ubuntu 22.04 + Nginx + MySQL + Spring Boot Jar + 静态文件 |

## 目录结构

```
campus-secondhand-platform/
├── secondhand-backend      # 后端服务 (Spring Boot)
├── secondhand-web-user     # Web 用户端 (Vue 3)
├── secondhand-web-admin    # Web 管理后台 (Vue 3)
├── secondhand-android      # Android 用户端
├── sql                     # 数据库脚本
├── docs                    # 项目文档
├── deploy                  # 部署相关配置
└── README.md
```

## 开发阶段

| 阶段 | 内容 | 状态 |
|------|------|------|
| 阶段一 | 项目初始化与规范文档 | ✅ 已完成 |
| 阶段二 | 数据库设计 | ✅ 已完成 |
| 阶段三 | 后端基础框架搭建 | ✅ 已完成 |
| 阶段四 | 后端核心业务开发 | ⏳ 待开始 |
| 阶段五 | Web 用户端开发 | ⏳ 待开始 |
| 阶段六 | Web 管理后台开发 | ⏳ 待开始 |
| 阶段七 | Android 端开发 | ⏳ 待开始 |
| 阶段八 | 推荐算法实现 | ⏳ 待开始 |
| 阶段九 | 部署与测试 | ⏳ 待开始 |

## 快速开始

### 1. 数据库初始化

```bash
mysql -u root -p < sql/init.sql
```

### 2. 后端启动

```bash
cd secondhand-backend

# 使用 Maven Wrapper（推荐）
./mvnw spring-boot:run

# 或使用本地 Maven
mvn spring-boot:run
```

启动后访问：`http://localhost:8080/api/health`

### 3. 配置说明

数据库连接配置在 `secondhand-backend/src/main/resources/application.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/campus_secondhand
    username: root
    password: root   # 根据本地 MySQL 修改
```