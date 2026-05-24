<script setup>
import { computed, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessageBox } from 'element-plus'
import { Menu, X } from 'lucide-vue-next'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const route = useRoute()
const store = useUserStore()
const menuOpen = ref(false)
const isHome = computed(() => route.path === '/')

const navigationLinks = computed(() => [
  { label: '首页', to: '/' },
  { label: '旅游线路', to: '/routes' },
  { label: '景点信息', to: '/attractions' },
  ...(store.isLoggedIn ? [{ label: '我的订单', to: '/my-orders' }] : []),
])

watch(
  () => route.fullPath,
  () => {
    menuOpen.value = false
  }
)

function toggleMenu() {
  menuOpen.value = !menuOpen.value
}

function logout() {
  store.clear()
  router.push('/')
}

async function confirmLogout() {
  try {
    await ElMessageBox.confirm('确定退出登录？', '提示', { type: 'warning' })
    logout()
  } catch {
    // no-op
  }
}
</script>

<template>
  <div class="portal-shell">
    <header class="portal-header">
      <div class="portal-header__inner">
        <RouterLink to="/" class="portal-brand">
          <img src="/logo.png" alt="Travel" class="portal-brand__logo">
          <div>
            <p class="portal-brand__title">TravelFlow</p>
            <p class="portal-brand__subtitle">旅游管理系统</p>
          </div>
        </RouterLink>

        <nav class="portal-nav">
          <RouterLink
            v-for="item in navigationLinks"
            :key="item.to"
            :to="item.to"
            class="portal-nav__link"
            active-class="portal-nav__link--active"
          >
            {{ item.label }}
          </RouterLink>
        </nav>

        <div class="portal-actions">
          <template v-if="store.isLoggedIn">
            <div class="portal-user">
              {{ store.realName || store.username }}
            </div>
            <button type="button" class="portal-button portal-button--secondary" @click="confirmLogout">
              退出登录
            </button>
          </template>

          <template v-else>
            <RouterLink to="/login" class="portal-button portal-button--ghost">登录</RouterLink>
            <RouterLink to="/register" class="portal-button portal-button--primary">注册</RouterLink>
          </template>

          <RouterLink
            v-if="store.isAdmin"
            to="/admin/dashboard"
            class="portal-button portal-button--secondary"
          >
            管理后台
          </RouterLink>
        </div>

        <button
          type="button"
          class="portal-menu-button"
          :aria-expanded="menuOpen"
          aria-label="切换菜单"
          @click="toggleMenu"
        >
          <Menu v-if="!menuOpen" class="h-5 w-5" />
          <X v-else class="h-5 w-5" />
        </button>
      </div>

      <transition name="fade">
        <div v-if="menuOpen" class="portal-mobile-menu">
          <RouterLink
            v-for="item in navigationLinks"
            :key="item.to"
            :to="item.to"
            class="portal-mobile-menu__link"
          >
            {{ item.label }}
          </RouterLink>

          <template v-if="store.isLoggedIn">
            <button type="button" class="portal-mobile-menu__link" @click="confirmLogout">
              退出登录
            </button>
          </template>

          <template v-else>
            <RouterLink to="/login" class="portal-mobile-menu__link">登录</RouterLink>
            <RouterLink to="/register" class="portal-mobile-menu__link portal-mobile-menu__link--primary">注册</RouterLink>
          </template>

          <RouterLink
            v-if="store.isAdmin"
            to="/admin/dashboard"
            class="portal-mobile-menu__link"
          >
            管理后台
          </RouterLink>
        </div>
      </transition>
    </header>

    <main class="portal-main">
      <div class="portal-main__inner" :class="{ 'portal-main__inner--framed': !isHome }">
        <RouterView v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" :key="route.fullPath" />
          </transition>
        </RouterView>
      </div>
    </main>
  </div>
</template>

<style scoped>
.portal-shell {
  min-height: 100vh;
  background:
    linear-gradient(180deg, rgba(var(--brand-soft-rgb), 0.18) 0%, transparent 18%),
    var(--bg-soft);
}

