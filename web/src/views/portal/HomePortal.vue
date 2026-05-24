<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ArrowRight, Compass, MapPinned, Mountain, Sparkles } from 'lucide-vue-next'
import forestHero from '@/assets/forest-hero.jpg'
import { useUserStore } from '@/stores/user'
import { getPublicRoutes, getPublicAttractions, getPublicNotices } from '@/api/public'

const router = useRouter()
const userStore = useUserStore()
const loading = ref(false)
const routes = ref([])
const attractions = ref([])
const notices = ref([])
const homeHeroStyle = { '--hero-image': `url(${forestHero})` }

const featuredRoute = computed(() => routes.value[0] || null)
const routeCards = computed(() => {
  const candidates = routes.value.slice(1, 4)
  return candidates.length ? candidates : routes.value.slice(0, 3)
})
const featuredAttraction = computed(() => attractions.value[0] || null)
const attractionCards = computed(() => {
  const candidates = attractions.value.slice(1, 5)
  return candidates.length ? candidates : attractions.value.slice(0, 4)
})
const latestNotices = computed(() => notices.value.slice(0, 3))

const startingPrice = computed(() => {
  const priceList = routes.value
    .map((item) => Number(item.price))
    .filter((value) => Number.isFinite(value) && value > 0)

  return priceList.length ? Math.min(...priceList) : null
})

const averageRouteDays = computed(() => {
  const dayList = routes.value
    .map((item) => Number(item.days))
    .filter((value) => Number.isFinite(value) && value > 0)

  if (!dayList.length) return null
  return Math.round(dayList.reduce((sum, value) => sum + value, 0) / dayList.length)
})

const destinationChips = computed(() => {
  const values = routes.value
    .map((item) => item.destination || item.title)
    .filter(Boolean)

  return Array.from(new Set(values)).slice(0, 5)
})

const heroStats = computed(() => [
  {
    label: '可预订线路',
    value: `${routes.value.length}+`,
    note: '覆盖热门线路与周边游',
    icon: Compass,
    color: '#38bdf8',
    bgColor: 'rgba(56, 189, 248, 0.18)',
  },
  {
    label: '景点资源',
    value: `${attractions.value.length}+`,
    note: '支持景点信息集中展示',
    icon: Mountain,
    color: '#4ade80',
    bgColor: 'rgba(74, 222, 128, 0.18)',
  },
  {
    label: '出发预算',
    value: startingPrice.value ? `￥${formatPrice(startingPrice.value)} 起` : '持续更新',
    note: averageRouteDays.value ? `平均 ${averageRouteDays.value} 天游` : '支持短线和多日出行',
    icon: MapPinned,
    color: '#fbbf24',
    bgColor: 'rgba(251, 191, 36, 0.18)',
  },
])

const serviceItems = [
  { title: '线路查询', text: '游客可按目的地、天数和价格快速筛选线路。 ' },
  { title: '在线预订', text: '支持填写出行信息并直接提交订单。' },
  { title: '订单跟踪', text: '登录后查看预订记录、处理状态和出行信息。' },
]

const accountButtonLabel = computed(() => {
  return userStore.isLoggedIn ? '查看我的订单' : '登录后开始预订'
})

const accountDescription = computed(() => {
  return userStore.isLoggedIn
    ? '继续查看已提交订单、待确认行程和出行安排。'
    : '登录后可同步订单记录、快速完成预订并查看处理状态。'
})

async function load() {
  loading.value = true
  try {
    const [routeRes, attractionRes, noticeRes] = await Promise.all([
      getPublicRoutes({ page: 1, size: 6 }),
      getPublicAttractions({ page: 1, size: 6 }),
      getPublicNotices({ page: 1, size: 3 }),
    ])

    routes.value = routeRes.data.items || []
    attractions.value = attractionRes.data.items || []
    notices.value = noticeRes.data.items || []
  } finally {
    loading.value = false
  }
}

function openRouteList() {
  router.push('/routes')
}

function openAttractionList() {
  router.push('/attractions')
}

function openAccount() {
  router.push(userStore.isLoggedIn ? '/my-orders' : '/login')
}

function openRouteDetail(id = featuredRoute.value?.id) {
  if (id) {
    router.push(`/routes/${id}`)
    return
  }
  openRouteList()
}

function openAttractionDetail(id = featuredAttraction.value?.id) {
  if (id) {
    router.push(`/attractions/${id}`)
    return
  }
  openAttractionList()
}

