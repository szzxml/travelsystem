<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import {
  ArrowRight,
  Compass,
  MapPinned,
  Mountain,
  Sparkles,
  Search,
  Building2,
  Calendar,
  ShieldCheck,
  Clock,
  ChevronRight,
  BadgeCheck
} from 'lucide-vue-next'
import forestHero from '@/assets/forest-hero.jpg'
import { useUserStore } from '@/stores/user'
import { getPublicRoutes, getPublicAttractions, getPublicNotices } from '@/api/public'

const router = useRouter()
const userStore = useUserStore()
const loading = ref(false)
const routes = ref([])
const attractions = ref([])
const notices = ref([])
const searchKeyword = ref('')

const homeHeroStyle = {
  backgroundImage: `url(${forestHero})`,
  backgroundSize: 'cover',
  backgroundPosition: 'center',
}

const fallbackRouteImages = [
  'https://images.unsplash.com/photo-1528127269322-539801943592?auto=format&fit=crop&w=800&q=80',
  'https://images.unsplash.com/photo-1534447677768-be436bb09401?auto=format&fit=crop&w=800&q=80',
  'https://images.unsplash.com/photo-1507525428034-b723cf961d3e?auto=format&fit=crop&w=800&q=80',
  'https://images.unsplash.com/photo-1544644181-1484b3fdfc62?auto=format&fit=crop&w=800&q=80',
  'https://images.unsplash.com/photo-1508804185872-d7badad00f7d?auto=format&fit=crop&w=800&q=80',
  'https://images.unsplash.com/photo-1516483638261-f4dbaf036963?auto=format&fit=crop&w=800&q=80',
]

const fallbackAttractionImages = [
  'https://images.unsplash.com/photo-1464822759023-fed622ff2c3b?auto=format&fit=crop&w=800&q=80',
  'https://images.unsplash.com/photo-1548013146-72479768bada?auto=format&fit=crop&w=800&q=80',
  'https://images.unsplash.com/photo-1477959858617-67f30bc75b82?auto=format&fit=crop&w=800&q=80',
  'https://images.unsplash.com/photo-1441974231531-c6227db76b6e?auto=format&fit=crop&w=800&q=80',
  'https://images.unsplash.com/photo-1501785888041-af3ef285b470?auto=format&fit=crop&w=800&q=80',
  'https://images.unsplash.com/photo-1513836279014-a89f7a76ae86?auto=format&fit=crop&w=800&q=80',
]

function getRouteImage(route, index = 0) {
  if (route?.coverImage && String(route.coverImage).trim()) {
    return route.coverImage.trim()
  }
  return fallbackRouteImages[index % fallbackRouteImages.length]
}

function getAttractionImage(item, index = 0) {
  if (item?.coverImage && String(item.coverImage).trim()) {
    return item.coverImage.trim()
  }
  return fallbackAttractionImages[index % fallbackAttractionImages.length]
}

function handleImageError(event) {
  if (event?.target) {
    event.target.src = forestHero
  }
}

const destinationChips = computed(() => {
  const values = routes.value
    .map((item) => item.destination || item.title)
    .filter(Boolean)
  return Array.from(new Set(values)).slice(0, 6)
})

const routeCards = computed(() => {
  return routes.value.slice(0, 6)
})

const attractionCards = computed(() => {
  return attractions.value.slice(0, 4)
})

const latestNotices = computed(() => notices.value.slice(0, 3))

const startingPrice = computed(() => {
  const priceList = routes.value
    .map((item) => Number(item.price))
    .filter((value) => Number.isFinite(value) && value > 0)
  return priceList.length ? Math.min(...priceList) : null
})

const heroMetrics = computed(() => [
  {
    label: '精选线路',
    value: `${routes.value.length || 0}+`,
    desc: '覆盖全国热门目的地',
    icon: Compass,
  },
  {
    label: '深度景点',
    value: `${attractions.value.length || 0}+`,
    desc: '特色地标与门票指南',
    icon: Mountain,
  },
  {
    label: '品质出行',
    value: startingPrice.value ? `￥${formatPrice(startingPrice.value)}起` : '特惠优选',
    desc: '合作星级酒店联订保障',
    icon: Building2,
  },
])

