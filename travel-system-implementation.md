# 旅游管理系统实施计划

## 1. 文档目的

本文档用于指导 `travelsystem` 项目从“当前可运行版本”推进到“满足课程/项目核心要求的交付版本”。重点不是重写整个系统，而是在现有 Spring Boot + Vue 3 架构基础上，补齐缺失模块、完善业务流程、增强统计与部署能力，并形成可验收的最终成果。

## 2. 当前基线

### 2.1 已具备能力

- 后端已基于 `Spring Boot 3.3.0 + Spring Security + JWT + JPA + MySQL 8.0`
- 前端已基于 `Vue 3 + Pinia + Vue Router + Element Plus + Axios`
- 已有核心实体：`User`、`TourRoute`、`Order`、`Attraction`、`Review`、`Notice`
- 已有控制器：`AuthController`、`UserController`、`RouteController`、`OrderController`、`AttractionController`、`NoticeController`、`PublicController`、`StatsController`
- 已有统一响应封装：`ApiResponse<T>`
- 已有全局异常处理：`GlobalExceptionHandler`
- 已有 JWT 认证与 `ADMIN/USER` 权限控制
- 已有前台门户和管理员后台基础页面

### 2.2 当前缺口

结合代码现状与目标要求，当前主要缺口如下：

1. 酒店资源集中管理模块未实现
2. 统计报表 `CSV` 导出未实现
3. 订单流程缺少明确“拒绝”状态与拒绝原因
4. 数据库业务检索索引未显式完善
5. 统一返回类名与文档要求中的 `Result<T>` 不一致
6. 路线“上下架”目前依赖状态字段，缺少更明确的后台操作表达

## 3. 实施目标

本轮实施目标分为两类：

### 3.1 必达目标

- 补齐酒店资源管理模块
- 完善订单状态流转，支持提交、确认、拒绝、取消
- 新增统计报表 `CSV` 导出
- 完善数据库关键字段索引
- 保持 JWT、权限控制、统一异常处理和事务机制稳定

### 3.2 优化目标

- 将统一返回封装从 `ApiResponse<T>` 统一为 `Result<T>`，或增加兼容别名
- 为路线状态管理补充更清晰的上下架接口或前端交互
- 补充接口文档、测试用例和部署文档

## 4. 总体实施策略

- 原则一：优先补齐需求缺口，不做大规模无收益重构
- 原则二：后端接口先行，前端页面跟进，最后联调与验收
- 原则三：尽量复用现有 `Controller + Service + Repository` 结构
- 原则四：所有新增写操作继续使用事务保护
- 原则五：新增功能必须纳入统一响应和统一异常处理体系

## 5. 分阶段实施计划

## 第一阶段：需求收口与数据库设计

### 目标

明确最终交付范围，补充数据库表结构和字段设计，避免开发中途反复返工。

### 任务

1. 确认酒店模块的最小功能范围
2. 确认订单“拒绝”语义是否仅管理员可操作
3. 确认 `CSV` 导出字段范围
4. 确认是否必须将 `ApiResponse<T>` 重命名为 `Result<T>`
5. 明确数据库索引方案

### 输出物

- 酒店表字段设计
- 订单状态流转表
- CSV 报表字段定义
- 索引清单

### 建议表结构补充

`Hotel` 建议字段：

- `id`
- `name`
- `city`
- `address`
- `phone`
- `star_level`
- `description`
- `status`
- `created_at`
- `updated_at`

订单建议补充字段：

- `rejectReason`
- 可选：`processedBy`
- 可选：`processedAt`

## 第二阶段：后端功能补齐

### 目标

在现有后端基础上补齐核心业务能力，使接口层满足目标要求。

### 任务 2.1：酒店管理模块

新增内容：

- 实体：`Hotel`
- 仓储：`HotelRepository`
- 服务：`HotelService`
- 控制器：
  - `HotelController`，后台管理接口
  - 可选：在 `PublicController` 增加酒店公共查询接口
- DTO：
  - `HotelRequest`

建议接口：