function formatPrice(value) {
  const amount = Number(value)
  if (!Number.isFinite(amount)) return '--'
  return amount.toLocaleString()
}

function formatTicketPrice(value) {
  const amount = Number(value)
  if (!Number.isFinite(amount) || amount <= 0) return '免费开放'
  return `￥${amount.toLocaleString()} / 票`
}

function formatNoticeDate(value) {
  if (!value) return '最新更新'

  const date = new Date(value)
  if (Number.isNaN(date.getTime())) return '最新更新'

  return new Intl.DateTimeFormat('zh-CN', {
    month: 'short',
    day: 'numeric',
  }).format(date)
}

function excerpt(text, maxLength = 68) {
  if (!text) return ''
  const normalized = String(text).replace(/\s+/g, ' ').trim()
  return normalized.length > maxLength ? `${normalized.slice(0, maxLength)}...` : normalized
}

onMounted(load)
</script>

<template>
  <section class="home-page" aria-label="旅游管理系统首页" v-loading="loading">
    <section class="home-hero" :style="homeHeroStyle">
      <div class="home-hero__content">
        <p class="home-hero__kicker">TRAVEL MANAGEMENT PLATFORM</p>
        <h1>游客查询预订与资源管理一体化旅游平台</h1>
        <p class="home-hero__description">
          面向游客提供线路查询、景点浏览和在线预订服务，
          面向旅游企业支持订单处理、公告发布和经营管理。
        </p>

        <div class="home-hero__actions">
          <button type="button" class="hero-button hero-button--primary" @click="openRouteList">
            查看旅游线路
            <ArrowRight class="h-4 w-4" />
          </button>
          <button type="button" class="hero-button hero-button--secondary" @click="openAttractionList">
            浏览景点信息
          </button>
        </div>

        <div v-if="destinationChips.length" class="home-hero__chips">
          <span v-for="chip in destinationChips" :key="chip" class="hero-chip">
            <MapPinned class="h-3.5 w-3.5" />
            {{ chip }}
          </span>
        </div>
      </div>

      <div class="home-hero__panel">
        <div class="stat-grid">
          <article v-for="item in heroStats" :key="item.label" class="stat-card">
            <div class="stat-card__icon" :style="{ color: item.color, backgroundColor: item.bgColor, borderColor: `${item.color}33` }"><component :is="item.icon" class="h-5 w-5 flex-shrink-0" style="display: block; margin: auto; stroke-width: 2.25px;" /></div>
            <p>{{ item.label }}</p>
            <strong>{{ item.value }}</strong>
            <span>{{ item.note }}</span>
          </article>
        </div>

        <button
          v-if="featuredRoute"
          type="button"
          class="featured-summary"
          @click="openRouteDetail(featuredRoute.id)"
        >
          <div class="featured-summary__head">
            <span class="featured-summary__tag">推荐线路</span>
            <span class="featured-summary__price">￥{{ formatPrice(featuredRoute.price) }} / 人</span>
          </div>
          <h2>{{ featuredRoute.title }}</h2>
          <p>{{ featuredRoute.departure || '出发地' }} → {{ featuredRoute.destination || '目的地' }}</p>
          <div class="featured-summary__meta">
            <span>{{ featuredRoute.days }} 天</span>
            <span v-if="featuredRoute.maxGroupSize">最多 {{ featuredRoute.maxGroupSize }} 人</span>
          </div>
        </button>
      </div>
    </section>

    <section class="home-layout">
      <div class="home-main">
        <section class="content-section">
          <div class="section-head">
            <div>
              <p class="section-kicker">ROUTE SERVICE</p>
              <h2 class="section-title">精选旅游线路</h2>
            </div>
            <button type="button" class="section-link" @click="openRouteList">
              查看全部
              <ArrowRight class="h-4 w-4" />
            </button>
          </div>

          <div v-if="routeCards.length" class="route-grid">
            <button
              v-for="route in routeCards"
              :key="route.id"
              type="button"
              class="route-card"
              @click="openRouteDetail(route.id)"
            >
              <div class="route-card__media">
                <img v-if="route.coverImage" :src="route.coverImage" :alt="route.title">
                <div v-else class="media-fallback media-fallback--route">{{ route.destination || '路线封面' }}</div>
                <span class="route-card__badge">{{ route.days }} 天</span>
              </div>

              <div class="route-card__body">
                <p class="route-card__path">{{ route.departure || '出发地' }} → {{ route.destination || '目的地' }}</p>
                <h3>{{ route.title }}</h3>
                <p class="route-card__description">
                  {{ excerpt(route.description, 72) || '提供标准化线路信息，便于快速比较和预订。' }}
                </p>
                <div class="route-card__footer">
                  <span>{{ route.maxGroupSize ? `最多 ${route.maxGroupSize} 人` : '支持多人出行' }}</span>
                  <strong>￥{{ formatPrice(route.price) }}</strong>
                </div>
              </div>
            </button>
          </div>

          <div v-else class="empty-card">暂无可展示线路。</div>
        </section>

        <section class="content-section">
          <div class="section-head">
            <div>
              <p class="section-kicker">ATTRACTION SERVICE</p>
              <h2 class="section-title">景点与目的地</h2>
            </div>
            <button type="button" class="section-link" @click="openAttractionList">
              查看景点
              <ArrowRight class="h-4 w-4" />
            </button>
          </div>

          <div v-if="featuredAttraction" class="attraction-layout">
            <button type="button" class="attraction-feature" @click="openAttractionDetail(featuredAttraction.id)">
              <div class="attraction-feature__media">
                <img
                  v-if="featuredAttraction.coverImage"
                  :src="featuredAttraction.coverImage"
                  :alt="featuredAttraction.name"
                >
                <div v-else class="media-fallback media-fallback--attraction">
                  {{ featuredAttraction.location || '景点信息' }}
                </div>
              </div>
              <div class="attraction-feature__body">
                <span class="feature-tag">{{ featuredAttraction.status === 'OPEN' ? '开放中' : '信息更新中' }}</span>
                <h3>{{ featuredAttraction.name }}</h3>
                <p class="attraction-feature__location">{{ featuredAttraction.location || '位置待补充' }}</p>
                <p class="attraction-feature__description">
                  {{ excerpt(featuredAttraction.description, 120) || '提供景点简介、开放信息和门票参考。' }}
                </p>
                <div class="attraction-feature__meta">
                  <span>{{ featuredAttraction.openTime || '开放时间待补充' }}</span>
                  <span>{{ formatTicketPrice(featuredAttraction.ticketPrice) }}</span>
                </div>
              </div>
            </button>

            <div class="attraction-grid">
              <button
                v-for="item in attractionCards"
                :key="item.id"
                type="button"
                class="attraction-card"
                @click="openAttractionDetail(item.id)"
              >
                <div class="attraction-card__media">
                  <img v-if="item.coverImage" :src="item.coverImage" :alt="item.name">
                  <div v-else class="media-fallback media-fallback--attraction">{{ item.location || '景点' }}</div>
                </div>
                <div class="attraction-card__body">
                  <h3>{{ item.name }}</h3>
                  <p>{{ item.location || '位置待补充' }}</p>
                </div>
              </button>
            </div>
          </div>

          <div v-else class="empty-card">暂无景点信息。</div>
        </section>
      </div>

      <aside class="home-side">
        <section class="side-panel">
          <div class="section-head section-head--compact">
            <div>
              <p class="section-kicker">SYSTEM NOTICE</p>
              <h2 class="section-title">公告与提醒</h2>
            </div>
          </div>

          <div v-if="latestNotices.length" class="notice-list">
            <article v-for="notice in latestNotices" :key="notice.id || notice.title" class="notice-item">
              <p class="notice-item__date">{{ formatNoticeDate(notice.createdAt) }}</p>
              <h3>{{ notice.title }}</h3>
              <p>{{ excerpt(notice.content, 88) || '系统公告与出行提示会在这里统一展示。' }}</p>
            </article>
          </div>

          <div v-else class="empty-card empty-card--soft">暂无公告信息。</div>
        </section>

        <section class="side-panel side-panel--account" :style="homeHeroStyle">
          <p class="section-kicker">ORDER SERVICE</p>
          <h2 class="section-title">{{ accountButtonLabel }}</h2>
          <p class="side-panel__text">{{ accountDescription }}</p>
          <button type="button" class="hero-button hero-button--primary hero-button--full" @click="openAccount">
            {{ accountButtonLabel }}
          </button>
        </section>

        <section class="side-panel">
          <div class="section-head section-head--compact">
            <div>
              <p class="section-kicker">SERVICE FLOW</p>
              <h2 class="section-title">预订流程</h2>
            </div>
          </div>

          <div class="service-list">
            <article v-for="item in serviceItems" :key="item.title" class="service-item">
              <span class="service-item__icon">
                <Sparkles class="h-4 w-4" />
              </span>
              <div>
                <h3>{{ item.title }}</h3>
                <p>{{ item.text }}</p>
              </div>
            </article>
          </div>
        </section>
      </aside>
    </section>
  </section>
