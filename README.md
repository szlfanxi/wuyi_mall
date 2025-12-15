<<<<<<< HEAD
# 无艺商城项目

## 项目简介

基于 Spring Boot + Vue 3 + MySQL 开发的现代化电商平台，提供完整的商品管理、订单管理、用户管理、购物车、收藏等功能。

## 技术栈

### 后端技术
- **框架**：Spring Boot 3.2.0
- **构建工具**：Maven
- **ORM框架**：Spring Data JPA
- **安全框架**：Spring Security
- **认证方式**：JWT Token
- **数据库**：MySQL 8.0
- **API设计**：RESTful API

### 前端技术
- **框架**：Vue 3
- **构建工具**：Vite
- **路由管理**：Vue Router
- **HTTP客户端**：Axios
- **状态管理**：Vue 3 Composition API

## 项目结构

```
wuyi_mall/
├── backend/              # 后端 Spring Boot 项目
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── com/
│   │   │   │       └── wuyimall/   # 主代码目录
│   │   │   │           ├── config/      # 配置类
│   │   │   │           ├── controller/   # 控制器
│   │   │   │           ├── dto/          # 数据传输对象
│   │   │   │           ├── entity/       # 实体类
│   │   │   │           ├── exception/    # 异常处理
│   │   │   │           ├── repository/   # 数据访问层
│   │   │   │           ├── service/      # 业务逻辑层
│   │   │   │           ├── util/         # 工具类
│   │   │   │           └── WuyiMallApplication.java  # 启动类
│   │   │   └── resources/          # 资源文件目录
│   │   │       └── application.yml  # 配置文件
│   │   └── test/                   # 测试代码目录
│   └── pom.xml                     # Maven 配置文件
├── frontend/             # 前端 Vue 项目
│   ├── public/           # 静态资源目录
│   ├── src/              # 主代码目录
│   │   ├── assets/       # 资源文件
│   │   ├── components/   # Vue 组件
│   │   ├── router/       # 路由配置
│   │   ├── utils/        # 工具类
│   │   ├── views/        # 页面组件
│   │   │   ├── User/     # 用户相关页面
│   │   │   └── admin/    # 管理员相关页面
│   │   ├── App.vue       # 根组件
│   │   └── main.js       # 入口文件
│   ├── index.html        # HTML 模板
│   ├── package.json      # npm 配置文件
│   └── vite.config.js    # Vite 配置文件
├── wuyi_mall.sql          # MySQL 初始化脚本
└── README.md             # 项目说明文档
```

## 功能模块

### 1. 用户管理
- 用户注册、登录
- 用户信息管理
- 用户权限控制

### 2. 商品管理
- 商品列表展示
- 商品详情展示
- 商品分类管理
- 商品图片管理
- 商品库存管理

### 3. 购物车管理
- 加入购物车
- 修改购物车商品数量
- 删除购物车商品
- 购物车商品选择
- 批量加入收藏

### 4. 订单管理
- 创建订单
- 订单列表
- 订单详情
- 取消订单
- 支付订单
- 确认收货

### 5. 收藏管理
- 商品收藏/取消收藏
- 收藏列表

### 6. 管理员功能
- 用户管理
- 商品管理
- 订单管理
- 类目管理
- 仪表盘

## 环境要求

### 后端环境
- JDK 17+
- Maven 3.8+
- MySQL 8.0+

### 前端环境
- Node.js 16+
- npm 8+

## 初始化步骤

### 1. 数据库初始化

```bash
# 登录 MySQL
mysql -u root -p

# 创建数据库
CREATE DATABASE wuyi_mall CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

# 退出 MySQL
exit;

# 执行初始化脚本
mysql -u root -p wuyi_mall < wuyi_mall.sql
```

### 2. 后端项目初始化

```bash
# 进入后端目录
cd backend

# 安装依赖
mvn clean install

# 运行项目
mvn spring-boot:run
```

### 3. 前端项目初始化

```bash
# 进入前端目录
cd frontend

# 安装依赖
npm install

# 运行项目
npm run dev
```

## 运行项目

### 后端

```bash
cd backend
mvn spring-boot:run
```

### 前端

```bash
cd frontend
npm run dev
```

## 访问地址

| 服务 | 地址 | 说明 |
|------|------|------|
| 后端 API | http://localhost:8080 | 后端服务地址 |
| 前端应用 | http://localhost:5173 | 前端应用地址 |

## API 接口

### 用户相关
- `POST /api/auth/login` - 用户登录
- `POST /api/auth/register` - 用户注册

### 商品相关
- `GET /api/products` - 获取商品列表
- `GET /api/products/:id` - 获取商品详情

### 购物车相关
- `GET /api/cart/list` - 获取购物车列表
- `POST /api/cart/add` - 添加商品到购物车
- `POST /api/cart/update` - 更新购物车商品数量
- `POST /api/cart/remove` - 移除购物车商品

