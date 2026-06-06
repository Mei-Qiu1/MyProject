# 医院药品管理系统

## 项目简介

本项目是面向中小型医院的药品管理信息系统，采用前后端分离架构，基于Spring Boot + Vue 3技术栈开发。系统覆盖药品从采购入库到最终使用的全生命周期管理，实现药品管理的信息化、规范化和智能化。

系统支持7种业务角色：系统管理员、药剂师、医生、采购员、库存管理员、特殊药品管理员和药剂科主任，各角色根据职责分配不同的功能权限，确保业务操作的安全性和规范性。

## 技术架构

### 后端技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| Java | 21 | 运行环境 |
| Spring Boot | 3.2.0 | 应用框架 |
| Spring Security | 6.x | 安全认证框架 |
| MyBatis Plus | 3.5.5 | ORM持久层框架 |
| JWT | 0.12.5 | 无状态身份令牌 |
| MySQL | 8.0+ | 关系型数据库 |
| HikariCP | - | 数据库连接池 |

### 前端技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| Vue | 3.x | 前端框架 |
| Vue Router | 4.x | 路由管理 |
| Element Plus | 2.x | UI组件库 |
| Axios | 1.x | HTTP客户端 |
| Vite | 6.x | 构建工具 |

### 系统架构图

```
┌─────────────────────────────────────────────────────────────┐
│                        客户端浏览器                          │
│              Vue 3 + Element Plus + Axios                   │
└─────────────────────────────────────────────────────────────┘
                              │
                              │ HTTP/REST
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                      应用服务器                               │
│  ┌─────────────────────────────────────────────────────┐    │
│  │              Spring Boot 应用                         │    │
│  │  ┌─────────┐  ┌─────────┐  ┌─────────┐           │    │
│  │  │Controller│  │ Service │  │ Mapper   │           │    │
│  │  └─────────┘  └─────────┘  └─────────┘           │    │
│  │  ┌─────────────────────────────────────────┐       │    │
│  │  │        Spring Security + JWT             │       │    │
│  │  └─────────────────────────────────────────┘       │    │
│  └─────────────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────────────┘
                              │
                              │ JDBC
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                      数据库服务器                            │
│                    MySQL 8.0 (3307端口)                     │
│                   hospital_drug 数据库                       │
└─────────────────────────────────────────────────────────────┘
```

## 功能模块

系统共划分为9个功能模块，覆盖医院药品管理的完整业务流程。

### 1. 系统管理模块

系统管理模块是整个系统的基础支撑模块，为其他业务模块提供用户管理、权限控制、日志记录等基础服务。该模块包含四个子功能：用户管理功能负责系统用户的账号管理，支持用户的增删改查、密码修改和状态管理；角色权限功能负责定义和管理系统中的角色以及每个角色对应的功能权限，采用基于角色的访问控制（RBAC）模型；系统日志功能负责记录用户在系统中的所有操作行为，支持日志查询和审计追踪；数据字典功能负责管理系统中的通用基础数据，如药品剂型、处方类型、订单状态等。

### 2. 药品基础信息管理模块

药品基础信息管理模块是系统的核心基础模块，负责管理医院的所有药品信息和供应商信息。该模块包含五个子功能：药品字典功能存储医院所有在用药品的详细信息，包括药品编码、名称、规格、剂型、生产厂家、批准文号、价格信息、分类信息和存储要求等；药品分类功能采用两级分类结构，对药品进行药理分类和管理分类；供应商管理功能维护药品供应商的基本信息和资质信息，支持资质有效期预警；价格调整功能记录药品价格的变更历史，支持采购价、零售价和批发价的调整；预警阈值设置功能为药品设置最低库存警戒值，当库存低于警戒线时自动提醒相关人员。

### 3. 采购管理模块

采购管理模块负责医院药品的采购业务流程，从采购计划的制定到药品的到货入库，实现采购全过程的跟踪和管理。该模块包含五个子功能：采购计划功能允许采购人员根据库存预警信息和临床需求制定采购计划，支持设置计划的执行日期；采购申请功能是采购流程的核心环节，采购人员创建采购申请后需要经过药剂科主任审批；采购订单功能管理采购订单的执行过程，包括订单确认、发货跟踪、到货状态等；到货验收功能负责药品到货后的质量检查和数量核对工作；入库登记功能在验收合格后自动增加相应药品的库存。

### 4. 库存管理模块

