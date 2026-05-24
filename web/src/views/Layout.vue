<script setup>
import { computed, onMounted, onUnmounted, ref, watch } from 'vue'
import { RouterLink, RouterView, useRoute, useRouter } from 'vue-router'
import { Moon, Sun, Languages } from 'lucide-vue-next'
import { useUserStore } from '@/stores/user'
import { useI18n } from 'vue-i18n'
import { getAttractions } from '@/api/attractions'
import { getHotels } from '@/api/hotels'
import { getNotices } from '@/api/notices'
import { getOrders } from '@/api/orders'
import { getRoutes } from '@/api/routes'
import { getUsers } from '@/api/users'
import { initializeAppPreferences, useAppPreferences } from '@/composables/useAppPreferences'
import {
  Bars3Icon,
  BellIcon,
  BuildingOffice2Icon,
  Cog6ToothIcon,
  GlobeAltIcon,
  HomeIcon,
  MagnifyingGlassIcon,
  ShoppingBagIcon,
  UserGroupIcon,
  XMarkIcon,
} from '@heroicons/vue/24/outline'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const mobileMenuOpen = ref(false)
const notificationsOpen = ref(false)
const notificationsLoading = ref(false)
const globalSearchQuery = ref('')
const globalSearchOpen = ref(false)
const globalSearchLoading = ref(false)
const globalSearchSections = ref([])
const pendingOrders = ref([])
const latestNotices = ref([])
const { locale } = useI18n({ useScope: 'global' })
const { preferences, toggleTheme, toggleLocale } = useAppPreferences()

const navigation = [
  { labelKey: 'nav.dashboard', to: '/admin/dashboard', icon: HomeIcon },
  { labelKey: 'nav.destinations', to: '/admin/routes', icon: GlobeAltIcon },
  { labelKey: 'nav.hotels', to: '/admin/hotels', icon: BuildingOffice2Icon },
  { labelKey: 'nav.bookings', to: '/admin/orders', icon: ShoppingBagIcon },
  { labelKey: 'nav.customers', to: '/admin/users', icon: UserGroupIcon },
  { labelKey: 'nav.settings', to: '/admin/settings', icon: Cog6ToothIcon },
]

const currentSectionKey = computed(() => {
  return navigation.find((item) => route.path.startsWith(item.to))?.labelKey || 'nav.dashboard'
})

const userInitials = computed(() => {
  const source = userStore.realName || userStore.username || 'TM'
  return source
    .trim()
    .split(/\s+/)
    .map((part) => part[0])
    .join('')
    .slice(0, 2)
    .toUpperCase()
})

const isDarkMode = computed(() => preferences.theme === 'dark')

const notificationCount = computed(() => {
  if (!preferences.notificationsEnabled) return 0
  return pendingOrders.value.length + latestNotices.value.length
})

const SEARCH_MIN_LENGTH = 2
const SEARCH_RESULT_LIMIT = 3
const SEARCH_CACHE_TTL = 60 * 1000

let searchTimer = null
let activeSearchRequest = 0
const globalSearchCache = new Map()

function getCachedGlobalSearch(keyword) {
  const cacheKey = keyword.toLowerCase()
  const cached = globalSearchCache.get(cacheKey)
  if (!cached) return null

  if (Date.now() - cached.ts > SEARCH_CACHE_TTL) {
    globalSearchCache.delete(cacheKey)
    return null
  }

  return cached.sections
}

function setCachedGlobalSearch(keyword, sections) {
  globalSearchCache.set(keyword.toLowerCase(), {
    ts: Date.now(),
    sections,
  })
}

function isActive(path) {
  return route.path.startsWith(path)
}

function closeMobileMenu() {
  mobileMenuOpen.value = false
}

