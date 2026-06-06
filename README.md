# 医院药品管理系统

## 项目简介

本项目是面向中小型医院的药品管理信息系统，覆盖药品从采购入库到最终使用的全生命周期管理，实现药品管理的信息化、规范化和智能化。

## 技术栈

### 后端
- Java 21
- Spring Boot 3.2.0
- MyBatis Plus 3.5.5
- Spring Security
- JWT
- MySQL 8.0+

### 前端
- Vue 3
- Vue Router 4
- Element Plus
- Axios
- Vite

## 功能模块

### 1. 系统管理模块
- 用户管理：增删改查系统用户，设置用户状态
- 角色权限管理：定义角色，分配菜单和功能权限
- 数据字典管理：维护药品分类、剂型、单位等基础数据
- 系统日志：记录操作日志，支持审计追踪

### 2. 药品基础信息管理模块
- 药品字典维护：药品编码、名称、规格、剂型、生产厂家、批准文号
- 药品分类管理：按药理分类、管理分类维护
- 供应商管理：供应商信息、资质证书、合作状态维护
- 价格管理：采购价、零售价、批发价维护
- **预警阈值设置**：为药品设置库存预警阈值，低于阈值自动触发预警

### 3. 采购管理模块
- 采购计划：根据库存预警和消耗趋势自动生成采购建议
- 采购申请：填写采购申请单，提交审批
- 采购订单：根据审批通过的申请生成订单
- 到货验收：核对到货药品与订单一致性
- 入库登记：验收合格后办理入库

### 4. 库存管理模块
- 入库管理：采购入库、退药入库、调拨入库
- 出库管理：领用出库、调拨出库、报损出库
- 库存调拨：支持药库与药房之间的调拨
- 库存盘点：定期/不定期盘点，盘盈盘损处理
- 库存预警：低库存预警（建议采购量=预警阈值×3）、效期预警

### 5. 药房管理模块
- 处方管理：接收门诊/住院处方，处方审核
- 药品调配：按处方配药，支持预调配
- 发药管理：核对患者身份后发药
- 退药管理：支持未发药处方撤销、已发药退药处理

### 6. 临床用药管理模块
- 医嘱管理：接收医生医嘱，生成用药需求
- 药品配送：根据医嘱生成配送单
- 用药记录：记录患者用药情况

### 7. 特殊药品管理模块
- 专库管理：毒麻精放药品独立库位管理
- 双人双锁：领用需双人授权
- 专用处方：红色处方管理，限量管理
- 空安瓿回收：注射剂空安瓿回收登记

### 8. 统计报表模块
- 库存报表：实时库存表、库存周转率报表
- 采购报表：采购明细、供应商供货统计
- 消耗报表：药品消耗排名、科室消耗统计
- ABC分析：按金额/数量进行ABC分类分析

### 9. 工作台模块
- 系统管理员工作台：系统概览、快速操作
- 药剂科主任工作台：审批管理、统计分析
- 药剂师工作台：处方审核、药品调配
- 医生工作台：医嘱管理、处方开具
- 采购员工作台：采购管理、订单跟踪

## 项目结构

```
backend/
├── src/main/java/com/example/hospital/
│   ├── controller/     # REST API控制器
│   ├── service/        # 业务逻辑层
│   ├── mapper/         # 数据访问层
│   ├── entity/         # 实体类
│   ├── config/         # 配置类（数据源、安全、CORS等）
│   ├── security/       # JWT认证相关
│   ├── common/         # 通用工具类和响应封装
│   └── HospitalDrugManagementApplication.java
├── src/main/resources/
│   ├── application.yml # 应用配置（数据库连接、JWT等）
│   ├── schema.sql      # 数据库建表脚本
│   └── data.sql        # 初始化数据（用户、角色、菜单等）
├── pom.xml             # Maven依赖管理
└── start.bat           # Windows启动脚本

frontend/
├── src/
│   ├── views/          # 页面组件（按模块划分）
│   ├── router/         # 路由配置（含权限控制）
│   ├── utils/          # 工具函数（Axios配置等）
│   ├── App.vue         # 根组件
│   └── main.js         # 入口文件
├── index.html
├── package.json        # 前端依赖
└── vite.config.js      # Vite配置
```

## 快速开始

### 环境要求

| 软件 | 版本 | 说明 |
|------|------|------|
| JDK | 21+ | 必须使用Java 21或更高版本 |
| MySQL | 8.0+ | 数据库，需支持utf8mb4字符集 |
| Node.js | 18+ | 前端构建工具 |
| Maven | 3.8+ | 后端依赖管理（可选，IDEA自带） |

### 数据库配置

**注意事项：**
1. 确保MySQL服务已启动
2. 创建数据库时必须使用utf8mb4字符集，否则可能导致中文乱码
3. 建议创建专门的数据库用户，避免使用root用户

```sql
-- 1. 创建数据库
CREATE DATABASE hospital_drug CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 2. 创建用户（推荐）
CREATE USER 'hospital_user'@'localhost' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON hospital_drug.* TO 'hospital_user'@'localhost';
FLUSH PRIVILEGES;
```

### 后端配置与启动

**配置文件说明：**
文件路径：`backend/src/main/resources/application.yml`