### 订单相关
- `GET /api/orders` - 获取订单列表
- `POST /api/orders/create` - 创建订单
- `POST /api/orders/:orderId/cancel` - 取消订单
- `POST /api/orders/:orderId/pay` - 支付订单

### 收藏相关
- `GET /api/favorites/list` - 获取收藏列表
- `POST /api/favorites/add` - 添加收藏
- `POST /api/favorites/remove` - 取消收藏

## 开发说明

### 代码规范
- 后端：遵循 Java 代码规范
- 前端：遵循 Vue 代码规范

### 提交规范
- 提交信息应清晰描述所做的修改
- 建议使用 Conventional Commits 规范

## 测试账号

### 管理员账号
- 用户名：admin
- 密码：123456

### 普通用户账号
- 用户名：wuyi
- 密码：123456

## 注意事项

1. 确保 MySQL 服务已启动
2. 确保后端服务先于前端服务启动
3. 首次启动时，会自动创建管理员用户
4. 开发环境下，建议关闭防火墙或开放相应端口
5. 生产环境部署时，应修改默认密码和密钥



=======
# 无艺商城-客户注册登录核心模块

## 项目介绍

本项目是一个基于Spring Boot 3.2.x的客户注册登录核心模块，采用Maven进行依赖管理，使用MySQL 8.0作为数据库，实现了客户注册、登录功能，并使用JWT进行身份认证。

## 技术栈

- 开发框架：Spring Boot 3.2.x
- 数据库：MySQL 8.0
- 权限认证：Spring Security + JWT
- 依赖管理：Maven
- ORM框架：MyBatis Plus
- 代码规范：Alibaba Java开发手册

## 目录结构

```
├── src
│   ├── main
│   │   ├── java
│   │   │   └── com
│   │   │       └── wuyi
│   │   │           └── mall
│   │   │               ├── config           # 配置类
│   │   │               │   ├── JwtAuthenticationFilter.java  # JWT认证过滤器
│   │   │               │   ├── SecurityConfig.java           # Spring Security配置
│   │   │               │   └── WebConfig.java                # Web配置（跨域）
│   │   │               ├── controller       # 控制器
│   │   │               │   └── UserController.java           # 用户控制器
│   │   │               ├── entity           # 实体类
│   │   │               │   └── User.java                    # 用户实体类
│   │   │               ├── handler          # 处理器
│   │   │               │   └── GlobalExceptionHandler.java   # 全局异常处理器
│   │   │               ├── mapper           # Mapper接口
│   │   │               │   └── UserMapper.java               # 用户Mapper
│   │   │               ├── service          # 服务层
│   │   │               │   ├── UserService.java              # 用户服务接口
│   │   │               │   └── impl
│   │   │               │       └── UserServiceImpl.java      # 用户服务实现
│   │   │               ├── util             # 工具类
│   │   │               │   ├── JwtUtil.java                  # JWT工具类
│   │   │               │   └── ResultUtil.java               # 统一响应工具类
│   │   │               └── vo               # 视图对象
│   │   │                   ├── LoginVO.java                  # 登录请求VO
│   │   │                   ├── RegisterVO.java               # 注册请求VO
│   │   │                   └── UserLoginVO.java              # 登录成功返回VO
│   │   └── resources
│   │       ├── mapper                      # MyBatis映射文件
│   │       │   └── UserMapper.xml
│   │       ├── sql                         # SQL脚本
│   │       │   └── user.sql
│   │       └── application.yml             # 配置文件
│   └── test
│       └── java
│           └── com
│               └── wuyi
│                   └── mall
│                       └── WuyiMallApplicationTests.java
├── pom.xml                                 # Maven依赖文件
└── README.md                               # 项目说明文档
```

## 核心功能

### 1. 客户注册功能

- **入参**：用户名（唯一）、密码（加盐哈希存储）、手机号（唯一）、默认交易地址
- **校验规则**：
  - 用户名/手机号不能为空，且手机号格式符合11位数字规范
  - 用户名/手机号不能重复（数据库唯一索引）
  - 密码长度≥6位，需通过BCrypt加密后存储
- **出参**：注册成功/失败提示，成功返回用户ID，失败返回具体错误信息

### 2. 客户登录功能

- **入参**：登录账号（支持用户名/手机号）、密码
- **校验规则**：
  - 账号不能为空，密码需与数据库加密后的密码匹配
  - 登录成功后生成JWT令牌（有效期2小时），包含用户ID、角色（客户）
- **出参**：登录成功返回JWT令牌+用户基础信息（隐藏密码），失败返回错误信息

### 3. 基础数据校验

- **全局异常处理**：统一捕获参数校验、数据库重复、认证失败等异常，返回标准化JSON响应
- **响应格式**：统一遵循{
  "code": 状态码,
  "msg": "提示信息",
  "data": 业务数据,
  "timestamp": 时间戳
}

## API文档

### 1. 注册接口

