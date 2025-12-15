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