</template>

<style scoped>
.home-page {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.home-hero {
  position: relative;
  display: grid;
  grid-template-columns: minmax(0, 1.2fr) minmax(320px, 0.8fr);
  gap: 20px;
  padding: 32px;
  border-radius: 28px;
  overflow: hidden;
  background:
    linear-gradient(90deg, rgba(10, 24, 16, 0.74) 0%, rgba(14, 33, 22, 0.56) 34%, rgba(27, 56, 39, 0.24) 100%),
    linear-gradient(180deg, rgba(247, 241, 227, 0.30) 0%, rgba(220, 231, 219, 0.14) 20%, rgba(8, 23, 15, 0.48) 100%),
    var(--hero-image) center 38% / cover no-repeat;
  color: #fff;
  box-shadow: 0 24px 60px rgba(var(--brand-primary-ring), 0.22);
}

.home-hero::before {
  content: "";
  position: absolute;
  inset: 0;
  background:
    radial-gradient(circle at 16% 10%, rgba(255, 247, 231, 0.42) 0%, transparent 26%),
    radial-gradient(circle at 78% 14%, rgba(245, 239, 220, 0.26) 0%, transparent 20%),
    linear-gradient(180deg, rgba(255, 255, 255, 0.12) 0%, transparent 42%, rgba(7, 19, 12, 0.18) 100%);
  pointer-events: none;
}

.home-hero::after {
  content: "";
  position: absolute;
  left: 0;
  right: 0;
  bottom: 0;
  height: 42%;
  background:
    linear-gradient(180deg, rgba(7, 18, 12, 0) 0%, rgba(7, 18, 12, 0.34) 42%, rgba(6, 16, 11, 0.82) 100%);
  pointer-events: none;
}

.home-hero__content {
  position: relative;
  z-index: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.home-hero__kicker,
.section-kicker,
.route-card__path,
.notice-item__date {
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 0.16em;
  text-transform: uppercase;
}

.home-hero__kicker {
  color: rgba(255, 255, 255, 0.72);
}

.home-hero h1 {
  margin-top: 12px;
  font-size: clamp(32px, 4.8vw, 52px);
  line-height: 1.05;
  letter-spacing: -0.04em;
}

.home-hero__description {
  margin-top: 16px;
  max-width: 680px;
  color: rgba(255, 255, 255, 0.88);
  font-size: 15px;
  line-height: 1.85;
}

.home-hero__actions {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-top: 24px;
}

.hero-button {
  min-height: 44px;
  padding: 0 18px;
  border-radius: 12px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  font-size: 14px;
  font-weight: 700;
  transition: all 0.2s ease;
}

.hero-button--primary {
  background: #fff;
  color: var(--brand-dark);
}

.hero-button--primary:hover {
  transform: translateY(-2px);
}

.hero-button--secondary {
  background: rgba(255, 255, 255, 0.16);
  border: 1px solid rgba(255, 255, 255, 0.24);
  color: #fff;
}

.hero-button--full {
  width: 100%;
}

.home-hero__chips {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-top: 20px;
}

.hero-chip {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 8px 12px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.16);
  color: #fff;
  font-size: 13px;
}

.home-hero__panel {
  position: relative;
  z-index: 1;
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.stat-grid {
  display: grid;
  gap: 12px;
}

.stat-card,
.featured-summary,
.content-section,
.side-panel,
.route-card,
.attraction-feature,
.attraction-card {
  border-radius: 20px;
  border: 1px solid var(--border);
}

.stat-card {
  padding: 16px;
  background: rgba(255, 255, 255, 0.08);
  border: 1px solid rgba(255, 255, 255, 0.14);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  transition: transform 0.3s ease, border-color 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-2px);
  border-color: rgba(255, 255, 255, 0.28);
}

.stat-card__icon {
  width: 38px !important;
  height: 38px !important;
  display: flex !important;
  align-items: center !important;
  justify-content: center !important;
  border-radius: 11px;
  border: 1px solid transparent;
  transition: all 0.3s ease;
}

.stat-card p {
  margin-top: 10px;
  color: rgba(255, 255, 255, 0.7);
  font-size: 13px;
}

.stat-card strong {
  display: block;
  margin-top: 6px;
  font-size: 24px;
}

.stat-card span {
  display: block;
  margin-top: 6px;
  color: rgba(255, 255, 255, 0.78);
  font-size: 12px;
  line-height: 1.6;
}

.featured-summary {
  width: 100%;
  padding: 18px;
  background: #fff;
  color: hsl(var(--text-100));
  text-align: left;
  box-shadow: var(--shadow-panel);
}

.featured-summary__head,
.route-card__footer,
.section-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.featured-summary__tag {
  padding: 6px 10px;
  border-radius: 999px;
  background: rgba(var(--brand-primary-ring), 0.1);
  color: var(--brand);
  font-size: 12px;
  font-weight: 700;
}

.featured-summary__price {
  color: var(--brand);
  font-size: 14px;
  font-weight: 700;
}

.featured-summary h2 {
  margin-top: 14px;
  font-size: 24px;
  line-height: 1.2;
}

.featured-summary p {
  margin-top: 8px;
  color: hsl(var(--text-200));
  font-size: 14px;
}

.featured-summary__meta {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 14px;
}

.featured-summary__meta span,
.attraction-feature__meta span,
.feature-tag,
.route-card__badge {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  border-radius: 999px;
  font-size: 12px;
  font-weight: 700;
}

.featured-summary__meta span {
  padding: 6px 10px;
  background: rgba(var(--accent-rgb), 0.12);
  color: var(--accent);
}

.home-layout {
  display: grid;
  grid-template-columns: minmax(0, 1.18fr) 360px;
  gap: 20px;
}

.home-main,
.home-side {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.content-section,
.side-panel {
  background: hsl(var(--panel));
  padding: 22px;
  box-shadow: var(--shadow-panel);
}

.section-head {
  margin-bottom: 18px;
}

.section-head--compact {
  margin-bottom: 14px;
}

.section-kicker {
  color: var(--brand);
}

.section-title {
  margin-top: 6px;
  font-size: 26px;
  line-height: 1.15;
  color: hsl(var(--text-100));
}

.section-link {
  min-height: 40px;
  padding: 0 14px;
  display: inline-flex;
  align-items: center;
  gap: 8px;
  border-radius: 10px;
  border: 1px solid var(--border);
  background: hsl(var(--panel));
  color: hsl(var(--text-100));
  font-size: 14px;
  font-weight: 600;
}

.route-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 16px;
}

.route-card,
.attraction-feature,
.attraction-card {
  overflow: hidden;
  background: linear-gradient(180deg, rgba(var(--brand-soft-rgb), 0.16) 0%, hsl(var(--panel)) 60%);
  text-align: left;
  transition: transform 0.2s ease, box-shadow 0.2s ease, border-color 0.2s ease;
}

.route-card:hover,
.attraction-feature:hover,
.attraction-card:hover {
  transform: translateY(-3px);
  box-shadow: var(--shadow-lg);
  border-color: rgba(var(--brand-primary-ring), 0.22);
}

.route-card__media,
.attraction-feature__media,
.attraction-card__media {
  position: relative;
  background: linear-gradient(180deg, rgba(var(--brand-soft-rgb), 0.38) 0%, rgba(var(--accent-rgb), 0.12) 100%);
}

.route-card__media,
.attraction-card__media {
  aspect-ratio: 4 / 3;
}

.attraction-feature__media {
  aspect-ratio: 16 / 9;
}

.route-card__media img,
.attraction-feature__media img,
.attraction-card__media img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.route-card__badge {
  position: absolute;
  top: 12px;
  right: 12px;
  min-height: 28px;
  padding: 0 10px;
  background: var(--accent);
  color: #fff;
}

.route-card__body,
.attraction-feature__body,
.attraction-card__body {
  padding: 16px;
}

.route-card__path {
  color: hsl(var(--text-300));
}

.route-card h3,
.attraction-feature h3,
.attraction-card h3,
.notice-item h3,
.service-item h3 {
  margin-top: 8px;
  color: hsl(var(--text-100));
}

.route-card__description,
.notice-item p,
.side-panel__text,
.service-item p,
.attraction-feature__location,
.attraction-feature__description,
.attraction-card p {
  color: hsl(var(--text-200));
  line-height: 1.7;
}

.route-card__description,
.attraction-feature__description,
.service-item p {
  margin-top: 10px;
}

.route-card__footer {
  margin-top: 14px;
  padding-top: 14px;
  border-top: 1px solid var(--border);
}

.route-card__footer span,
.attraction-card p {
  font-size: 13px;
  color: hsl(var(--text-300));
}

.route-card__footer strong {
  color: var(--brand);
  font-size: 18px;
}

.attraction-layout {
  display: grid;
  grid-template-columns: minmax(0, 1.05fr) minmax(240px, 0.95fr);
  gap: 16px;
}

.feature-tag {
  min-height: 28px;
  padding: 0 10px;
  background: rgba(var(--accent-rgb), 0.12);
  color: var(--accent);
}

.attraction-feature__location {
  margin-top: 10px;
}

.attraction-feature__meta {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 14px;
}

.attraction-feature__meta span {
  min-height: 28px;
  padding: 0 10px;
  background: rgba(var(--brand-primary-ring), 0.1);
  color: hsl(var(--text-100));
}

.attraction-grid {
  display: grid;
  gap: 16px;
}

.notice-list,
.service-list {
  display: grid;
  gap: 12px;
}

.notice-item,
.service-item {
  padding: 14px;
  border-radius: 14px;
  border: 1px solid var(--border);
  background: rgba(var(--brand-primary-ring), 0.04);
}

.notice-item__date {
  color: var(--accent);
}

.side-panel--account {
  position: relative;
  overflow: hidden;
  background:
    linear-gradient(180deg, rgba(245, 240, 222, 0.16) 0%, rgba(18, 39, 27, 0.24) 24%, rgba(6, 18, 12, 0.82) 100%),
    linear-gradient(110deg, rgba(9, 24, 16, 0.78) 0%, rgba(15, 33, 23, 0.52) 40%, rgba(28, 56, 39, 0.18) 100%),
    var(--hero-image) center 54% / cover no-repeat;
}

.side-panel--account::before {
  content: "";
  position: absolute;
  inset: 0;
  background:
    radial-gradient(circle at 78% 12%, rgba(255, 247, 224, 0.26) 0%, transparent 18%),
    linear-gradient(180deg, rgba(255, 255, 255, 0.08) 0%, transparent 36%, rgba(5, 18, 12, 0.12) 100%);
  pointer-events: none;
}

.side-panel--account::after {
  content: "";
  position: absolute;
  left: 0;
  right: 0;
  bottom: 0;
  height: 42%;
  background: linear-gradient(180deg, rgba(6, 16, 11, 0) 0%, rgba(6, 16, 11, 0.34) 36%, rgba(4, 12, 8, 0.9) 100%);
  pointer-events: none;
}

.side-panel--account > * {
  position: relative;
  z-index: 1;
}

.side-panel--account .hero-button--primary {
  background: rgba(255, 255, 255, 0.92);
  color: #153122;
}

.side-panel--account .hero-button--primary:hover {
  background: #fff;
}

.side-panel--account .section-kicker,
.side-panel--account .section-title,
.side-panel--account .side-panel__text {
  color: #fff;
}

.service-item {
  display: grid;
  grid-template-columns: auto 1fr;
  gap: 12px;
}

.service-item__icon {
  width: 34px;
  height: 34px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  border-radius: 10px;
  background: rgba(var(--brand-primary-ring), 0.1);
  color: var(--brand);
}

.media-fallback {
  width: 100%;
  height: 100%;
  display: grid;
  place-items: center;
  padding: 16px;
  text-align: center;
  color: #fff;
  font-size: 14px;
  font-weight: 700;
}

.media-fallback--route {
  background: linear-gradient(160deg, rgba(var(--brand-dark-rgb), 0.9) 0%, rgba(var(--accent-rgb), 0.52) 100%);
}

.media-fallback--attraction {
  background: linear-gradient(160deg, rgba(var(--brand-dark-rgb), 0.82) 0%, rgba(var(--brand-primary-ring), 0.58) 100%);
}

.empty-card {
  padding: 18px;
  border-radius: 16px;
  border: 1px dashed var(--border-strong);
  background: linear-gradient(180deg, rgba(var(--brand-soft-rgb), 0.14) 0%, hsl(var(--panel)) 100%);
  color: hsl(var(--text-200));
}

.empty-card--soft {
  padding: 14px;
}

@media (max-width: 1180px) {
  .home-hero,
  .home-layout,
  .attraction-layout {
    grid-template-columns: 1fr;
  }

  .route-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 768px) {
  .home-hero,
  .content-section,
  .side-panel {
    padding: 18px;
  }

  .section-head {
    flex-direction: column;
    align-items: flex-start;
  }

  .route-grid {
    grid-template-columns: 1fr;
  }
}
</style>
