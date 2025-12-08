# 武夷商城项目

## 技术栈

- 后端：Spring Boot 3.2.0 + Maven
- 前端：Vue 3 + Vite
- 数据库：MySQL 8.0

## 项目结构

```
wuyi-mall/
├── backend/              # 后端 Spring Boot 项目
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── com/
│   │   │   │       └── wuyi/
│   │   │   │           └── mall/   # 主代码目录
│   │   │   └── resources/          # 资源文件目录
│   │   └── test/
│   │       └── java/               # 测试代码目录
│   └── pom.xml                     # Maven 配置文件
├── frontend/             # 前端 Vue 项目
│   ├── public/           # 静态资源目录
│   ├── src/              # 主代码目录
│   │   ├── assets/       # 资源文件
│   │   ├── components/   # Vue 组件
│   │   ├── App.vue       # 根组件
│   │   └── main.js       # 入口文件
│   ├── index.html        # HTML 模板
│   ├── package.json      # npm 配置文件
│   └── vite.config.js    # Vite 配置文件
├── init.sql              # MySQL 初始化脚本
└── README.md             # 项目说明文档
```

## 初始化步骤

### 1. 数据库初始化

```bash
# 执行 MySQL 初始化脚本
mysql -u root -p < init.sql
```

### 2. 后端项目初始化

```bash
# 进入后端目录
cd backend

# 安装依赖
mvn install

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

访问地址：http://localhost:8080

### 前端

```bash
cd frontend
npm run dev
```

访问地址：http://localhost:5173

## 项目说明

本项目是一个基于 Spring Boot + Vue + MySQL 的电商平台，包含用户管理、商品管理等功能。