库存管理模块负责医院药品的库存管理，确保药品的安全存储和账实相符。该模块包含五个子功能：库存查询功能提供药品库存的实时查询服务，支持按药品名称、仓库等条件筛选；库存预警功能包括低库存预警、效期预警和呆滞预警，帮助用户及时发现和处理库存异常情况；库存调拨功能支持在不同仓库之间进行药品的调拨操作，需要经过审批流程；库存盘点功能用于定期核对账面库存与实际库存是否一致，支持盘盈盘亏处理；库存流水功能记录所有库存变动的明细信息，实现库存的追溯管理。

### 5. 药房管理模块

药房管理模块是连接医生处方和药品发放的关键模块，实现处方从开具到发放的完整流程。该模块包含四个子功能：处方管理功能负责医生开具的处方的全生命周期管理，处方的状态包括待审核、已审核、已调配、已发药、已退药和已拒绝；处方审核功能由药剂师对医生开具的处方进行审核，确认处方的合理性和合法性；药品调配功能由药剂师按照处方明细进行药品的拣选和调配；发药确认功能在药品交付给患者或护士后确认发药，完成处方的最终闭环。

### 6. 临床用药管理模块

临床用药管理模块支持医生在诊疗过程中的用药管理功能，包括医嘱管理和用药记录等。该模块包含三个子功能：医嘱管理功能允许医生为住院患者开具长期医嘱或临时医嘱，支持医嘱的修改和停止；用药记录功能记录患者的实际用药情况，包括用药的药品信息、患者信息、医嘱信息和执行时间，用于临床用药的追溯和分析。

### 7. 特殊药品管理模块

特殊药品管理模块专门管理麻醉药品和第一类精神药品，由于这类药品的特殊性，需要严格的管理制度和双人核发制度。该模块包含三个子功能：特殊药品申请功能用于处理特殊药品的领用申请，需要审核申请人的资质和用药的合理性；使用记录功能详细记录特殊药品的使用情况，包括药品批号、使用数量、用途等；双人核发功能确保特殊药品的发放安全，每次发放需要两名经手人同时在场。

### 8. 统计报表模块

统计报表模块为管理人员提供数据分析和决策支持功能。该模块包含三个子功能：库存报表功能提供药品库存的各种统计分析视图，包括库存汇总表、库存明细表、库存预警表和效期分析表；采购报表功能统计药品采购的各种数据，包括采购汇总表、采购明细表、到货率统计和价格变动分析；用药分析功能分析医院的用药情况，包括处方统计表、药品使用排行和处方用药分布。

### 9. 工作台模块

工作台模块为不同角色的用户提供个性化的首页视图，包含系统概览、快速操作入口和待办事项提醒。系统管理员工作台提供系统运行状态、用户活跃度等统计信息；药剂师工作台显示待审核处方、待调配处方等任务列表；医生工作台提供处方管理、医嘱管理等快捷入口；采购员工作台显示采购订单跟踪、库存预警等提醒；药剂科主任工作台提供审批管理、科室统计等综合分析。

## 项目结构