- `GET /api/admin/hotels`
- `GET /api/admin/hotels/{id}`
- `POST /api/admin/hotels`
- `PUT /api/admin/hotels/{id}`
- `DELETE /api/admin/hotels/{id}`
- 可选：`GET /api/public/hotels`

实施重点：

- 复用现有路线、景点模块的 CRUD 模式
- 使用状态字段控制启用/停用
- 对城市、名称、状态建立索引

### 任务 2.2：订单流程完善

当前订单状态包括：

- `PENDING`
- `CONFIRMED`
- `PAID`
- `CANCELLED`
- `REFUNDING`
- `REFUNDED`
- `COMPLETED`

需要新增或调整：

- 增加 `REJECTED` 状态
- 增加 `rejectReason` 字段
- 在 `OrderService` 中增加状态流转校验

建议状态流转：

- 用户提交：`PENDING`
- 管理员确认：`CONFIRMED`
- 管理员拒绝：`REJECTED`
- 用户取消：`CANCELLED`
- 已支付完成：`PAID -> COMPLETED`

建议后端改造点：

- `Order` 实体新增字段与状态枚举
- `OrderService` 增加状态机校验逻辑
- `OrderController` 增加拒绝接口或扩展现有状态修改接口
- 异常时通过 `BusinessException` 返回业务错误

### 任务 2.3：统计报表 CSV 导出

当前仅有：

- `GET /api/admin/stats`

需新增：

- `GET /api/admin/stats/export`

实现方式建议：

- 新增 `StatsService`
- 由服务层汇总订单、路线、用户、营收等数据
- 生成标准 `CSV` 内容
- 通过响应头触发浏览器下载

建议导出字段：

- 统计日期
- 总用户数
- 总路线数
- 总景点数
- 总订单数
- 今日订单数
- 待处理订单数
- 总营收

### 任务 2.4：统一返回封装兼容处理

当前项目使用 `ApiResponse<T>`，文档要求为 `Result<T>`。

建议两种方案二选一：

1. 保持现有 `ApiResponse<T>` 不动，仅在文档中说明语义等价
2. 新增 `Result<T>` 类并逐步替换，保留兼容期

建议优先方案：

- 课程项目/短周期交付优先保持 `ApiResponse<T>` 不动，避免无收益改动扩大影响面

### 任务 2.5：索引优化

在实体 `@Table` 中补充 `indexes`，重点关注：

- 用户表：`username`
- 订单表：`status`、`created_at`、`user_id`、`route_id`
- 线路表：`status`、`destination`
- 景点表：`status`、`location`
- 酒店表：`city`、`status`、`name`

## 第三阶段：前端功能补齐

### 目标

让前台和后台具备完整的业务闭环，并对接新增后端接口。

### 任务 3.1：管理员酒店管理页面

新增页面建议：

- `web/src/views/HotelsView.vue`

需要实现：

- 酒店列表
- 条件筛选
- 新增酒店
- 编辑酒店
- 删除酒店
- 状态切换

同步新增：

- `web/src/api/hotels.js`
- 后台路由 `/admin/hotels`
- 后台菜单入口

### 任务 3.2：订单后台流程完善

需要实现：

- 订单确认按钮
- 订单拒绝按钮
- 拒绝原因输入框
- 订单状态显示优化
- 取消状态和拒绝状态区分展示

涉及页面：

- `web/src/views/OrdersView.vue`
- 可选：`web/src/views/portal/MyOrders.vue`

### 任务 3.3：统计报表导出

需要实现：

- 仪表盘增加“导出 CSV”按钮
- 前端调用导出接口并下载文件
- 对异常下载场景进行提示

涉及页面：

- `web/src/views/Dashboard.vue`
- 可选：新增工具函数处理文件下载

### 任务 3.4：路线上下架交互优化

可选增强：

- 在线路管理页面中增加“发布/下架”快捷操作
- 避免必须进入编辑弹窗才能改状态

## 第四阶段：联调、测试与验收

### 目标

验证新增功能可用、权限正确、流程完整、部署可复现。

### 测试重点