async function performGlobalSearch(keyword) {
  const normalized = keyword.trim()
  if (normalized.length < SEARCH_MIN_LENGTH) {
    globalSearchSections.value = []
    globalSearchLoading.value = false
    return
  }

  const cachedSections = getCachedGlobalSearch(normalized)
  if (cachedSections) {
    globalSearchSections.value = cachedSections
    globalSearchLoading.value = false
    return
  }

  const requestId = ++activeSearchRequest
  globalSearchLoading.value = true

  try {
    const [routesRes, hotelsRes, ordersRes, usersRes, attractionsRes, noticesRes] = await Promise.all([
      getRoutes({ keyword: normalized, page: 1, size: SEARCH_RESULT_LIMIT }),
      getHotels({ keyword: normalized, page: 1, size: SEARCH_RESULT_LIMIT }),
      getOrders({ keyword: normalized, page: 1, size: SEARCH_RESULT_LIMIT }),
      getUsers({ keyword: normalized, page: 1, size: SEARCH_RESULT_LIMIT }),
      getAttractions({ keyword: normalized, page: 1, size: SEARCH_RESULT_LIMIT }),
      getNotices({ keyword: normalized, page: 1, size: SEARCH_RESULT_LIMIT }),
    ])

    const sections = [
      {
        key: 'routes',
        label: 'header.searchTypeRoutes',
        items: (routesRes.data.items || []).map((item) => ({
          id: item.id,
          title: item.title,
          subtitle: `${item.departure || '-'} -> ${item.destination || '-'}`,
          path: { path: '/admin/routes', query: { keyword: normalized } },
        })),
      },
      {
        key: 'hotels',
        label: 'header.searchTypeHotels',
        items: (hotelsRes.data.items || []).map((item) => ({
          id: item.id,
          title: item.name,
          subtitle: `${item.city || '-'} · ${item.address || '-'}`,
          path: { path: '/admin/hotels', query: { keyword: normalized } },
        })),
      },
      {
        key: 'orders',
        label: 'header.searchTypeOrders',
        items: (ordersRes.data.items || []).map((item) => ({
          id: item.id,
          title: item.orderNo,
          subtitle: `${item.contactName || item.user?.username || '-'} / ${item.route?.title || '-'}`,
          path: { path: '/admin/orders', query: { keyword: normalized } },
        })),
      },
      {
        key: 'users',
        label: 'header.searchTypeUsers',
        items: (usersRes.data.items || []).map((item) => ({
          id: item.id,
          title: item.realName || item.username,
          subtitle: `${item.username} / ${item.email || '-'}`,
          path: { path: '/admin/users', query: { keyword: normalized } },
        })),
      },
      {
        key: 'attractions',
        label: 'header.searchTypeAttractions',
        items: (attractionsRes.data.items || []).map((item) => ({
          id: item.id,
          title: item.name,
          subtitle: `${item.location || '-'} / ${item.openTime || '-'}`,
          path: { path: '/admin/attractions', query: { keyword: normalized } },
        })),
      },
      {
        key: 'notices',
        label: 'header.searchTypeNotices',
        items: (noticesRes.data.items || []).map((item) => ({
          id: item.id,
          title: item.title,
          subtitle: item.content || '-',
          path: { path: '/admin/notices', query: { keyword: normalized } },
        })),
      },
    ].filter((section) => section.items.length)

    if (requestId !== activeSearchRequest) return
    globalSearchSections.value = sections
    setCachedGlobalSearch(normalized, sections)
  } catch {
    if (requestId !== activeSearchRequest) return
    globalSearchSections.value = []
  } finally {
    if (requestId === activeSearchRequest) {
      globalSearchLoading.value = false
    }
  }
}

function openGlobalSearch() {
  globalSearchOpen.value = true
  if (globalSearchQuery.value.trim().length >= SEARCH_MIN_LENGTH) {
    performGlobalSearch(globalSearchQuery.value)
  }
}

function closeGlobalSearch() {
  activeSearchRequest += 1
  clearTimeout(searchTimer)
  globalSearchLoading.value = false
  globalSearchOpen.value = false
}

function openSearchResult(target) {
  globalSearchOpen.value = false
  router.push(target)
}