const bookingSteps = [
  {
    step: '01',
    title: '挑选线路与合作酒店',
    desc: '多维度按目的地、出游天数与星级酒店筛选，透明行程一口价。',
    icon: MapPinned,
  },
  {
    step: '02',
    title: '在线提交出行信息',
    desc: '选择出行日期与人数，一键填写联系人并由系统事务保障锁单。',
    icon: Calendar,
  },
  {
    step: '03',
    title: '极速确认与全程安心',
    desc: '管理员审核确认并分配出团保障，订单状态流转实时透明可查。',
    icon: ShieldCheck,
  },
]

async function load() {
  loading.value = true
  try {
    const [routeRes, attractionRes, noticeRes] = await Promise.all([
      getPublicRoutes({ page: 1, size: 9 }),
      getPublicAttractions({ page: 1, size: 8 }),
      getPublicNotices({ page: 1, size: 4 }),
    ])

    routes.value = routeRes.data.items || []
    attractions.value = attractionRes.data.items || []
    notices.value = noticeRes.data.items || []
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  const query = searchKeyword.value.trim()
  if (query) {
    router.push({ path: '/routes', query: { keyword: query } })
  } else {
    router.push('/routes')
  }
}

function quickSearch(keyword) {
  router.push({ path: '/routes', query: { keyword } })
}

function openRouteList() {
  router.push('/routes')
}

function openAttractionList() {
  router.push('/attractions')
}

function openRouteDetail(id) {
  if (id) {
    router.push(`/routes/${id}`)
  } else {
    openRouteList()
  }
}

function openAttractionDetail(id) {
  if (id) {
    router.push(`/attractions/${id}`)
  } else {
    openAttractionList()
  }
}

function formatPrice(value) {
  const amount = Number(value)
  if (!Number.isFinite(amount)) return '--'
  return amount.toLocaleString()
}

function formatTicketPrice(value) {
  const amount = Number(value)
  if (!Number.isFinite(amount) || amount <= 0) return '免费开放'
  return `￥${amount.toLocaleString()}`
}

function formatNoticeDate(value) {
  if (!value) return '今日发布'
  const date = new Date(value)
  if (Number.isNaN(date.getTime())) return '今日发布'
  return new Intl.DateTimeFormat('zh-CN', {
    month: '2-digit',
    day: '2-digit',
  }).format(date)
}

function excerpt(text, maxLength = 60) {
  if (!text) return ''
  const normalized = String(text).replace(/\s+/g, ' ').trim()
  return normalized.length > maxLength ? `${normalized.slice(0, maxLength)}...` : normalized
}

onMounted(load)
</script>

<template>
  <div class="home-portal space-y-12 pb-16" v-loading="loading">
    
    <!-- Hero Section with Immersive Banner & Floating Search Bar -->
    <section class="hero-wrapper relative rounded-[32px] overflow-hidden text-white shadow-2xl" :style="homeHeroStyle">
      <div class="hero-overlay absolute inset-0 bg-gradient-to-r from-slate-950/85 via-slate-900/70 to-teal-950/60 backdrop-blur-[2px]"></div>
      
      <div class="relative z-10 max-w-5xl mx-auto px-6 py-16 sm:py-20 lg:py-24 text-center flex flex-col items-center">
        <!-- Eyebrow Tag -->
        <div class="inline-flex items-center gap-2 px-3.5 py-1.5 rounded-full bg-white/12 backdrop-blur-md border border-white/20 text-xs font-semibold tracking-wider text-emerald-300 mb-6 shadow-sm">
          <Sparkles class="w-3.5 h-3.5" />
          <span>智能文旅 · 一站式旅游服务与资源管理平台</span>
        </div>

        <!-- Headline -->
        <h1 class="text-3xl sm:text-5xl lg:text-6xl font-black tracking-tight leading-tight max-w-4xl text-balance">
          探索心之所向，开启难忘旅程
        </h1>
        <p class="mt-4 text-sm sm:text-base lg:text-lg text-slate-200/90 max-w-2xl font-light leading-relaxed">
          甄选当季优质旅游路线，联动高品质合作酒店与热门景区，为您提供透明可信的在线预订与出行保障
        </p>

        <!-- Floating Search Capsule -->
        <div class="mt-8 w-full max-w-3xl bg-white/95 dark:bg-slate-900/95 backdrop-blur-md rounded-2xl p-2.5 shadow-2xl border border-white/30 dark:border-slate-800 text-slate-800 dark:text-slate-100 flex flex-col sm:flex-row items-center gap-2.5">
          <div class="flex items-center gap-3 px-4 py-2.5 w-full sm:flex-1 bg-slate-50 dark:bg-slate-800/80 rounded-xl border border-slate-200/60 dark:border-slate-700/60">
            <Search class="w-5 h-5 text-teal-600 dark:text-teal-400 shrink-0" />
            <input
              v-model="searchKeyword"
              type="text"
              placeholder="搜索目的地、路线名称（如：云南、三亚、桂林）..."
              class="w-full bg-transparent border-none text-xs sm:text-sm text-slate-900 dark:text-white placeholder-slate-400 focus:outline-none"
              @keyup.enter="handleSearch"
            />
          </div>

          <button
            type="button"
            class="w-full sm:w-auto px-7 py-3 bg-gradient-to-r from-teal-600 to-emerald-600 hover:from-teal-500 hover:to-emerald-500 text-white font-bold text-sm rounded-xl shadow-lg shadow-teal-600/30 transition-all flex items-center justify-center gap-2 shrink-0 active:scale-95"
            @click="handleSearch"
          >
            <span>搜索线路</span>
            <ArrowRight class="w-4 h-4" />
          </button>
        </div>

        <!-- Destination Fast Chips -->
        <div v-if="destinationChips.length" class="mt-5 flex flex-wrap items-center justify-center gap-2 text-xs text-slate-300">
          <span class="text-slate-400 font-medium">热门目的地：</span>
          <button
            v-for="chip in destinationChips"
            :key="chip"
            type="button"
            class="px-3 py-1 rounded-full bg-white/10 hover:bg-white/20 border border-white/15 transition backdrop-blur-sm text-slate-200"
            @click="quickSearch(chip)"
          >
            {{ chip }}
          </button>
        </div>
      </div>

      <!-- Trust Metrics Bar -->
      <div class="relative z-10 border-t border-white/15 bg-slate-950/40 backdrop-blur-md px-6 py-4">
        <div class="max-w-5xl mx-auto grid grid-cols-1 sm:grid-cols-3 gap-4 text-center divide-y sm:divide-y-0 sm:divide-x divide-white/10">
          <div v-for="item in heroMetrics" :key="item.label" class="flex items-center justify-center gap-3.5 py-2 sm:py-0">
            <div class="w-10 h-10 rounded-xl flex items-center justify-center shrink-0 bg-white/10 border border-white/15 text-emerald-300">
              <component :is="item.icon" class="w-5 h-5" />
            </div>
            <div class="text-left">
              <div class="flex items-baseline gap-1.5">
                <span class="text-lg font-black text-white">{{ item.value }}</span>
                <span class="text-xs font-semibold text-emerald-300/90">{{ item.label }}</span>
              </div>
              <p class="text-[11px] text-slate-300/80">{{ item.desc }}</p>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- Main Section: Featured Routes (Full Width Modern 3-Column Grid) -->
    <section class="space-y-6">
      <div class="flex flex-col sm:flex-row sm:items-end justify-between gap-4 border-b border-slate-200/80 dark:border-slate-800/80 pb-4">
        <div>
          <div class="flex items-center gap-2 text-xs font-bold text-teal-600 dark:text-teal-400 uppercase tracking-widest">
            <Compass class="w-4 h-4" />
            <span>POPULAR TOUR ROUTES</span>
          </div>
          <h2 class="text-2xl sm:text-3xl font-black text-slate-900 dark:text-white mt-1">
            热门精选旅游线路
          </h2>
          <p class="text-xs sm:text-sm text-slate-500 dark:text-slate-400 mt-1">
            严选品质保障、包含合作酒店与专业行程安排的当季精品线路
          </p>
        </div>

        <button
          type="button"
          class="inline-flex items-center gap-1.5 text-sm font-bold text-teal-600 hover:text-teal-700 dark:text-teal-400 group transition shrink-0"
          @click="openRouteList"
        >
          <span>查看全部线路</span>
          <ArrowRight class="w-4 h-4 transition-transform group-hover:translate-x-1" />
        </button>
      </div>

      <!-- Route Cards Grid -->
      <div v-if="routeCards.length" class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        <div
          v-for="(route, index) in routeCards"
          :key="route.id"
          class="route-card group bg-white dark:bg-slate-900 rounded-2xl overflow-hidden border border-slate-200/80 dark:border-slate-800 shadow-sm hover:shadow-xl transition-all duration-300 hover:-translate-y-1 flex flex-col cursor-pointer"
          @click="openRouteDetail(route.id)"
        >
          <!-- Card Image & Badges -->
          <div class="relative h-48 sm:h-52 w-full overflow-hidden bg-slate-100 dark:bg-slate-800">
            <img
              :src="getRouteImage(route, index)"
              :alt="route.title"
              class="w-full h-full object-cover transition-transform duration-500 group-hover:scale-105"
              @error="handleImageError"
            />

            <!-- Duration Badge -->
            <div class="absolute top-3 right-3 px-2.5 py-1 rounded-full bg-black/60 backdrop-blur-md text-white font-bold text-xs shadow-sm">
              {{ route.days }} 天行程
            </div>

            <!-- Departure -> Destination Pill -->
            <div class="absolute bottom-3 left-3 px-3 py-1 rounded-lg bg-slate-950/70 backdrop-blur-md text-white text-xs font-semibold flex items-center gap-1.5">
              <span>{{ route.departure || '出发地' }}</span>
              <span class="text-teal-400">→</span>
              <span>{{ route.destination || '目的地' }}</span>
            </div>
          </div>

          <!-- Card Content Body -->
          <div class="p-5 flex-1 flex flex-col justify-between space-y-4">
            <div>
              <!-- Hotel Linkage Badge -->
              <div class="mb-2.5">
                <span
                  v-if="route.hotel"
                  class="inline-flex items-center gap-1.5 text-[11px] font-semibold px-2.5 py-1 rounded-md bg-emerald-50 dark:bg-emerald-950/60 text-emerald-700 dark:text-emerald-300 border border-emerald-200/60 dark:border-emerald-800/60"
                >
                  <Building2 class="w-3.5 h-3.5 text-emerald-600" />
                  <span>合作酒店：{{ route.hotel.name }} · {{ route.hotel.starLevel ? `${route.hotel.starLevel}星` : '特选' }}</span>
                </span>
                <span
                  v-else
                  class="inline-flex items-center gap-1.5 text-[11px] font-medium px-2.5 py-1 rounded-md bg-slate-50 dark:bg-slate-800 text-slate-500 dark:text-slate-400"
                >
                  <Building2 class="w-3.5 h-3.5" />
                  <span>精选优质酒店</span>
                </span>
              </div>

              <!-- Title -->
              <h3 class="text-base font-bold text-slate-900 dark:text-white line-clamp-2 group-hover:text-teal-600 dark:group-hover:text-teal-400 transition-colors">
                {{ route.title }}
              </h3>

              <!-- Description -->
              <p class="mt-2 text-xs text-slate-500 dark:text-slate-400 line-clamp-2 leading-relaxed">
                {{ excerpt(route.description, 60) || '提供标准化线路规划与优质出游体验，支持在线预订与行程保障。' }}
              </p>
            </div>

            <!-- Card Footer -->
            <div class="pt-3 border-t border-slate-100 dark:border-slate-800 flex items-center justify-between">
              <div class="text-[11px] text-slate-400 font-medium">
                {{ route.maxGroupSize ? `成团上限 ${route.maxGroupSize} 人` : '支持团队出行' }}
              </div>

              <div class="flex items-baseline gap-1">
                <span class="text-xs text-teal-600 dark:text-teal-400 font-bold">￥</span>
                <span class="text-xl font-black text-teal-600 dark:text-teal-400">{{ formatPrice(route.price) }}</span>
                <span class="text-[10px] text-slate-400">/人</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div v-else class="text-center py-16 bg-slate-50 dark:bg-slate-900 rounded-2xl border border-dashed border-slate-300 dark:border-slate-800">
        <p class="text-sm text-slate-400">暂无可展示线路，管理员可在后台新增线路数据。</p>
      </div>
    </section>

    <!-- Attractions Showcase Section (Grid & Visual Cards) -->
    <section class="space-y-6">
      <div class="flex flex-col sm:flex-row sm:items-end justify-between gap-4 border-b border-slate-200/80 dark:border-slate-800/80 pb-4">
        <div>
          <div class="flex items-center gap-2 text-xs font-bold text-emerald-600 dark:text-emerald-400 uppercase tracking-widest">
            <Mountain class="w-4 h-4" />
            <span>DESTINATIONS & ATTRACTIONS</span>
          </div>
          <h2 class="text-2xl sm:text-3xl font-black text-slate-900 dark:text-white mt-1">
            必游景点与名胜打卡
          </h2>
          <p class="text-xs sm:text-sm text-slate-500 dark:text-slate-400 mt-1">
            发现沿途风光，掌握开放时间与门票指南
          </p>
        </div>

        <button
          type="button"
          class="inline-flex items-center gap-1.5 text-sm font-bold text-emerald-600 hover:text-emerald-700 dark:text-emerald-400 group transition shrink-0"
          @click="openAttractionList"
        >
          <span>查看全部景点</span>
          <ArrowRight class="w-4 h-4 transition-transform group-hover:translate-x-1" />
        </button>
      </div>

      <div v-if="attractionCards.length" class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-5">
        <div
          v-for="(item, index) in attractionCards"
          :key="item.id"
          class="group bg-white dark:bg-slate-900 rounded-2xl overflow-hidden border border-slate-200/80 dark:border-slate-800 shadow-sm hover:shadow-lg transition-all duration-300 hover:-translate-y-1 flex flex-col cursor-pointer"
          @click="openAttractionDetail(item.id)"
        >
          <div class="relative h-40 w-full overflow-hidden bg-slate-100 dark:bg-slate-800">
            <img
              :src="getAttractionImage(item, index)"
              :alt="item.name"
              class="w-full h-full object-cover transition-transform duration-500 group-hover:scale-105"
              @error="handleImageError"
            />

            <div class="absolute bottom-2 left-2 px-2.5 py-0.5 rounded-md bg-black/60 backdrop-blur-md text-white text-[11px] font-medium flex items-center gap-1">
              <MapPinned class="w-3 h-3 text-emerald-400" />
              <span>{{ item.location || '热门地标' }}</span>
            </div>
          </div>

          <div class="p-4 flex-1 flex flex-col justify-between">
            <div>
              <h4 class="font-bold text-sm text-slate-900 dark:text-white group-hover:text-emerald-600 dark:group-hover:text-emerald-400 transition-colors">
                {{ item.name }}
              </h4>
              <p class="text-[11px] text-slate-400 line-clamp-2 mt-1 leading-relaxed">
                {{ excerpt(item.description, 45) || '自然景观与人文历史融合的优选旅行打卡地。' }}
              </p>
            </div>

            <div class="mt-4 pt-2.5 border-t border-slate-100 dark:border-slate-800 flex items-center justify-between text-xs">
              <span class="text-slate-400 text-[11px] truncate max-w-[120px]">{{ item.openTime || '全天开放' }}</span>
              <span class="font-bold text-emerald-600 dark:text-emerald-400">{{ formatTicketPrice(item.ticketPrice) }}</span>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- Bottom Section: 3-Step Booking Guide & Latest Notices Banner -->
    <section class="grid grid-cols-1 lg:grid-cols-12 gap-6 items-stretch">
      
      <!-- 3-Step Booking Guide (8 Cols) -->
      <div class="lg:col-span-8 bg-gradient-to-br from-teal-900/10 via-emerald-900/5 to-slate-900/5 dark:bg-slate-900/60 rounded-3xl p-6 sm:p-8 border border-teal-500/20 shadow-sm flex flex-col justify-between">
        <div>
          <div class="inline-flex items-center gap-2 text-xs font-bold text-teal-600 dark:text-teal-400 uppercase tracking-wider mb-2">
            <BadgeCheck class="w-4 h-4" />
            <span>SMOOTH TRAVEL PROCESS</span>
          </div>
          <h3 class="text-xl sm:text-2xl font-black text-slate-900 dark:text-white">
            只需 3 步，轻松启程
          </h3>
          <p class="text-xs sm:text-sm text-slate-500 dark:text-slate-400 mt-1">
            透明规范的预订流程与全流程事务保护，保障您的出行权益
          </p>
        </div>

        <div class="mt-6 grid grid-cols-1 sm:grid-cols-3 gap-4">
          <div
            v-for="step in bookingSteps"
            :key="step.step"
            class="bg-white/80 dark:bg-slate-800/80 backdrop-blur-sm p-4 rounded-2xl border border-teal-500/15 shadow-sm space-y-2.5"
          >
            <div class="flex items-center justify-between">
              <span class="text-xs font-black text-teal-600 dark:text-teal-400 bg-teal-500/10 px-2 py-0.5 rounded-md font-mono">{{ step.step }}</span>
              <component :is="step.icon" class="w-4 h-4 text-teal-600/80" />
            </div>
            <h4 class="font-bold text-xs sm:text-sm text-slate-900 dark:text-white">{{ step.title }}</h4>
            <p class="text-[11px] text-slate-500 dark:text-slate-400 leading-relaxed">{{ step.desc }}</p>
          </div>
        </div>
      </div>

      <!-- Latest Notices Panel (4 Cols) -->
      <div class="lg:col-span-4 bg-white dark:bg-slate-900 rounded-3xl p-6 sm:p-7 border border-slate-200/80 dark:border-slate-800 shadow-sm flex flex-col justify-between space-y-4">
        <div>
          <div class="flex items-center justify-between mb-2">
            <span class="text-xs font-bold text-amber-600 dark:text-amber-400 uppercase tracking-wider flex items-center gap-1.5">
              <Clock class="w-3.5 h-3.5" />
              <span>最新系统公告</span>
            </span>
          </div>
          <h3 class="text-lg font-black text-slate-900 dark:text-white">
            出行提示与动态
          </h3>
        </div>

        <div v-if="latestNotices.length" class="space-y-3 divide-y divide-slate-100 dark:divide-slate-800">
          <div v-for="notice in latestNotices" :key="notice.id" class="pt-2.5 first:pt-0">
            <div class="flex items-center justify-between text-[11px] text-slate-400 mb-1">
              <span class="font-medium text-amber-600 dark:text-amber-400 bg-amber-500/10 px-1.5 py-0.5 rounded">{{ notice.type || '公告' }}</span>
              <span>{{ formatNoticeDate(notice.createdAt) }}</span>
            </div>
            <h5 class="text-xs font-bold text-slate-800 dark:text-slate-200 line-clamp-1 hover:text-teal-600 cursor-pointer">
              {{ notice.title }}
            </h5>
            <p class="text-[11px] text-slate-500 dark:text-slate-400 line-clamp-2 mt-0.5 leading-relaxed">
              {{ notice.content }}
            </p>
          </div>
        </div>
        <div v-else class="text-xs text-slate-400 py-6 text-center">暂无系统公告。</div>

        <div class="pt-3 border-t border-slate-100 dark:border-slate-800">
          <button
            type="button"
            class="w-full py-2.5 px-4 bg-slate-100 dark:bg-slate-800 hover:bg-teal-50 hover:text-teal-700 dark:hover:bg-slate-700 text-slate-700 dark:text-slate-300 font-bold text-xs rounded-xl transition flex items-center justify-center gap-1"
            @click="openRouteList"
          >
            <span>开始您的旅行之旅</span>
            <ChevronRight class="w-3.5 h-3.5" />
          </button>
        </div>
      </div>

    </section>

  </div>
</template>

<style scoped>
.hero-wrapper {
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
}

.route-card {
  transition: transform 0.25s ease, box-shadow 0.25s ease;
}
</style>