1. 注册、登录、JWT 鉴权是否正常
2. 普通用户是否无法访问 `/api/admin/**`
3. 管理员是否可完成酒店 CRUD
4. 用户下单后管理员是否可确认或拒绝
5. 拒绝后前台是否能正确显示状态和原因
6. 统计报表是否能成功导出 CSV
7. 索引补充后基础查询是否正常
8. Vite 代理与 Nginx 反向代理是否正常

### 验收标准

- 所有主要功能可通过前端页面完成操作
- 后端接口统一返回格式一致
- 异常请求可获得明确错误提示
- 订单关键写操作具备事务一致性
- 管理员后台能看到统计并导出报表
- 数据库表结构与需求说明一致

## 6. 建议实施顺序

建议按以下顺序推进：

1. 数据模型设计确认
2. 酒店模块后端实现
3. 订单拒绝流程后端实现
4. CSV 导出后端实现
5. 数据库索引补充
6. 前端酒店管理页面
7. 前端订单拒绝与状态展示
8. 前端统计导出
9. 联调测试
10. 部署验收

## 7. 影响文件清单

### 后端预计新增文件

- `src/main/java/com/ts/entity/Hotel.java`
- `src/main/java/com/ts/dto/HotelRequest.java`
- `src/main/java/com/ts/repository/HotelRepository.java`
- `src/main/java/com/ts/service/HotelService.java`
- `src/main/java/com/ts/controller/HotelController.java`
- 可选：`src/main/java/com/ts/service/StatsService.java`

### 后端预计修改文件

- `src/main/java/com/ts/entity/Order.java`
- `src/main/java/com/ts/service/OrderService.java`
- `src/main/java/com/ts/controller/OrderController.java`
- `src/main/java/com/ts/controller/StatsController.java`
- `src/main/java/com/ts/controller/PublicController.java`
- `src/main/java/com/ts/config/SecurityConfig.java`

### 前端预计新增文件

- `web/src/api/hotels.js`
- `web/src/views/HotelsView.vue`

### 前端预计修改文件

- `web/src/router/index.js`
- `web/src/views/Layout.vue`
- `web/src/views/OrdersView.vue`
- `web/src/views/Dashboard.vue`
- `web/src/views/portal/MyOrders.vue`

## 8. 风险与应对

### 风险 1：需求扩散

表现：

- 酒店模块可能继续扩展到房型、库存、价格日历，导致超出当前项目范围

应对：

- 本轮只实现“酒店资源集中管理”的最小闭环，不扩展复杂库存系统

### 风险 2：订单状态混乱

表现：

- 直接允许任意状态修改会导致业务流转不一致

应对：

- 在 `OrderService` 中实现明确的状态迁移规则

### 风险 3：CSV 导出与前端下载兼容性

表现：

- 前端若按普通 JSON 请求处理，将无法正确下载文件

应对：

- 前端导出请求单独配置 `blob` 响应类型

### 风险 4：数据库索引变更影响旧数据

表现：

- 本地已有表结构与新索引不完全一致

应对：

- 先在开发环境验证 `ddl-auto: update` 结果，必要时补充手工 SQL

## 9. 时间安排建议

若按 5 个工作日推进，可采用以下节奏：

### 第 1 天

- 确认需求边界
- 完成酒店与订单扩展的数据结构设计

### 第 2 天

- 完成酒店模块后端
- 完成订单拒绝流程后端

### 第 3 天

- 完成统计导出与索引优化
- 完成接口自测

### 第 4 天

- 完成前端酒店管理
- 完成订单状态交互优化

### 第 5 天

- 完成导出联调
- 完成测试、文档、部署验证

## 10. 最终交付物

本轮实施完成后，建议交付以下成果：

1. 可运行的前后端项目代码
2. 更新后的项目说明文档
3. 本实施计划文档
4. 数据库结构说明
5. 测试记录或功能验收截图
6. 部署说明或 Nginx 配置示例

## 11. 结论

当前项目已经具备旅游管理系统的主体框架，实施重点不在于推倒重来，而在于围绕“酒店管理、订单拒绝、CSV 导出、索引优化”四个缺口做增量完善。按本计划执行后，项目可以更接近完整课程设计或实际演示交付版本。