async function loadNotifications() {
  if (!preferences.notificationsEnabled) return

  notificationsLoading.value = true
  try {
    const [ordersRes, noticesRes] = await Promise.all([
      getOrders({ page: 1, size: 3, status: 'PENDING' }),
      getNotices({ page: 1, size: 3 }),
    ])
    pendingOrders.value = ordersRes.data.items || []
    latestNotices.value = noticesRes.data.items || []
  } finally {
    notificationsLoading.value = false
  }
}

async function toggleNotifications() {
  notificationsOpen.value = !notificationsOpen.value
  if (notificationsOpen.value) {
    await loadNotifications()
  }
}

function openNotificationsRoute(path) {
  notificationsOpen.value = false
  router.push(path)
}

function toggleLanguage() {
  toggleLocale(locale)
}

function goToMainPage() {
  closeMobileMenu()
  router.push('/')
}

function logout() {
  userStore.clear()
  closeMobileMenu()
  router.push('/admin/login')
}

onMounted(() => {
  initializeAppPreferences(locale)
  loadNotifications()
})

watch(() => route.fullPath, () => {
  notificationsOpen.value = false
  globalSearchOpen.value = false
})

watch(() => preferences.notificationsEnabled, (enabled) => {
  if (!enabled) {
    notificationsOpen.value = false
    pendingOrders.value = []
    latestNotices.value = []
    return
  }

  loadNotifications()
})

watch(globalSearchQuery, (value) => {
  clearTimeout(searchTimer)

  if (!globalSearchOpen.value) return

  searchTimer = setTimeout(() => {
    performGlobalSearch(value)
  }, 300)
})

onUnmounted(() => {
  activeSearchRequest += 1
  clearTimeout(searchTimer)
})
</script>