```
HospitalApplication/
├── backend/                          # 后端项目
│   ├── src/main/java/com/example/hospital/
│   │   ├── controller/               # REST API控制器
│   │   │   ├── AuthController.java    # 认证接口
│   │   │   ├── DrugController.java    # 药品管理接口
│   │   │   ├── InventoryController.java # 库存管理接口
│   │   │   ├── PharmacyController.java # 药房管理接口
│   │   │   ├── PurchaseController.java # 采购管理接口
│   │   │   ├── ClinicalController.java # 临床用药接口
│   │   │   ├── SpecialController.java  # 特殊药品接口
│   │   │   ├── ReportController.java   # 报表接口
│   │   │   └── SystemController.java   # 系统管理接口
│   │   ├── service/                   # 业务逻辑层
│   │   │   ├── impl/                  # 业务逻辑实现
│   │   │   └── DrugService.java       # 药品业务接口
│   │   ├── mapper/                    # 数据访问层
│   │   │   └── DrugMapper.java        # 药品数据访问
│   │   ├── entity/                    # 实体类
│   │   │   ├── Drug.java              # 药品实体
│   │   │   ├── User.java              # 用户实体
│   │   │   └── Prescription.java     # 处方实体
│   │   ├── config/                    # 配置类
│   │   │   ├── SecurityConfig.java    # 安全配置
│   │   │   └── CorsConfig.java        # 跨域配置
│   │   ├── security/                  # 安全认证相关
│   │   │   ├── JwtAuthenticationFilter.java    # JWT过滤器
│   │   │   ├── JwtAuthenticationEntryPoint.java # 认证入口点
│   │   │   └── JwtTokenProvider.java            # Token提供者
│   │   ├── common/                    # 通用工具类
│   │   │   ├── Result.java            # 统一响应封装
│   │   │   └── Constants.java         # 常量定义
│   │   └── HospitalDrugManagementApplication.java # 启动类
│   ├── src/main/resources/
│   │   ├── application.yml            # 应用配置
│   │   ├── schema.sql                 # 数据库建表脚本
│   │   └── data.sql                   # 初始化数据脚本
│   ├── pom.xml                        # Maven依赖配置
│   └── start.bat                      # Windows启动脚本
│
├── frontend/                          # 前端项目
│   ├── src/
│   │   ├── views/                     # 页面组件
│   │   │   ├── Login.vue              # 登录页面
│   │   │   ├── Layout.vue             # 布局组件
│   │   │   ├── Dashboard.vue          # 首页
│   │   │   ├── drug/                  # 药品管理页面
│   │   │   │   ├── DrugList.vue       # 药品列表
│   │   │   │   └── DrugCategory.vue   # 药品分类
│   │   │   ├── inventory/             # 库存管理页面
│   │   │   │   └── InventoryList.vue # 库存列表
│   │   │   ├── pharmacy/              # 药房管理页面
│   │   │   │   └── PrescriptionList.vue # 处方列表
│   │   │   ├── doctor/                # 医生页面
│   │   │   │   └── DispensingManage.vue # 处方管理
│   │   │   ├── purchase/             # 采购管理页面
│   │   │   ├── clinical/             # 临床用药页面
│   │   │   ├── special/              # 特殊药品页面
│   │   │   ├── reports/              # 统计报表页面
│   │   │   ├── system/               # 系统管理页面
│   │   │   └── dashboard/            # 各角色工作台
│   │   ├── router/
│   │   │   └── index.js               # 路由配置（含权限控制）
│   │   ├── utils/
│   │   │   └── axios.js              # Axios配置（请求拦截器）
│   │   ├── App.vue                   # 根组件
│   │   └── main.js                   # 入口文件
│   ├── index.html
│   ├── package.json                  # 前端依赖
│   └── vite.config.js               # Vite配置
│
├── 医院药品管理系统-系统设计报告书.md  # 系统设计文档
└── README.md                         # 项目说明文档
```

## 数据库设计

系统数据库采用MySQL 8.0，共设计27张核心业务表，涵盖系统管理、药品管理、库存管理、处方管理和采购管理五大业务领域。数据库设计遵循第三范式，通过外键关联保证数据的一致性和完整性。

### 主要数据表

| 序号 | 表名 | 说明 |
|------|------|------|
| 1 | sys_user | 系统用户表 |
| 2 | sys_role | 角色表 |
| 3 | sys_menu | 菜单表 |
| 4 | sys_role_menu | 角色菜单关联表 |
| 5 | sys_log | 系统日志表 |
| 6 | sys_dict | 数据字典表 |
| 7 | drug | 药品表 |
| 8 | drug_category | 药品分类表 |
| 9 | supplier | 供应商表 |
| 10 | warehouse | 仓库表 |
| 11 | inventory | 库存表 |
| 12 | inventory_record | 库存流水表 |
| 13 | inventory_transfer | 库存调拨表 |
| 14 | inventory_check | 库存盘点表 |
| 15 | prescription | 处方表 |
| 16 | prescription_detail | 处方明细表 |
| 17 | medical_order | 医嘱表 |
| 18 | medical_order_detail | 医嘱明细表 |
| 19 | purchase_plan | 采购计划表 |
| 20 | purchase_request | 采购申请表 |
| 21 | purchase_order | 采购订单表 |
| 22 | goods_receipt | 到货验收表 |
| 23 | special_drug_apply | 特殊药品申请表 |
| 24 | special_drug_record | 特殊药品使用记录表 |
| 25 | drug_delivery | 药品配送表 |
| 26 | medication_record | 用药记录表 |
| 27 | price_adjustment | 价格调整记录表 |

详细数据库设计请参阅《数据库设计文档.md》。

## 快速开始

### 环境要求