- **URL**：`/api/user/register`
- **方法**：`POST`
- **请求体**：
  ```json
  {
    "username": "testuser",
    "password": "123456",
    "phone": "13800138000",
    "defaultAddress": "北京市朝阳区"
  }
  ```
- **响应示例**：
  - 成功：
    ```json
    {
      "code": 200,
      "msg": "操作成功",
      "data": 1,
      "timestamp": "2025-12-04T15:00:00.000+08:00"
    }
    ```
  - 失败：
    ```json
    {
      "code": 400,
      "msg": "手机号已存在",
      "data": null,
      "timestamp": "2025-12-04T15:00:00.000+08:00"
    }
    ```

### 2. 登录接口

- **URL**：`/api/user/login`
- **方法**：`POST`
- **请求体**：
  ```json
  {
    "account": "testuser",
    "password": "123456"
  }
  ```
  或
  ```json
  {
    "account": "13800138000",
    "password": "123456"
  }
  ```
- **响应示例**：
  - 成功：
    ```json
    {
      "code": 200,
      "msg": "操作成功",
      "data": {
        "id": 1,
        "username": "testuser",
        "phone": "13800138000",
        "role": "客户",
        "token": "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjEsInJvbGUiOiLmuK_lj6EiLCJpYXQiOjE3NTQxNzgwMDAsImV4cCI6MTc1NDE4NTIwMH0.7Z3Z3Z3Z3Z3Z3Z3Z3Z3Z3Z3Z3Z3Z3Z3Z3Z3Z3Z3Z3Z3Z3"
      },
      "timestamp": "2025-12-04T15:00:00.000+08:00"
    }
    ```
  - 失败：
    ```json
    {
      "code": 401,
      "msg": "账号或密码错误",
      "data": null,
      "timestamp": "2025-12-04T15:00:00.000+08:00"
    }
    ```

## 配置说明

### 数据库配置

在`application.yml`文件中配置数据库连接信息：

```yaml
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/wuyi_mall?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai
    username: root
    password: root
```

### JWT配置

在`application.yml`文件中配置JWT信息：

```yaml
jwt:
  # JWT密钥，建议生产环境使用复杂的随机字符串
  secret: wuyi_mall_jwt_secret_key_20251204
  # JWT有效期，单位：毫秒，2小时
  expire: 7200000
```

## 快速开始

### 1. 初始化数据库

执行`src/main/resources/sql/user.sql`脚本，创建数据库和用户表：

```bash
mysql -u root -p < src/main/resources/sql/user.sql
```

### 2. 配置数据库连接

修改`application.yml`文件中的数据库连接信息，确保与实际数据库配置一致。

### 3. 构建项目

```bash
mvn clean package -DskipTests
```

### 4. 运行项目

```bash
java -jar target/wuyi-mall-1.0-SNAPSHOT.jar
```

### 5. 测试API

使用Postman或其他API测试工具，测试注册和登录接口。

## Postman测试示例

### 1. 注册测试

- **请求方法**：`POST`
- **请求URL**：`http://localhost:8080/api/user/register`
- **请求头**：`Content-Type: application/json`
- **请求体**：
  ```json
  {
    "username": "testuser",
    "password": "123456",
    "phone": "13800138000",
    "defaultAddress": "北京市朝阳区"
  }
  ```
- **预期响应**：
  ```json
  {
    "code": 200,
    "msg": "操作成功",
    "data": 1,
    "timestamp": "2025-12-04T15:00:00.000+08:00"
  }
  ```

### 2. 登录测试

- **请求方法**：`POST`
- **请求URL**：`http://localhost:8080/api/user/login`
- **请求头**：`Content-Type: application/json`
- **请求体**：
  ```json
  {
    "account": "testuser",
    "password": "123456"
  }
  ```
- **预期响应**：
  ```json
  {
    "code": 200,
    "msg": "操作成功",
    "data": {
      "id": 1,
      "username": "testuser",
      "phone": "13800138000",
      "role": "客户",
      "token": "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjEsInJvbGUiOiLmuK_lj6EiLCJpYXQiOjE3NTQxNzgwMDAsImV4cCI6MTc1NDE4NTIwMH0.7Z3Z3Z3Z3Z3Z3Z3Z3Z3Z3Z3Z3Z3Z3Z3Z3Z3Z3Z3Z3Z3Z3"
    },
    "timestamp": "2025-12-04T15:00:00.000+08:00"
  }
  ```

## 注意事项

1. 项目使用Spring Boot 3.2.x，需要JDK 17或以上版本。
2. 数据库使用MySQL 8.0，需要确保数据库服务已启动。
3. 生产环境中，建议修改JWT密钥为复杂的随机字符串，提高安全性。
4. 生产环境中，建议关闭MyBatis Plus的SQL日志输出，避免敏感信息泄露。
5. 项目遵循Alibaba Java开发手册，代码注释完整，变量命名语义化，便于维护和扩展。

## 许可证

MIT License
>>>>>>> 12eb2494b0a37370213f21d14e78f93cca43bd9d