<template>
  <div>
    <div class="scene-bg" aria-hidden="true"></div>

    <div class="app-shell min-h-screen px-3 py-3 md:px-6 md:py-5">
      <div class="flex min-h-[calc(100vh-2rem)] gap-3">
        <aside class="liquid-glass-strong hidden w-72 shrink-0 rounded-[2rem] p-6 lg:flex lg:flex-col">
          <div class="flex items-center gap-3">
            <img src="/logo.png" alt="logo" class="h-10 w-10 rounded-xl object-cover">
            <div>
              <p class="text-xl font-extrabold tracking-tight" style="background: linear-gradient(135deg, var(--brand) 0%, var(--brand-dark) 100%); -webkit-background-clip: text; -webkit-text-fill-color: transparent;">{{ $t('app.name') }}</p>
              <p class="theme-text-secondary text-xs">{{ $t('app.subtitle') }}</p>
            </div>
          </div>

          <nav class="mt-8 space-y-2">
            <RouterLink
              v-for="item in navigation"
              :key="item.to"
              :to="item.to"
              class="theme-text-secondary flex items-center gap-3 rounded-2xl px-4 py-3 text-sm transition-all hover:bg-brand/5 hover:text-brand"
              :class="isActive(item.to) ? 'bg-brand/10 text-brand font-medium' : ''"
            >
              <component :is="item.icon" class="h-5 w-5" />
              <span>{{ $t(item.labelKey) }}</span>
            </RouterLink>
          </nav>

          <div class="theme-surface-soft mt-auto rounded-3xl p-4">
            <p class="theme-text-primary text-sm font-semibold">{{ $t('sidebar.workspaceTitle') }}</p>
            <p class="theme-text-secondary mt-2 text-xs leading-5">{{ $t('sidebar.workspaceDescription') }}</p>
            <button
              type="button"
              class="theme-control mt-4 w-full rounded-full py-2 text-xs font-medium"
              @click="logout"
            >
              {{ $t('common.signOut') }}
            </button>
          </div>
        </aside>

        <div class="min-w-0 flex-1">
          <header class="liquid-glass-strong !overflow-visible z-20 rounded-[2rem] px-4 py-4 md:px-6">
            <div class="flex flex-col gap-4 lg:flex-row lg:items-center lg:justify-between">
              <div class="flex items-center gap-3">
                <button
                  type="button"
                  class="theme-control inline-flex h-11 w-11 items-center justify-center rounded-2xl transition-transform hover:scale-105 lg:hidden"
                  @click="mobileMenuOpen = true"
                >
                  <Bars3Icon class="h-6 w-6" />
                </button>
                <div>
                  <p class="text-[10px] font-bold uppercase tracking-[0.2em] text-brand/60">{{ $t('header.commandCenter') }}</p>
                  <h1 class="theme-text-primary mt-0.5 text-2xl font-bold">{{ $t(currentSectionKey) }}</h1>
                </div>
              </div>

              <div class="flex flex-1 flex-col gap-3 lg:max-w-3xl lg:flex-row lg:items-center lg:justify-end">
                <div class="relative flex-1">
                  <label class="theme-search-control flex items-center gap-3 rounded-2xl px-4 py-3">
                    <MagnifyingGlassIcon class="h-5 w-5" />
                    <input
                      v-model="globalSearchQuery"
                      type="text"
                      :placeholder="$t('header.searchPlaceholder')"
                      class="theme-search-input w-full bg-transparent text-sm outline-none"
                      @focus="openGlobalSearch"
                      @keydown.esc="closeGlobalSearch"
                    >
                  </label>

                  <div
                    v-if="globalSearchOpen"
                    class="liquid-glass-strong absolute left-0 right-0 z-40 mt-3 rounded-[1.6rem] p-4"
                  >
                    <div v-if="globalSearchLoading" class="theme-text-secondary py-6 text-center text-sm">
                      {{ $t('header.searchLoading') }}...
                    </div>

                    <div v-else-if="globalSearchQuery.trim().length < 2" class="py-4">
                      <p class="theme-text-primary text-sm">{{ $t('header.searchPlaceholder') }}</p>
                      <p class="theme-text-secondary mt-2 text-xs">{{ $t('header.searchHint') }}</p>
                    </div>

                    <div v-else-if="globalSearchSections.length" class="space-y-4">
                      <div v-for="section in globalSearchSections" :key="section.key">
                        <p class="theme-text-soft mb-2 text-xs uppercase tracking-[0.28em]">{{ $t(section.label) }}</p>
                        <div class="space-y-2">
                          <button
                            v-for="item in section.items"
                            :key="`${section.key}-${item.id}`"
                            type="button"
                            class="theme-text-primary liquid-glass flex w-full items-start justify-between gap-4 rounded-2xl px-4 py-3 text-left transition-transform hover:scale-105"
                            @mousedown.prevent="openSearchResult(item.path)"
                          >
                            <div class="min-w-0">
                              <p class="theme-text-primary truncate text-sm">{{ item.title }}</p>
                              <p class="theme-text-secondary mt-1 truncate text-xs">{{ item.subtitle }}</p>
                            </div>
                            <span class="theme-text-secondary text-xs">{{ $t('header.viewAll') }}</span>
                          </button>
                        </div>
                      </div>
                    </div>

                    <div v-else class="py-6 text-center">
                      <p class="theme-text-primary text-sm">{{ $t('header.searchNoResults') }}</p>
                      <p class="theme-text-secondary mt-2 text-xs">{{ $t('header.searchNoResultsSubtitle') }}</p>
                    </div>
                  </div>
                </div>

                <div class="flex items-center justify-between gap-2 lg:justify-end">
                  <div class="relative">
                    <button
                      type="button"
                      class="theme-control relative inline-flex h-11 w-11 items-center justify-center rounded-2xl transition-transform hover:scale-105"
                      @click="toggleNotifications"
                    >
                      <BellIcon class="h-5 w-5" />
                      <span
                        v-if="notificationCount"
                        class="theme-count-badge absolute right-1 top-1 min-w-[18px] rounded-full px-1.5 text-center text-[11px] leading-5"
                      >
                        {{ notificationCount }}
                      </span>
                    </button>

                    <div
                      v-if="notificationsOpen"
                      class="liquid-glass-strong absolute right-0 z-40 mt-3 w-[360px] max-w-[calc(100vw-2rem)] rounded-[1.6rem] p-4"
                    >
                      <div class="flex items-center justify-between">
                        <p class="theme-text-primary text-sm">{{ $t('header.notifications') }}</p>
                        <button
                          type="button"
                          class="theme-control rounded-full px-3 py-1.5 text-xs transition-transform hover:scale-105"
                          @click="openNotificationsRoute('/admin/settings')"
                        >
                          {{ $t('header.openSettings') }}
                        </button>
                      </div>

                      <div v-if="!preferences.notificationsEnabled" class="py-8 text-center">
                        <p class="theme-text-primary text-sm">{{ $t('header.notificationsDisabled') }}</p>
                        <p class="theme-text-secondary mt-2 text-xs">{{ $t('header.notificationsDisabledSubtitle') }}</p>
                      </div>

                      <div v-else-if="notificationsLoading" class="theme-text-secondary py-8 text-center text-sm">
                        {{ $t('header.notifications') }}...
                      </div>

                      <div v-else class="mt-4 space-y-4">
                        <div>
                          <div class="mb-2 flex items-center justify-between">
                            <p class="theme-text-soft text-xs uppercase tracking-[0.28em]">{{ $t('header.pendingOrders') }}</p>
                            <button
                              type="button"
                              class="theme-text-secondary text-xs"
                              @click="openNotificationsRoute('/admin/orders')"
                            >
                              {{ $t('header.viewAll') }}
                            </button>
                          </div>

                          <div v-if="pendingOrders.length" class="space-y-2">
                            <button
                              v-for="order in pendingOrders"
                              :key="order.id"
                              type="button"
                              class="theme-text-primary liquid-glass flex w-full items-start justify-between rounded-2xl px-4 py-3 text-left transition-transform hover:scale-105"
                              @click="openNotificationsRoute('/admin/orders')"
                            >
                              <div>
                                <p class="theme-text-primary text-sm">{{ order.contactName || order.user?.username || order.orderNo }}</p>
                                <p class="theme-text-secondary mt-1 text-xs">{{ order.route?.title || order.orderNo }}</p>
                              </div>
                              <span class="theme-status-pill rounded-full px-2.5 py-1 text-xs font-medium">PENDING</span>
                            </button>
                          </div>
                        </div>

                        <div>
                          <div class="mb-2 flex items-center justify-between">
                            <p class="theme-text-soft text-xs uppercase tracking-[0.28em]">{{ $t('header.latestNotices') }}</p>
                            <button
                              type="button"
                              class="theme-text-secondary text-xs"
                              @click="openNotificationsRoute('/admin/notices')"
                            >
                              {{ $t('header.viewAll') }}
                            </button>
                          </div>

                          <div v-if="latestNotices.length" class="space-y-2">
                            <button
                              v-for="notice in latestNotices"
                              :key="notice.id"
                              type="button"
                              class="theme-text-primary liquid-glass flex w-full items-start gap-3 rounded-2xl px-4 py-3 text-left transition-transform hover:scale-105"
                              @click="openNotificationsRoute('/admin/notices')"
                            >
                              <div class="mt-1 h-2.5 w-2.5 rounded-full bg-brand/35" />
                              <div class="min-w-0">
                                <p class="theme-text-primary truncate text-sm">{{ notice.title }}</p>
                                <p class="theme-text-secondary mt-1 line-clamp-2 text-xs">{{ notice.content }}</p>
                              </div>
                            </button>
                          </div>
                        </div>

                        <div v-if="!pendingOrders.length && !latestNotices.length" class="py-6 text-center">
                          <p class="theme-text-primary text-sm">{{ $t('header.noNotifications') }}</p>
                          <p class="theme-text-secondary mt-2 text-xs">{{ $t('header.noNotificationsSubtitle') }}</p>
                        </div>
                      </div>
                    </div>
                  </div>

                  <button
                    type="button"
                    class="theme-control relative inline-flex h-11 w-11 items-center justify-center rounded-2xl transition-all"
                    @click="toggleTheme"
                  >
                    <Sun v-if="isDarkMode" class="h-5 w-5" />
                    <Moon v-else class="h-5 w-5" />
                  </button>

                  <button
                    type="button"
                    class="theme-text-primary liquid-glass inline-flex items-center gap-2 rounded-2xl px-4 py-3 text-sm transition-transform hover:scale-105"
                    @click="toggleLanguage"
                  >
                    <Languages class="h-4 w-4" />
                    <span>{{ $t('header.languageSwitch') }}</span>
                  </button>

                  <button
                    type="button"
                    class="theme-control inline-flex items-center gap-2 rounded-2xl px-4 py-3 text-sm font-medium transition-all"
                    @click="goToMainPage"
                  >
                    <HomeIcon class="h-4 w-4" />
                    <span>返回主页面</span>
                  </button>

                  <div class="theme-surface-soft hidden items-center gap-3 rounded-2xl px-3 py-2 sm:flex">
                    <div class="inline-flex h-10 w-10 items-center justify-center rounded-xl bg-brand/10 text-sm font-bold text-brand">
                      {{ userInitials }}
                    </div>
                    <div class="min-w-0">
                      <p class="theme-text-primary truncate text-sm font-semibold">{{ userStore.realName || userStore.username || $t('header.userFallback') }}</p>
                      <p class="theme-text-secondary text-xs">{{ $t('header.adminRole') }}</p>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </header>

          <main class="mt-3">
            <div class="liquid-glass-strong min-h-[calc(100vh-200px)] rounded-[2rem] p-3 md:p-5">
              <RouterView v-slot="{ Component }">
                <transition name="fade" mode="out-in">
                  <div :key="route.fullPath">
                    <component :is="Component" />
                  </div>
                </transition>
              </RouterView>
            </div>
          </main>
        </div>
      </div>

      <transition name="fade">
        <div v-if="mobileMenuOpen" class="fixed inset-0 z-50 lg:hidden">
          <button type="button" class="absolute inset-0 bg-black/55" @click="closeMobileMenu" />

          <aside class="liquid-glass-strong relative h-full w-80 max-w-[85vw] p-6">
            <div class="flex items-center justify-between">
              <div class="flex items-center gap-3">
                <img src="/logo.png" alt="logo" class="h-10 w-10 rounded-xl object-cover">
                <div>
                  <p class="text-base font-extrabold tracking-tight" style="background: linear-gradient(135deg, var(--brand) 0%, var(--brand-dark) 100%); -webkit-background-clip: text; -webkit-text-fill-color: transparent;">{{ $t('app.name') }}</p>
                  <p class="theme-text-secondary text-xs">{{ $t('app.subtitle') }}</p>
                </div>
              </div>

              <button
                type="button"
                class="theme-control inline-flex h-10 w-10 items-center justify-center rounded-xl transition-transform hover:scale-105"
                @click="closeMobileMenu"
              >
                <XMarkIcon class="h-6 w-6" />
              </button>
            </div>

            <nav class="mt-7 space-y-2">
              <RouterLink
                v-for="item in navigation"
                :key="item.to"
                :to="item.to"
                class="theme-text-primary liquid-glass flex items-center gap-3 rounded-2xl px-4 py-3 text-sm transition-transform hover:scale-105"
                :class="isActive(item.to) ? 'bg-brand/10 text-brand border-brand/20' : ''"
                @click="closeMobileMenu"
              >
                <component :is="item.icon" class="h-5 w-5" />
                <span>{{ $t(item.labelKey) }}</span>
              </RouterLink>
            </nav>

            <div class="theme-surface-soft mt-8 rounded-3xl p-4">
              <p class="theme-text-primary text-sm">{{ userStore.realName || userStore.username || $t('header.userFallback') }}</p>
              <p class="theme-text-secondary mt-1 text-xs">{{ $t('header.adminRole') }}</p>
              <button
                type="button"
                class="theme-control mt-4 rounded-full px-4 py-2 text-xs transition-transform hover:scale-105"
                @click="goToMainPage"
              >
                返回主页面
              </button>
              <button
                type="button"
                class="theme-control mt-3 rounded-full px-4 py-2 text-xs transition-transform hover:scale-105"
                @click="logout"
              >
                {{ $t('common.signOut') }}
              </button>
            </div>
          </aside>
        </div>
      </transition>
    </div>
  </div>
</template>