| 软件 | 版本要求 | 说明 |
|------|----------|------|
| JDK | 21+ | 必须使用Java 21或更高版本 |
| MySQL | 8.0+ | 数据库，需支持utf8mb4字符集 |
| Node.js | 18+ | 前端构建工具 |
| Maven | 3.8+ | 后端依赖管理（可选） |

### 1. 数据库配置

确保MySQL服务已启动，并创建数据库：

```sql
-- 创建数据库（必须使用utf8mb4字符集）
CREATE DATABASE hospital_drug 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

-- 创建专用用户（推荐）
CREATE USER 'hospital_user'@'localhost' IDENTIFIED BY 'hospital123';
GRANT ALL PRIVILEGES ON hospital_drug.* TO 'hospital_user'@'localhost';
FLUSH PRIVILEGES;
```

### 2. 后端配置

修改配置文件：`backend/src/main/resources/application.yml`

```yaml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:mysql://localhost:3307/hospital_drug?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true&useSSL=false
    username: hospital_user
    password: hospital123
    driver-class-name: com.mysql.cj.jdbc.Driver

jwt:
  secret: hospital_drug_management_system_jwt_secret_key_32bit
  expiration: 86400000
```

### 3. 启动后端

```bash
cd backend
mvn spring-boot:run
```

启动成功标志：显示 `Started HospitalDrugManagementApplication in X seconds`

### 4. 启动前端

```bash
cd frontend
npm install
npm run dev
```

启动成功标志：显示 `ready in X ms` 和 `Local: http://localhost:5173/`

### 5. 访问系统

打开浏览器访问：http://localhost:5173

使用以下测试账号登录：

| 用户名 | 密码 | 角色 | 权限说明 |
|--------|------|------|----------|
| admin | admin123 | 系统管理员 | 所有功能 |
| pharmacist | admin123 | 药剂师 | 药品管理、库存管理、药房管理 |
| doctor | admin123 | 医生 | 医嘱管理、处方开具 |
| purchaser | admin123 | 采购员 | 采购管理 |
| stock | admin123 | 库存管理员 | 库存管理 |
| special | admin123 | 特殊药品管理员 | 特殊药品管理 |
| director | admin123 | 药剂科主任 | 审批管理、统计报表 |

## 关键配置说明

### 数据库连接池配置

后端使用HikariCP连接池，配置参数位于application.yml：

```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 10       # 最大连接数
      minimum-idle: 5            # 最小空闲连接
      connection-timeout: 30000  # 连接超时（毫秒）
```

### JWT认证配置

JWT用于用户身份认证，配置参数说明：

```yaml
jwt:
  secret: JWT签名密钥（至少32位字符）
  expiration: token过期时间（毫秒），默认24小时
```

### CORS跨域配置

开发环境已配置允许前端开发服务器访问。如需调整，修改SecurityConfig.java中的allowedOriginPatterns配置。

## 常见问题

### 数据库连接失败

检查MySQL服务是否启动，数据库配置是否正确，数据库是否已创建。

### 前端请求报401/403错误

检查JWT密钥配置是否正确，检查用户角色是否有访问权限。

### 页面显示无数据

检查数据库中是否有对应数据，检查后端API是否正常返回。

## 部署说明

### 生产环境部署

**后端打包部署：**

```bash
cd backend
mvn clean package -DskipTests
java -jar target/hospital-drug-management-1.0.0.jar
```

**前端打包部署：**

```bash
cd frontend
npm run build
# 将dist目录部署到Nginx
```

### Nginx配置示例：

```nginx
server {
    listen 80;
    server_name your-domain.com;
    
    location / {
        root /path/to/dist;
        index index.html;
        try_files $uri $uri/ /index.html;
    }
    
    location /api {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```

## API接口说明

系统采用RESTful API设计风格，主要接口路径如下：

| 模块 | 接口路径 | 说明 |
|------|----------|------|
| 认证 | /auth/login | 用户登录 |
| 药品 | /drugs/** | 药品管理接口 |
| 库存 | /inventory/** | 库存管理接口 |
| 处方 | /pharmacy/** | 药房管理接口 |
| 采购 | /purchase/** | 采购管理接口 |
| 临床 | /clinical/** | 临床用药接口 |
| 特殊 | /special/** | 特殊药品接口 |
| 报表 | /reports/** | 统计报表接口 |
| 系统 | /system/** | 系统管理接口 |

## 版本信息

- 版本号：V1.0.0
- 发布日期：2024年6月
- 开发团队：医院药品管理系统开发组

## 许可证

MIT License
