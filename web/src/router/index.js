import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    // 前台路由
    {
      path: '/',
      component: () => import('@/views/portal/PortalLayout.vue'),
      children: [
        { path: '', name: 'home', component: () => import('@/views/portal/HomePortal.vue') },
        { path: 'routes', name: 'route-list', component: () => import('@/views/portal/RouteList.vue') },
        { path: 'routes/:id', name: 'route-detail', component: () => import('@/views/portal/RouteDetail.vue') },
        { path: 'attractions', name: 'attraction-list', component: () => import('@/views/portal/AttractionList.vue') },
        { path: 'attractions/:id', name: 'attraction-detail', component: () => import('@/views/portal/AttractionDetail.vue') },
        { path: 'my-orders', name: 'my-orders', component: () => import('@/views/portal/MyOrders.vue'), meta: { requiresAuth: true } },
        { path: 'login', name: 'portal-login', component: () => import('@/views/portal/PortalLogin.vue') },
        { path: 'register', name: 'register', component: () => import('@/views/portal/PortalRegister.vue') },
      ]
    },
    // 管理后台路由
    { path: '/admin/login', name: 'login', component: () => import('@/views/LoginView.vue'), meta: { public: true } },
    {
      path: '/admin',
      component: () => import('@/views/Layout.vue'),
      meta: { requiresAdmin: true },
      children: [
        { path: '', redirect: '/admin/dashboard' },
        { path: 'dashboard', name: 'dashboard', component: () => import('@/views/Dashboard.vue') },
        { path: 'routes', name: 'admin-routes', component: () => import('@/views/Destinations.vue') },
        { path: 'hotels', name: 'admin-hotels', component: () => import('@/views/HotelsView.vue') },
        { path: 'attractions', name: 'admin-attractions', component: () => import('@/views/AttractionsView.vue') },
        { path: 'orders', name: 'admin-orders', component: () => import('@/views/OrdersView.vue') },
        { path: 'users', name: 'admin-users', component: () => import('@/views/UsersView.vue') },
        { path: 'notices', name: 'admin-notices', component: () => import('@/views/NoticesView.vue') },
        { path: 'settings', name: 'settings', component: () => import('@/views/SettingsView.vue') },
      ],
    },
    { path: '/:pathMatch(.*)*', redirect: '/' },
  ],
})

router.beforeEach((to) => {
  const store = useUserStore()
  if (to.meta.requiresAuth && !store.isLoggedIn) {
    return { name: 'portal-login', query: { redirect: to.fullPath } }
  }
  if (to.meta.requiresAdmin) {
    if (!store.isLoggedIn) return { name: 'login', query: { redirect: to.fullPath } }
    if (!store.isAdmin) return { path: '/' }
  }
})

export default router
