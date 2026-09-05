# 前端说明

本目录是旅游管理系统前端工程，基于 `Vue 3 + Vite + Pinia + Element Plus + Axios`，同时承载前台门户和管理员后台。

## 运行

```bash
npm install
npm run dev
```

默认通过 Vite 开发服务器启动，`/api` 请求会代理到：

```text
http://localhost:8080
```

## 构建

```bash
npm run build
npm run preview
```

## 目录说明

- `src/api`：接口封装
- `src/router`：路由与权限守卫
- `src/stores`：Pinia 用户状态
- `src/views/portal`：前台门户页面
- `src/views`：管理员后台页面

## 主要页面

前台：

- `/`
- `/routes`
- `/attractions`
- `/my-orders`
- `/login`
- `/register`

后台：

- `/admin/dashboard`
- `/admin/routes`
- `/admin/hotels`
- `/admin/attractions`
- `/admin/orders`
- `/admin/users`
- `/admin/notices`
- `/admin/settings`

更多项目背景、后端架构和部署方式见仓库根目录 `README.md`。
