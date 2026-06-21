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
| 阶段四 | 登录认证 + 权限控制 + 图片上传 | ✅ 已完成 |
| 阶段五 | 商品模块开发 | ✅ 已完成 |
| 阶段六 | 求购模块开发 | ✅ 已完成 |
| 阶段七 | 收藏+交易意向+搜索浏览历史 | ✅ 已完成 |
| 阶段八 | 管理员商品审核与下架 | ✅ 已完成 |
| 阶段九 | 管理员求购审核 | ✅ 已完成 |
| 阶段十 | 管理员用户管理 | ✅ 已完成 |
| 阶段十一 | 管理员订单查看与平台统计 | ✅ 已完成 |
| 阶段十二 | 智能匹配与推荐算法 | ✅ 已完成 |
| 阶段十三 | Web 用户端基础框架与登录注册 | ✅ 已完成 |
| 阶段十四 | Web 用户端商品首页、列表、详情 | ✅ 已完成 |
| 阶段十五 | Web 用户端发布商品与我的商品 | ✅ 已完成 |
| 阶段十六 | Web 管理后台开发 | ⏳ 待开始 |
| 阶段十七 | Android 端开发 | ⏳ 待开始 |
| 阶段十八 | 部署与测试 | ⏳ 待开始 |

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

### 3. Web 用户端启动

```bash
cd secondhand-web-user

# 安装依赖
npm install

# 启动开发服务器
npm run dev
```

启动后访问：`http://localhost:5173`

**前端技术栈**：Vue 3 + Vite + Element Plus + Vue Router + Pinia + Axios

**默认测试账号**：student001 / 123456

**已实现功能**：
- 用户登录/注册
- 首页商品展示（最新商品）
- 商品列表（搜索、分类筛选、成色筛选、价格区间、分页）
- 商品详情（图片浏览、卖家信息、联系方式）
- 发布商品（图片上传、表单填写）
- 我的商品（状态筛选、修改、删除）
- 求购广场（搜索、分类筛选、预算区间、分页）
- 求购详情（分类、预算、期望成色、地点、关键词、发布者信息）
- 发布求购（表单提交，成功后等待审核）
- 我的求购（状态筛选、查看详情、修改、删除）

### 4. 配置说明

数据库连接配置在 `secondhand-backend/src/main/resources/application.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/campus_secondhand
    username: root
    password: root   # 根据本地 MySQL 修改
```

---

## 登录认证说明

### 测试账号

| 角色 | 用户名 | 密码 |
|------|--------|------|
| 管理员 | admin | 123456 |
| 普通用户 | student001 | 123456 |
| 普通用户 | student002 | 123456 |

> 密码使用 BCrypt 加密存储，数据库中的密码字段为密文，不可直接修改。

### Token 使用方式

需要登录的接口在请求头中携带 Token：

```
Authorization: Bearer <token>
```

### 加密方式

- 密码使用 BCrypt 加密存储
- Token 使用 JWT（HS512），包含 userId、username、userType 信息
- Token 默认有效期 24 小时

### 图片上传目录说明

- 开发环境上传目录：`secondhand-backend/upload/`
- 生产环境上传目录：`/www/upload/`
- 按日期分目录存储，例如 `/upload/2026/06/xxx.jpg`
- 文件名使用 UUID，避免重名
- 支持 jpg、jpeg、png、webp 格式，限制 5MB

可在 `application.yml` 中配置：
```yaml
file:
  upload-path: ./upload/      # 上传目录
  access-prefix: /upload/     # 访问前缀
```

---

## 求购功能测试方法

### 1. 发布求购

1. 使用 `student001 / 123456` 登录
2. 点击导航栏「发布」下拉或直接访问 `/publish-demand`
3. 填写求购表单：标题、分类、描述、预算范围、期望成色、期望地点、关键词
4. 点击「发布求购」
5. 应提示「发布成功，等待管理员审核」
6. 自动跳转到 `/my-demands`，列表中应有刚发布的求购，状态为「待审核」

### 2. 求购列表

1. 在管理员后台审核通过一条求购后
2. 访问 `/demand` 页面，已通过的求购应出现在列表中
3. 测试搜索框输入关键词，列表应更新
4. 测试分类筛选下拉框
5. 测试预算区间筛选
6. 测试分页功能
7. 未登录用户也可以正常访问求购列表

### 3. 求购详情

1. 在求购列表点击任意卡片，进入 `/demand/:id`
2. 应显示：标题、分类、预算范围、期望成色、期望地点、关键词、发布者、发布时间、详细描述
3. 点击「返回」按钮可回到列表

### 4. 我的求购

1. 登录后访问 `/my-demands` 或点击导航栏「我的求购」
2. 应显示当前用户发布的所有求购
3. 测试状态筛选：全部、待审核、已通过、已驳回、已关闭
4. 点击「查看」跳转到求购详情页
5. 点击「修改」弹出修改弹窗，回显已有数据，修改后保存
6. 点击「删除」弹出确认框，确认后删除成功并刷新列表

### 5. 修改求购

1. 在我的求购列表点击「修改」按钮
2. 弹窗中应回显已有数据（标题、分类、描述、预算、成色、地点、关键词）
3. 修改任意字段后点击「确认修改」
4. 应提示「修改成功」，列表刷新
5. 修改后求购状态可能变回「待审核」，这是正常行为

### 6. 删除求购

1. 在我的求购列表点击「删除」按钮
2. 弹出确认框「确定要删除这条求购吗？」
3. 点击「确认删除」，提示「删除成功」
4. 列表刷新，该求购不再显示

### 7. 未登录拦截

1. 未登录状态下访问 `/publish-demand`，应自动跳转到 `/login`
2. 未登录状态下访问 `/my-demands`，应自动跳转到 `/login`