# 旅游管理系统

基于 `Spring Boot 3.3 + Spring 
Security + JWT + JPA + MySQL 8.0` 和 `Vue 3 + Pinia + Element Plus + Axios` 的前后端分离旅游管理系统。项目面向旅游路线展示、在线预订、景点查询和后台运营管理场景，采用 B/S 架构，后端提供 REST API，前端负责门户站点与管理员后台。

## 1. 系统目标

- 实现旅游路线查询与展示
- 提供景点、公告等公共信息查询
- 支持旅客注册、登录、在线预订和订单查询
- 支持管理员进行路线、景点、用户、订单、公告等后台管理
- 支持查看基础运营统计数据

说明：
- 当前仓库已完整实现“路线、酒店、景点、订单、公告、用户、统计”各业务主干能力与管理闭环。
- 已包含酒店资源集中管理（`Hotel` 实体、接口、后台管理页面及路线关联）。

## 2. 技术栈

### 后端

- `Spring Boot 3.3.0`
- `Spring Security`
- `JWT (jjwt 0.11.5)`
- `Spring Data JPA`
- `MySQL 8.0`
- `Spring Validation`
- `springdoc-openapi`

### 前端

- `Vue 3`
- `Pinia`
- `Vue Router`
- `Element Plus`
- `Axios`
- `Vite`
- `ECharts`

## 3. 架构设计

### 3.1 总体架构

- 架构形态：前后端分离 B/S 架构
- 前端职责：页面渲染、路由守卫、表单交互、调用 REST API
- 后端职责：认证鉴权、业务处理、数据持久化、统一异常返回
- 数据层：MySQL 8.0

### 3.2 后端分层

项目实际采用：

- `Controller`：对外暴露 REST 接口
- `Service`：处理业务逻辑和事务
- `Repository`：基于 JPA 完成数据访问
- `Entity/DTO/VO`：实体与请求对象

说明：
- 文档要求写作 `Controller / Service / Mapper` 三层。
- 当前仓库未使用 MyBatis-Plus，也没有 `Mapper`，而是由 `Spring Data JPA Repository` 承担数据访问层职责。文档表述时可视为 `Mapper/DAO` 的 JPA 实现版本。

### 3.3 通用能力

- 统一响应封装：当前实现类名为 `ApiResponse<T>`，语义等同于 `Result<T>`
- 分页结果封装：`PageResult<T>`
- 全局异常处理：`GlobalExceptionHandler`
- 事务控制：订单、用户、路线、景点、公告写操作均使用 `@Transactional`

## 4. 主要功能模块

### 4.1 用户注册与登录

- 接口：`/api/auth/login`、`/api/auth/register`
- 认证方式：JWT 无状态认证
- 密码处理：`BCryptPasswordEncoder`
- 角色划分：`ADMIN`、`USER`
- 系统启动时自动初始化管理员账号：
  - 用户名：`admin`
  - 密码：`admin123`

### 4.2 旅游路线管理

- 后台接口前缀：`/api/admin/routes`
- 已支持能力：
  - 路线分页查询
  - 路线详情查询
  - 新增路线（支持下拉关联合作酒店）
  - 修改路线
  - 删除路线
- 路线状态字段：
  - `DRAFT`
  - `PUBLISHED`
  - `OFFLINE`

说明：
- “上下架”能力通过 `status` 字段控制，通过新增/编辑时维护状态实现。
- 路线与合作酒店（`Hotel`）已建立多对一外键关联。

### 4.3 酒店管理

- 后台接口前缀：`/api/admin/hotels`
- 已支持能力：
  - 酒店列表与分页查询（支持关键词、状态筛选）
  - 酒店详情查询
  - 新增酒店（包含城市、地址、联系电话、星级、描述等）
  - 编辑酒店信息
  - 启用/停用状态切换（`ACTIVE` / `INACTIVE`）
  - 删除酒店
- 业务联动：在路线管理的新增/编辑弹窗中，可直接下拉选择并关联合作酒店。

### 4.4 景点管理

- 前台公共查询接口：`/api/public/attractions`
- 后台管理接口前缀：`/api/admin/attractions`
- 已支持能力：
  - 景点分页查询
  - 景点详情查询
  - 新增/修改/删除
  - 状态过滤

### 4.5 订单管理

- 用户接口：
  - `POST /api/orders` 提交订单
  - `GET /api/orders` 查询本人订单
