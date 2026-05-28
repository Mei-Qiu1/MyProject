
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
- MySQL

### 前端
- Vue 3
- Vue Router 4
- Element Plus
- Axios

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
- 库存预警：低库存预警、效期预警

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

## 项目结构

```
backend/
├── src/main/java/com/example/hospital/
│   ├── controller/     # REST API控制器
│   ├── service/        # 业务逻辑层
│   ├── mapper/         # 数据访问层
│   ├── entity/         # 实体类
│   ├── config/         # 配置类
│   ├── security/       # 安全相关
│   ├── common/         # 通用工具类
│   └── HospitalDrugManagementApplication.java
├── src/main/resources/
│   ├── application.yml # 应用配置
│   ├── schema.sql      # 数据库建表脚本
│   └── data.sql        # 初始化数据

frontend/
├── src/
│   ├── views/          # 页面组件
│   ├── router/         # 路由配置
│   ├── utils/          # 工具函数
│   ├── App.vue         # 根组件
│   └── main.js         # 入口文件
├── index.html
├── package.json
└── vite.config.js
```

## 快速开始

### 环境要求
- JDK 21+
- MySQL 8.0+
- Node.js 18+

### 后端启动

1. 创建数据库：
```sql
CREATE DATABASE hospital_drug CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. 执行数据库初始化脚本：
```bash
mysql -u root -p hospital_drug < backend/src/main/resources/schema.sql
mysql -u root -p hospital_drug < backend/src/main/resources/data.sql
```

3. 修改数据库配置（backend/src/main/resources/application.yml）：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/hospital_drug
    username: root
    password: your_password
```

4. 启动后端服务：
```bash
cd backend
mvn spring-boot:run
```

### 前端启动

1. 安装依赖：
```bash
cd frontend
npm install
```

2. 启动开发服务器：
```bash
npm run dev
```

3. 访问地址：http://localhost:5173

## 登录账号

| 用户名 | 密码 | 角色 |
|--------|------|------|
| admin | admin123 | 系统管理员 |
| pharmacist | admin123 | 药剂师 |
| purchaser | admin123 | 采购员 |

## API文档

后端服务启动后，API文档地址：http://localhost:8080/api/swagger-ui.html

## 许可证

MIT License