.portal-header {
  position: sticky;
  top: 0;
  z-index: 30;
  background: rgba(255, 255, 255, 0.96);
  border-bottom: 1px solid var(--border-strong);
  box-shadow: 0 10px 28px rgba(var(--brand-dark-rgb), 0.06);
}

html.dark .portal-header {
  background: rgba(23, 34, 40, 0.96);
}

.portal-header__inner,
.portal-main__inner {
  width: min(1360px, calc(100% - 32px));
  margin: 0 auto;
}

.portal-header__inner {
  min-height: 76px;
  display: flex;
  align-items: center;
  gap: 16px;
}

.portal-brand {
  display: flex;
  align-items: center;
  gap: 12px;
  color: hsl(var(--text-100));
  text-decoration: none;
}

.portal-brand__logo {
  width: 40px;
  height: 40px;
  border-radius: 12px;
  object-fit: cover;
}

.portal-brand__title {
  font-size: 20px;
  font-weight: 800;
  letter-spacing: -0.02em;
  background: linear-gradient(135deg, var(--brand) 0%, var(--brand-dark) 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.portal-brand__subtitle {
  font-size: 12px;
  color: hsl(var(--text-300));
}

.portal-nav {
  display: none;
  align-items: center;
  gap: 6px;
  margin-left: 20px;
}

.portal-nav__link {
  padding: 10px 16px;
  border-radius: 999px;
  color: hsl(var(--text-200));
  font-size: 14px;
  font-weight: 500;
  text-decoration: none;
  transition: background 0.2s ease, color 0.2s ease;
}

.portal-nav__link:hover,
.portal-nav__link--active {
  background: rgba(var(--brand-primary-ring), 0.1);
  color: var(--brand);
}

.portal-actions {
  display: none;
  align-items: center;
  gap: 12px;
  margin-left: auto;
}

.portal-user {
  padding: 10px 14px;
  border-radius: 999px;
  background: rgba(var(--brand-primary-ring), 0.08);
  color: hsl(var(--text-100));
  font-size: 13px;
  font-weight: 600;
}

.portal-button {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-height: 40px;
  padding: 0 18px;
  border-radius: 999px;
  border: 1px solid transparent;
  text-decoration: none;
  font-size: 14px;
  font-weight: 600;
  transition: all 0.2s ease;
}

.portal-button--primary {
  background: var(--brand);
  color: #fff;
}

.portal-button--primary:hover {
  background: var(--brand-dark);
}

.portal-button--secondary {
  border-color: var(--border);
  background: hsl(var(--panel));
  color: hsl(var(--text-100));
}

.portal-button--secondary:hover,
.portal-button--ghost:hover {
  border-color: rgba(var(--brand-primary-ring), 0.28);
  color: var(--brand);
}

.portal-button--ghost {
  color: hsl(var(--text-200));
}

.portal-menu-button {
  width: 40px;
  height: 40px;
  margin-left: auto;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  border-radius: 10px;
  border: 1px solid var(--border);
  background: hsl(var(--panel));
  color: hsl(var(--text-100));
}

.portal-mobile-menu {
  width: min(1360px, calc(100% - 32px));
  margin: 0 auto 14px;
  display: grid;
  gap: 10px;
  padding-bottom: 4px;
}

.portal-mobile-menu__link {
  min-height: 44px;
  padding: 12px 14px;
  display: flex;
  align-items: center;
  border-radius: 12px;
  border: 1px solid var(--border);
  background: hsl(var(--panel));
  color: hsl(var(--text-100));
  text-decoration: none;
  font-size: 14px;
}

.portal-mobile-menu__link--primary {
  background: var(--brand);
  color: #fff;
  border-color: var(--brand);
}

.portal-main {
  padding: 24px 0 40px;
}

.portal-main__inner--framed {
  padding: 20px;
  border-radius: 24px;
  background: hsl(var(--panel));
  border: 1px solid var(--border);
  box-shadow: var(--shadow-panel);
}

@media (min-width: 1024px) {
  .portal-nav,
  .portal-actions {
    display: flex;
  }

  .portal-menu-button,
  .portal-mobile-menu {
    display: none;
  }
}
</style>