- 管理员接口：
  - `GET /api/admin/orders`
  - `GET /api/admin/orders/{id}`
  - `PATCH /api/admin/orders/{id}/status`（支持流转推进及填写拒绝原因 `rejectReason`）
  - `DELETE /api/admin/orders/{id}`

当前订单状态枚举：

- `PENDING`（待确认）
- `CONFIRMED`（已确认）
- `REJECTED`（已拒绝）
- `PAID`（已支付）
- `CANCELLED`（已取消）
- `REFUNDING`（退款中）
- `REFUNDED`（已退款）
- `COMPLETED`（已完成）

说明：
- 订单创建及状态流转已使用事务保证原子性。
- 完整支持“提交/确认/拒绝/取消”流程：管理员在后台可对 `PENDING` 订单进行确认或拒绝（拒绝强制填写原因），前台旅客在“我的订单”可清晰查看拒绝原因。

### 4.6 统计报表与数据导出

- 统计数据接口：`GET /api/admin/stats`
- 数据导出接口：`GET /api/admin/stats/export`（生成并下载标准 CSV 文件）
- 当前已提供指标：
  - 路线总数
  - 景点总数
  - 用户总数
  - 订单总数
  - 今日订单数
  - 待处理订单数
  - 总营收

说明：
- 服务端使用 `StatsService` 汇总关键指标并生成带 UTF-8 BOM 的标准 CSV 文本流。
- 前端管理后台仪表盘（Dashboard）已集成“导出 CSV”功能，点击直接触发文件下载。

### 4.7 公共门户

前台已提供：

- 首页 `/`
- 路线列表 `/routes`
- 路线详情 `/routes/:id`
- 景点列表 `/attractions`
- 景点详情 `/attractions/:id`
- 我的订单 `/my-orders`（含订单状态展示及拒绝原因提示）
- 登录 `/login`
- 注册 `/register`

### 4.8 管理后台

后台已提供：

- 仪表盘 `/admin/dashboard`（含数据指标统计与 CSV 导出功能）
- 路线管理 `/admin/routes`（支持维护路线信息及关联合作酒店）
- 酒店管理 `/admin/hotels`（支持酒店 CRUD 与状态启停用）
- 景点管理 `/admin/attractions`
- 订单管理 `/admin/orders`（支持确认、拒绝并记录原因、取消与状态推进）
- 用户管理 `/admin/users`
- 公告管理 `/admin/notices`
- 设置页 `/admin/settings`

## 5. 现有项目结构

```text
travelsystem
├─ src/main/java/com/ts
│  ├─ common        # 通用响应与分页封装
│  ├─ config        # 安全、配置类
│  ├─ controller    # REST 接口层
│  ├─ dto           # 请求参数对象
│  ├─ entity        # JPA 实体
│  ├─ exception     # 业务异常与全局异常处理
│  ├─ repository    # JPA Repository 数据访问层
│  ├─ security      # JWT 工具、过滤器、用户加载
│  └─ service       # 业务服务层
├─ src/main/resources
│  └─ application.yml
└─ web
   ├─ src/api       # 前端接口封装
   ├─ src/router    # 前端路由
   ├─ src/stores    # Pinia 状态管理
   └─ src/views     # 前台与后台页面
```

## 6. 安全设计

- 认证模式：JWT 无状态认证
- 会话策略：`SessionCreationPolicy.STATELESS`
- 密码加密：`BCryptPasswordEncoder`
- 角色权限：
  - `/api/auth/**`、`/api/public/**`、`/api/health` 免认证
  - `/api/admin/**` 仅允许 `ADMIN`
  - 其他接口默认需要登录
- 前端控制：
  - `Pinia` 持久化 token
  - 路由守卫控制普通用户和管理员访问范围

## 7. 数据库设计说明

数据库连接配置：

- 数据库名：`travelsystem`
- 地址：`localhost:3306`
- 用户名：`root`（环境变量 `DB_USERNAME`，默认值 `root`）
- 密码：`123456`（环境变量 `DB_PASSWORD`，默认值 `123456`）

当前核心实体：

- `User`（用户表 `ts_user`）
- `TourRoute`（旅游线路表 `ts_tour_route`）
- `Hotel`（酒店表 `ts_hotel`）
- `Order`（订单表 `ts_order`）
- `Attraction`（景点表 `ts_attraction`）
- `Notice`（公告表 `ts_notice`）
- `Review`（评价表 `ts_review`）