```yaml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/hospital_drug?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true&useSSL=false
    username: hospital_user  # 替换为实际用户名
    password: your_password  # 替换为实际密码
    driver-class-name: com.mysql.cj.jdbc.Driver
    hikari:
      maximum-pool-size: 10
      minimum-idle: 5
      connection-timeout: 30000

mybatis-plus:
  configuration:
    map-underscore-to-camel-case: true
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
  global-config:
    db-config:
      id-type: auto

# JWT配置
jwt:
  secret: your_jwt_secret_key_here_must_be_at_least_32_characters
  expiration: 86400000  # 过期时间（毫秒），默认24小时

# CORS配置（前端开发环境）
cors:
  allowed-origins: http://localhost:5173
```

**启动步骤：**

方法一：使用Maven命令
```bash
cd backend
mvn spring-boot:run
```

方法二：使用启动脚本（Windows）
```bash
cd backend
.\start.bat
```

方法三：使用IDEA运行
1. 打开backend目录
2. 找到`HospitalDrugManagementApplication.java`
3. 右键选择"Run"

**启动成功标志：**
```
Started HospitalDrugManagementApplication in X seconds (process running)
```

### 前端配置与启动

**配置说明：**
前端通过Vite配置代理，将API请求转发到后端。配置文件：`frontend/vite.config.js`

```javascript
export default defineConfig({
  plugins: [vue()],
  server: {
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, '')
      }
    }
  }
})
```

**启动步骤：**

```bash
# 1. 进入前端目录
cd frontend

# 2. 安装依赖（首次运行）
npm install

# 3. 启动开发服务器
npm run dev
```

**启动成功标志：**
```
  VITE v6.x.x  ready in X ms
  ➜  Local:   http://localhost:5173/
```

### 访问系统

1. 打开浏览器访问：http://localhost:5173
2. 使用以下账号登录：

| 用户名 | 密码 | 角色 | 权限说明 |
|--------|------|------|----------|
| admin | admin123 | 系统管理员 | 所有功能权限 |
| yaoji | admin123 | 药剂师 | 药品管理、库存管理、药房管理 |
| doctor | admin123 | 医生 | 医嘱管理、处方开具 |
| caigou | admin123 | 采购员 | 采购管理 |
| guanli | admin123 | 药剂科主任 | 审批管理、统计报表 |

## 关键配置说明

### 1. 数据库连接池配置
后端使用HikariCP连接池，默认配置：
- 最大连接数：10
- 最小空闲连接：5
- 连接超时：30秒

如需调整，修改`application.yml`中的`spring.datasource.hikari`配置。

### 2. JWT安全配置
- **secret**：JWT签名密钥，必须至少32位字符
- **expiration**：token过期时间（毫秒）

生产环境建议使用环境变量注入敏感配置。

### 3. CORS跨域配置
开发环境已配置允许`http://localhost:5173`访问。生产环境部署时，需修改`WebConfig.java`或`application.yml`中的允许来源。

### 4. 数据初始化
系统启动时会自动执行`data.sql`初始化数据：
- 预置用户、角色、菜单数据
- 预置药品分类、仓库等基础数据

**注意**：每次启动都会执行`data.sql`，如需避免重复插入，请使用`INSERT IGNORE`或条件判断。

## 常见问题与解决方案

### 问题1：数据库连接失败
**现象**：启动时报错`Cannot connect to database`

**解决方案**：
1. 检查MySQL服务是否启动：`net start mysql`（Windows）
2. 检查数据库配置是否正确（用户名、密码、端口）
3. 确保数据库已创建：`CREATE DATABASE hospital_drug`
4. 检查防火墙是否允许3306端口

### 问题2：前端请求报401/403错误
**现象**：登录后访问页面报401 Unauthorized或403 Forbidden

**解决方案**：
1. 检查JWT密钥是否配置正确
2. 检查token是否过期（默认24小时）
3. 检查用户角色权限配置是否正确

### 问题3：页面显示"No Data"
**现象**：工作台或列表页面显示无数据

**解决方案**：
1. 检查数据库中是否有对应数据
2. 检查后端API是否正常返回数据（浏览器F12查看Network）
3. 检查SQL查询条件是否正确（如日期范围、状态条件）

### 问题4：500 Internal Server Error
**现象**：页面操作时报500错误

**解决方案**：
1. 查看后端控制台日志，定位具体错误原因
2. 常见原因：数据库表结构不匹配、SQL语法错误、空指针异常
3. 检查`schema.sql`是否已正确执行

### 问题5：前端样式错乱
**现象**：页面样式显示异常

**解决方案**：
1. 清除浏览器缓存：Ctrl+Shift+R
2. 重新安装依赖：`npm install`
3. 检查Element Plus是否正确引入

## 部署说明

### 开发环境
- 后端：使用`mvn spring-boot:run`启动
- 前端：使用`npm run dev`启动

### 生产环境

**后端打包：**
```bash
cd backend
mvn clean package
java -jar target/hospital-drug-management-1.0.0.jar
```

**前端打包：**
```bash
cd frontend
npm run build
# 将dist目录部署到Nginx或静态服务器
```

## API文档

后端服务启动后，可通过以下地址访问API文档：
- Swagger UI：http://localhost:8080/swagger-ui.html（需添加swagger依赖）

## 许可证

MIT License

## 技术支持

如有问题，请联系开发团队或查看项目文档。