说明：
- JPA 配置为 `ddl-auto: update`，启动时会按实体自动同步表结构。
- 核心唯一约束与检索索引已全面显式配置：
  - 唯一约束：`ts_user.username`、`ts_order.order_no`
  - 订单表索引：`idx_ts_order_status`、`idx_ts_order_created_at`、`idx_ts_order_user_id`、`idx_ts_order_route_id`
  - 路线表索引：`idx_ts_route_status`、`idx_ts_route_destination`、`idx_ts_route_hotel_id`
  - 景点表索引：`idx_ts_attraction_status`、`idx_ts_attraction_location`
  - 酒店表索引：`idx_ts_hotel_city`、`idx_ts_hotel_status`、`idx_ts_hotel_name`
  - 用户表索引：`idx_ts_user_username`

## 8. 部署与运行

### 8.1 后端

后端默认端口：

- `8080`

运行前准备：

1. 启动 MySQL 8.0
2. 创建数据库 `travelsystem`
3. 按 `src/main/resources/application.yml` 配置账号密码

启动命令示例：

```bash
mvn spring-boot:run
```

或：

```bash
mvn -s maven.settings.xml spring-boot:run
```

健康检查：

```text
GET /api/health
```

Swagger：

```text
http://localhost:8080/swagger-ui.html
```

### 8.2 前端

进入前端目录：

```bash
cd web
```

安装依赖并启动：

```bash
npm install
npm run dev
```

Vite 开发代理：

- 已配置将 `/api` 代理到 `http://localhost:8080`

### 8.3 生产环境 Nginx

示例配置：

```nginx
server {
    listen 80;
    server_name your-domain;

    location / {
        root   /data/www/travelsystem;
        index  index.html;
        try_files $uri $uri/ /index.html;
    }

    location /api/ {
        proxy_pass http://127.0.0.1:8080/api/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

## 9. 要求与现状对照

| 文档要求 | 当前状态 | 说明 |
| --- | --- | --- |
| 用户注册/登录 + JWT | 已实现 | 支持登录、注册、JWT 鉴权 |
| 路线管理增删改查 + 上下架 | 已实现 | 通过状态字段维护 `DRAFT/PUBLISHED/OFFLINE`，支持关联合作酒店 |
| 景点管理 | 已实现 | 前后台均已具备完备 CRUD 与展示 |
| 订单提交/确认/拒绝/取消 | 已实现 | 支持 `REJECTED` 状态与拒绝原因，严格状态机流转控制 |
| 统计报表导出 CSV | 已实现 | 后端提供 `/api/admin/stats/export`，前端仪表盘一键导出 CSV |
| 管理员后台与运营数据 | 已实现 | 具备全套管理后台页面与实时运营指标统计 |
| 酒店资源集中管理 | 已实现 | 具备 `Hotel` 实体、后台 CRUD、状态切换及路线关联 |
| 统一 `Result<T>` | 已实现（类名等价） | 当前类名为 `ApiResponse<T>`，语义与结构等价 |
| MVC 三层 + Mapper | 已实现（JPA实现） | 采用成熟的 `Controller + Service + Repository(JPA)` 分层架构 |
| 事务保证订单原子性 | 已实现 | `OrderService#create`、`updateStatus` 等写操作均使用 `@Transactional` |
| JWT 无状态认证 | 已实现 | Spring Security + JWT，无会话存储 |
| 角色权限控制 | 已实现 | 基于角色的权限隔离（`ADMIN` / `USER`） |
| BCrypt 密码加密 | 已实现 | 后端登录、注册及管理员初始化均使用 BCrypt 加密 |
| 关键字段索引优化 | 已实现 | 订单、线路、酒店、景点、用户等表关键业务字段均已显式配置索引 |

## 10. 后续建议

- 补充自动化单元测试：在 `src/test/java` 中补齐订单流转、CSV 导出及鉴权等关键用例
- 前台酒店查询扩展：根据展示需求决定是否在 `PublicController` 增加公共酒店列表与详情接口
- 路线一键上下架：可在线路管理表格操作列增加快捷“发布/下架”按钮，免去进入编辑弹窗
- 统一响应命名别名：视严格验收标准决定是否为 `ApiResponse<T>` 增加名为 `Result<T>` 的类型别名或过渡类

## 11. 开发环境说明

- Java：`21`
- MySQL：`8.0`
- Node.js：建议使用当前 Vite 版本兼容的 LTS 版本
- JWT 配置：
  - `secret` 已配置
  - `access-token-validity = 7200` 秒（2小时）
- CORS：
  - 开发环境允许所有来源：`*`

