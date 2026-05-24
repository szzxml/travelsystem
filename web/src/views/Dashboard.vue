<script setup>
import { computed, onMounted, ref } from 'vue'
import { useI18n } from 'vue-i18n'
import { ElMessage } from 'element-plus'
import {
  ArrowDownTrayIcon,
  ArrowTrendingUpIcon,
  CalendarDaysIcon,
  CheckCircleIcon,
  ClockIcon,
  CurrencyDollarIcon,
  MapPinIcon,
  UserPlusIcon,
} from '@heroicons/vue/24/outline'
import { getOrders } from '@/api/orders'
import { exportStatsCsv, getStats } from '@/api/stats'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { BarChart, LineChart, PieChart } from 'echarts/charts'
import {
  GridComponent,
  TooltipComponent,
  LegendComponent,
  TitleComponent,
  DatasetComponent,
  TransformComponent
} from 'echarts/components'
import VChart from 'vue-echarts'

use([
  CanvasRenderer,
  BarChart,
  LineChart,
  PieChart,
  GridComponent,
  TooltipComponent,
  LegendComponent,
  TitleComponent,
  DatasetComponent,
  TransformComponent
])

const { t, locale } = useI18n()

const TRENDING_PAGE_SIZE = 100
const TRENDING_MAX_ORDERS = 500
const dashboardHeroStyle = {
  background: 'linear-gradient(135deg, var(--brand) 0%, var(--brand-dark) 55%, rgba(var(--accent-rgb), 0.96) 125%)',
  boxShadow: '0 30px 80px rgba(var(--brand-primary-ring), 0.22)',
}
const dashboardHeroGlowStyle = {
  background: 'radial-gradient(circle at top, rgba(255,255,255,0.28), transparent 60%)',
}

const loading = ref(false)
const exporting = ref(false)
const statsPayload = ref({
  totalRoutes: 0,
  totalAttractions: 0,
  totalUsers: 0,
  totalOrders: 0,
  todayOrders: 0,
  pendingOrders: 0,
  totalRevenue: 0,
})
const orderItems = ref([])

const destinationImageMap = {
  Maldives: 'https://images.unsplash.com/photo-1573843981267-be1999ff37cd?auto=format&fit=crop&w=240&q=80',
  Santorini: 'https://images.unsplash.com/photo-1570077188670-e3a8d69ac5ff?auto=format&fit=crop&w=240&q=80',
  Bali: 'https://images.unsplash.com/photo-1537996194471-e657df975ab4?auto=format&fit=crop&w=240&q=80',
  Kyoto: 'https://images.unsplash.com/photo-1545569341-9eb8b30979d9?auto=format&fit=crop&w=240&q=80',
  'Swiss Alps': 'https://images.unsplash.com/photo-1500530855697-b586d89ba3ee?auto=format&fit=crop&w=240&q=80',
  Dubai: 'https://images.unsplash.com/photo-1512453979798-5ea266f8880c?auto=format&fit=crop&w=240&q=80',
}

const statusKeyMap = {
  PENDING: 'dashboard.statusPending',
  CONFIRMED: 'dashboard.statusConfirmed',
  PAID: 'dashboard.statusPaid',
  CANCELLED: 'dashboard.statusCancelled',
  REFUNDING: 'dashboard.statusRefunding',
  REFUNDED: 'dashboard.statusRefunded',
  COMPLETED: 'dashboard.statusCompleted',
}

const statDefinitions = computed(() => [
  {
    title: t('dashboard.totalRevenue'),
    value: formatCurrency(statsPayload.value.totalRevenue),
    changeText: t('dashboard.todayOrdersLabel', { count: statsPayload.value.todayOrders }),
    caption: t('dashboard.revenueSubtitle'),
    icon: CurrencyDollarIcon,
    color: '#0f87a7',
    bgColor: 'rgba(15, 135, 167, 0.09)',
    glowColor: 'rgba(15, 135, 167, 0.04)',
    changeColor: 'text-[#0f87a7] dark:text-[#2ad0ff]',
  },
  {
    title: t('dashboard.activeTours'),
    value: formatNumber(statsPayload.value.totalRoutes),
    changeText: t('dashboard.attractionCountLabel', { count: statsPayload.value.totalAttractions }),
    caption: t('dashboard.activeToursSubtitle'),
    icon: MapPinIcon,
    color: '#10b981',
    bgColor: 'rgba(16, 185, 129, 0.09)',
    glowColor: 'rgba(16, 185, 129, 0.04)',
    changeColor: 'text-[#10b981] dark:text-[#34e8a1]',
  },
  {
    title: t('dashboard.newCustomers'),
    value: formatNumber(statsPayload.value.totalUsers),
    changeText: t('dashboard.totalOrdersLabel', { count: statsPayload.value.totalOrders }),
    caption: t('dashboard.customersSubtitle'),
    icon: UserPlusIcon,
    color: '#6366f1',
    bgColor: 'rgba(99, 102, 241, 0.09)',
    glowColor: 'rgba(99, 102, 241, 0.04)',
    changeColor: 'text-[#6366f1] dark:text-[#818cf8]',
  },
  {
    title: t('dashboard.pendingBookings'),
    value: formatNumber(statsPayload.value.pendingOrders),
    changeText: t('dashboard.growthValue'),
    caption: t('dashboard.pendingSubtitle'),
    icon: ClockIcon,
    color: '#f59e0b',
    bgColor: 'rgba(245, 158, 11, 0.09)',
    glowColor: 'rgba(245, 158, 11, 0.04)',
    changeColor: 'text-[#f59e0b] dark:text-[#fbbf24]',
  },
])

const recentBookings = computed(() => {
  return orderItems.value.slice(0, 5).map((order) => ({
    id: order.id || order.orderNo,
    customer: order.contactName || order.user?.realName || order.user?.username || '-',
    destination: order.route?.destination || order.route?.title || '-',
    date: order.travelDate || formatDate(order.createdAt),
    statusKey: statusKeyMap[order.status] || 'dashboard.statusPending',
  }))
})

const trendingDestinations = computed(() => {
  const grouped = new Map()

  orderItems.value.forEach((order) => {
    const destinationName = order.route?.destination || order.route?.title || t('dashboard.destination')
    const current = grouped.get(destinationName) || {
      name: destinationName,
      bookings: 0,
      image: order.route?.coverImage || destinationImageMap[destinationName] || '',
    }

    current.bookings += 1
    if (!current.image && order.route?.coverImage) {
      current.image = order.route.coverImage
    }
    grouped.set(destinationName, current)
  })

  return Array.from(grouped.values())
    .sort((a, b) => b.bookings - a.bookings)
    .slice(0, 3)
})

const trendChartOption = computed(() => {
  const dates = []
  const revenueData = []
  const orderData = []

  for (let i = 6; i >= 0; i--) {
    const d = new Date()
    d.setDate(d.getDate() - i)
    const dateStr = d.toISOString().split('T')[0]
    
    const month = String(d.getMonth() + 1).padStart(2, '0')
    const day = String(d.getDate()).padStart(2, '0')
    dates.push(`${month}-${day}`)

    const dayOrders = orderItems.value.filter(order => {
      if (!order.createdAt) return false
      return order.createdAt.startsWith(dateStr)
    })

    const dayRevenue = dayOrders.reduce((sum, order) => sum + Number(order.totalAmount || 0), 0)
    revenueData.push(dayRevenue)
    orderData.push(dayOrders.length)
  }

  return {
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'shadow' }
    },
    legend: {
      data: ['营收额', '订单数'],
      right: '4%',
      textStyle: { color: '#6b7280' }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: [
      {
        type: 'category',
        data: dates,
        axisLine: { lineStyle: { color: '#e5e7eb' } },
        axisLabel: { color: '#6b7280' }
      }
    ],
    yAxis: [
      {
        type: 'value',
        name: '营收 (元)',
        axisLine: { show: false },
        axisLabel: { color: '#6b7280' },
        splitLine: { lineStyle: { color: '#f3f4f6' } }
      },
      {
        type: 'value',
        name: '订单数',
        axisLine: { show: false },
        axisLabel: { color: '#6b7280' },
        splitLine: { show: false }
      }
    ],
    series: [
      {
        name: '营收额',
        type: 'line',
        smooth: true,
        data: revenueData,
        itemStyle: { color: '#0f87a7' },
        areaStyle: {
          color: {
            type: 'linear',
            x: 0,
            y: 0,
            x2: 0,
            y2: 1,
            colorStops: [
              { offset: 0, color: 'rgba(15, 135, 167, 0.3)' },
              { offset: 1, color: 'rgba(15, 135, 167, 0)' }
            ]
          }
        }
      },
      {
        name: '订单数',
        type: 'bar',
        yAxisIndex: 1,
        barWidth: '40%',
        data: orderData,
        itemStyle: {
          color: {
            type: 'linear',
            x: 0,
            y: 0,
            x2: 0,
            y2: 1,
            colorStops: [
              { offset: 0, color: '#f59e0b' },
              { offset: 1, color: '#fbbf24' }
            ]
          },
          borderRadius: [4, 4, 0, 0]
        }
      }
    ]
  }
})

const pieChartOption = computed(() => {
  const groupedAll = new Map()

  orderItems.value.forEach((order) => {
    const destinationName = order.route?.destination || order.route?.title || t('dashboard.destination')
    const current = groupedAll.get(destinationName) || 0
    groupedAll.set(destinationName, current + 1)
  })

  const sortedData = Array.from(groupedAll.entries())
    .map(([name, bookings]) => ({ name, value: bookings }))
    .sort((a, b) => b.value - a.value)

  let data = []
  if (sortedData.length > 5) {
    data = sortedData.slice(0, 5)
    const otherBookings = sortedData.slice(5).reduce((sum, item) => sum + item.value, 0)
    data.push({ name: '其他', value: otherBookings })
  } else {
    data = sortedData
  }

  return {
    tooltip: {
      trigger: 'item',
      formatter: '{b}: {c} 次预订 ({d}%)'
    },
    legend: {
      bottom: '0',
      left: 'center',
      textStyle: { color: '#6b7280' }
    },
    series: [
      {
        name: '目的地占比',
        type: 'pie',
        radius: ['45%', '70%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 8,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: {
          show: false,
          position: 'center'
        },
        emphasis: {
          label: {
            show: true,
            fontSize: 14,
            fontWeight: 'bold',
            color: '#1f2937'
          }
        },
        labelLine: {
          show: false
        },
        data: data.length ? data : [{ name: '暂无数据', value: 0 }],
        color: ['#0f87a7', '#10b981', '#6366f1', '#f59e0b', '#ec4899', '#3b82f6']
      }
    ]
  }
})

function formatCurrency(value) {
  return new Intl.NumberFormat(locale.value === 'zh' ? 'zh-CN' : 'en-US', {
    style: 'currency',
    currency: locale.value === 'zh' ? 'CNY' : 'USD',
    maximumFractionDigits: 0,
  }).format(Number(value || 0))
}

function formatNumber(value) {
  return new Intl.NumberFormat(locale.value === 'zh' ? 'zh-CN' : 'en-US').format(Number(value || 0))
}

function formatDate(value) {
  if (!value) return '-'
  const date = new Date(value)
  if (Number.isNaN(date.getTime())) return value

  return new Intl.DateTimeFormat(locale.value === 'zh' ? 'zh-CN' : 'en-US', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
  }).format(date)
}

function statusClasses(statusKey) {
  return statusKey === 'dashboard.statusConfirmed' || statusKey === 'dashboard.statusCompleted' || statusKey === 'dashboard.statusPaid'
    ? 'bg-blue-50 text-blue-700 ring-1 ring-blue-200'
    : statusKey === 'dashboard.statusPending'
      ? 'bg-accent/10 text-accent ring-1 ring-accent/20'
      : 'bg-brand/10 text-brand ring-1 ring-brand/20'
}

async function fetchOrdersForTrending() {
  const merged = []
  let page = 1
  let total = Number.POSITIVE_INFINITY

  while (merged.length < TRENDING_MAX_ORDERS && merged.length < total) {
    const size = Math.min(TRENDING_PAGE_SIZE, TRENDING_MAX_ORDERS - merged.length)
    const res = await getOrders({ page, size })
    const items = res.data.items || []
    total = Number(res.data.total || 0)

    if (!items.length) break

    merged.push(...items)
    if (items.length < size) break

    page += 1
  }

  return merged
}

async function loadDashboard() {
  loading.value = true

  try {
    const [statsRes, orders] = await Promise.all([
      getStats(),
      fetchOrdersForTrending(),
    ])

    statsPayload.value = {
      ...statsPayload.value,
      ...statsRes.data,
    }
    orderItems.value = orders
  } catch (error) {
    ElMessage.error(error?.response?.data?.message || '仪表盘数据加载失败')
  } finally {
    loading.value = false
  }
}

function getDownloadFilename(contentDisposition) {
  if (!contentDisposition) return ''
  const utf8Match = contentDisposition.match(/filename\*=UTF-8''([^;]+)/i)
  if (utf8Match?.[1]) {
    return decodeURIComponent(utf8Match[1])
  }
  const plainMatch = contentDisposition.match(/filename="?([^"]+)"?/i)
  return plainMatch?.[1] || ''
}

async function downloadStats() {
  exporting.value = true
  try {
    const response = await exportStatsCsv()
    const filename = getDownloadFilename(response.headers['content-disposition']) || 'travel-stats.csv'
    const blob = response.data instanceof Blob ? response.data : new Blob([response.data], { type: 'text/csv;charset=utf-8' })
    const url = URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = filename
    document.body.appendChild(link)
    link.click()
    link.remove()
    URL.revokeObjectURL(url)
    ElMessage.success('统计报表已开始下载')
  } catch (error) {
    ElMessage.error(error?.response?.data?.message || '统计报表导出失败')
  } finally {
    exporting.value = false
  }
}

onMounted(loadDashboard)
</script>

<template>
  <div class="flex flex-col gap-8 rounded-[32px] bg-white/50 p-1 text-gray-900 transition-colors duration-300">
    <section
      class="relative overflow-hidden rounded-[32px] px-6 py-7 text-white transition-colors duration-300 sm:px-8"
      :style="dashboardHeroStyle"
    >
      <div class="absolute inset-y-0 right-0 w-1/2" :style="dashboardHeroGlowStyle" />
      <div class="relative flex flex-col gap-6 lg:flex-row lg:items-end lg:justify-between">
        <div class="max-w-2xl">
          <p class="text-sm font-semibold uppercase tracking-[0.35em] text-white/70">
            {{ $t('dashboard.overview') }}
          </p>
          <h2 class="mt-3 text-3xl font-semibold tracking-tight sm:text-4xl">
            {{ $t('dashboard.overviewTitle') }}
          </h2>
          <p class="mt-3 max-w-xl text-sm leading-7 text-white/80 sm:text-base">
            {{ $t('dashboard.overviewDescription') }}
          </p>
        </div>

        <div class="flex flex-wrap gap-3">
          <button
            type="button"
            class="rounded-2xl border border-white/10 bg-white/10 px-4 py-3 text-left text-white transition hover:bg-white/16 disabled:cursor-not-allowed disabled:opacity-60"
            :disabled="exporting"
            @click="downloadStats"
          >
            <p class="text-xs uppercase tracking-[0.3em] text-white/60">{{ $t('dashboard.export') }}</p>
            <p class="mt-2 flex items-center gap-2 text-sm font-medium text-white">
              <ArrowDownTrayIcon class="h-4 w-4" />
              {{ exporting ? $t('dashboard.exporting') : $t('dashboard.exportCsv') }}
            </p>
          </button>
          <div class="rounded-2xl border border-white/10 bg-white/10 px-4 py-3">
            <p class="text-xs uppercase tracking-[0.3em] text-white/60">{{ $t('dashboard.today') }}</p>
            <p class="mt-2 flex items-center gap-2 text-sm font-medium text-white">
              <CalendarDaysIcon class="h-4 w-4" />
              {{ formatDate(new Date()) }}
            </p>
          </div>
          <div class="rounded-2xl border border-white/10 bg-white/10 px-4 py-3">
            <p class="text-xs uppercase tracking-[0.3em] text-white/60">{{ $t('dashboard.growth') }}</p>
            <p class="mt-2 flex items-center gap-2 text-sm font-medium text-white">
              <ArrowTrendingUpIcon class="h-4 w-4" />
              {{ $t('dashboard.todayOrdersLabel', { count: statsPayload.todayOrders }) }}
            </p>
          </div>
        </div>
      </div>
    </section>

    <section class="grid gap-5 xl:grid-cols-4 md:grid-cols-2">
      <article
        v-for="stat in statDefinitions"
        :key="stat.title"
        class="group relative overflow-hidden rounded-[30px] border border-gray-100/80 bg-white p-6 shadow-[0_16px_40px_rgba(0,0,0,0.02)] transition-all duration-300 hover:-translate-y-1.5 hover:shadow-[0_24px_55px_rgba(var(--brand-primary-ring),0.06)] dark:bg-slate-900/60 dark:border-slate-800/80"
      >
        <!-- Background Ambient Glow -->
        <div
          class="absolute top-0 right-0 -mr-6 -mt-6 h-28 w-28 rounded-full blur-2xl transition-opacity duration-300 opacity-20 group-hover:opacity-45"
          :style="{ background: stat.color }"
        />

        <div class="relative flex items-start justify-between gap-4">
          <div>
            <p class="text-xs font-bold uppercase tracking-wider text-gray-400 dark:text-gray-500">{{ stat.title }}</p>
            <p class="mt-3 text-3xl font-extrabold tracking-tight text-gray-900 transition-colors duration-300 dark:text-white">
              {{ loading ? '...' : stat.value }}
            </p>
          </div>

          <div
            class="flex h-14 w-14 items-center justify-center rounded-2xl transition-all duration-300 group-hover:scale-110"
            :style="{ color: stat.color, backgroundColor: stat.bgColor }"
          >
            <component :is="stat.icon" class="h-6 w-6" style="stroke-width: 2.25px;" />
          </div>
        </div>

        <div class="relative mt-6 flex items-center justify-between gap-3">
          <p class="text-sm font-semibold" :class="stat.changeColor">{{ stat.changeText }}</p>
          <p class="text-xs font-medium text-gray-400 transition-colors duration-300 dark:text-gray-500">{{ stat.caption }}</p>
        </div>
      </article>
    </section>

    <!-- Data Visualization Charts Section -->
    <section class="grid gap-6 lg:grid-cols-3 mt-1">
      <article class="lg:col-span-2 rounded-[30px] border border-gray-100/80 bg-white p-6 shadow-[0_16px_40px_rgba(0,0,0,0.02)] dark:bg-slate-900/60 dark:border-slate-800/80">
        <div class="mb-4">
          <h3 class="text-base font-bold text-gray-900 dark:text-white">营业额与订单趋势</h3>
          <p class="text-xs text-gray-400 dark:text-gray-500 mt-1">展示最近7天的每日营业总额与新订单数量变化趋势</p>
        </div>
        <div class="h-80 w-full">
          <v-chart class="h-full w-full" :option="trendChartOption" autoresize />
        </div>
      </article>

      <article class="rounded-[30px] border border-gray-100/80 bg-white p-6 shadow-[0_16px_40px_rgba(0,0,0,0.02)] dark:bg-slate-900/60 dark:border-slate-800/80">
        <div class="mb-4">
          <h3 class="text-base font-bold text-gray-900 dark:text-white">热门出行目的地占比</h3>
          <p class="text-xs text-gray-400 dark:text-gray-500 mt-1">实时汇总所有预订订单中游客的目的地选择偏好</p>
        </div>
        <div class="h-80 w-full animate-fade-in">
          <v-chart class="h-full w-full" :option="pieChartOption" autoresize />
        </div>
      </article>
    </section>

    <section class="grid gap-6 xl:grid-cols-[minmax(0,1.6fr)_minmax(320px,0.9fr)]">
      <article class="overflow-hidden rounded-[32px] border border-gray-100 bg-white shadow-[0_20px_60px_rgba(0,0,0,0.03)] transition-colors duration-300">
        <div class="flex items-center justify-between border-b border-gray-50 px-6 py-5 transition-colors duration-300">
          <div>
            <h3 class="text-lg font-bold text-gray-900 transition-colors duration-300">{{ $t('dashboard.recentBookings') }}</h3>
            <p class="mt-1 text-sm text-gray-500 transition-colors duration-300">{{ $t('dashboard.recentBookingsSubtitle') }}</p>
          </div>
          <div class="rounded-full bg-brand/10 px-4 py-2 text-sm font-semibold text-brand transition-colors duration-300">
            {{ $t('dashboard.liveUpdates') }}
          </div>
        </div>

        <div v-if="recentBookings.length" class="overflow-x-auto">
          <table class="min-w-full divide-y divide-gray-100 text-left transition-colors duration-300">
            <thead class="bg-gray-50/50 transition-colors duration-300">
              <tr class="text-xs uppercase tracking-[0.24em] text-gray-400 transition-colors duration-300">
                <th class="px-6 py-4 font-bold">{{ $t('dashboard.customerName') }}</th>
                <th class="px-6 py-4 font-bold">{{ $t('dashboard.destination') }}</th>
                <th class="px-6 py-4 font-bold">{{ $t('dashboard.date') }}</th>
                <th class="px-6 py-4 font-bold">{{ $t('dashboard.status') }}</th>
              </tr>
            </thead>
            <tbody class="divide-y divide-gray-50 transition-colors duration-300">
              <tr
                v-for="booking in recentBookings"
                :key="booking.id"
                class="transition-colors duration-300 hover:bg-brand/5"
              >
                <td class="px-6 py-4">
                  <div class="flex items-center gap-3">
                    <div class="flex h-10 w-10 items-center justify-center rounded-2xl bg-brand/10 text-sm font-bold text-brand transition-colors duration-300">
                      {{ booking.customer.slice(0, 2).toUpperCase() }}
                    </div>
                    <span class="font-semibold text-gray-900 transition-colors duration-300">{{ booking.customer }}</span>
                  </div>
                </td>
                <td class="px-6 py-4 text-sm text-gray-900 transition-colors duration-300">{{ booking.destination }}</td>
                <td class="px-6 py-4 text-sm text-gray-500 transition-colors duration-300">{{ booking.date }}</td>
                <td class="px-6 py-4">
                  <span
                    class="inline-flex items-center gap-2 rounded-full px-3 py-1.5 text-xs font-semibold"
                    :class="statusClasses(booking.statusKey)"
                  >
                    <CheckCircleIcon v-if="booking.statusKey === 'dashboard.statusConfirmed' || booking.statusKey === 'dashboard.statusPaid' || booking.statusKey === 'dashboard.statusCompleted'" class="h-4 w-4" />
                    <ClockIcon v-else class="h-4 w-4" />
                    {{ $t(booking.statusKey) }}
                  </span>
                </td>
              </tr>
            </tbody>
          </table>
        </div>

        <div v-else class="px-6 py-12 text-center">
          <p class="text-lg font-semibold text-gray-900 transition-colors duration-300 dark:text-white">{{ $t('dashboard.noBookings') }}</p>
          <p class="mt-2 text-sm text-gray-500 transition-colors duration-300 dark:text-gray-400">{{ $t('dashboard.noBookingsSubtitle') }}</p>
        </div>
      </article>

      <article class="rounded-[32px] border border-gray-100 bg-white p-6 shadow-[0_20px_60px_rgba(0,0,0,0.03)] transition-colors duration-300">
        <div class="flex items-center justify-between">
          <div>
            <h3 class="text-lg font-bold text-gray-900 transition-colors duration-300">{{ $t('dashboard.trendingDestinations') }}</h3>
            <p class="mt-1 text-sm text-gray-500 transition-colors duration-300">{{ $t('dashboard.trendingDestinationsSubtitle') }}</p>
          </div>
          <div class="rounded-full bg-accent/10 px-4 py-2 text-sm font-semibold text-accent transition-colors duration-300">
            {{ $t('dashboard.topThree') }}
          </div>
        </div>

        <div v-if="trendingDestinations.length" class="mt-6 grid gap-4 sm:grid-cols-3 xl:grid-cols-1">
          <div
            v-for="destination in trendingDestinations"
            :key="destination.name"
            class="flex items-center gap-4 rounded-[26px] border border-gray-50 bg-white p-4 transition-all duration-300 hover:-translate-y-1 hover:shadow-[0_18px_44px_rgba(0,0,0,0.05)]"
          >
            <img
              v-if="destination.image"
              :src="destination.image"
              :alt="destination.name"
              class="h-16 w-16 rounded-full object-cover ring-4 ring-white shadow-lg"
            >
              <div
                v-else
                class="flex h-16 w-16 items-center justify-center rounded-full bg-brand/10 text-2xl text-brand ring-4 ring-white shadow-lg"
              >
                鉁堬笍
              </div>
            <div class="min-w-0">
              <p class="text-base font-semibold text-gray-900 transition-colors duration-300 dark:text-white">{{ destination.name }}</p>
              <p class="mt-1 text-sm text-gray-500 transition-colors duration-300 dark:text-gray-400">
                {{ $t('dashboard.bookingsCount', { count: destination.bookings }) }}
              </p>
            </div>
          </div>
        </div>

        <div v-else class="mt-6 rounded-[26px] border border-dashed border-gray-200 px-6 py-10 text-center transition-colors duration-300 dark:border-gray-700">
          <p class="text-lg font-semibold text-gray-900 transition-colors duration-300 dark:text-white">{{ $t('dashboard.noDestinations') }}</p>
          <p class="mt-2 text-sm text-gray-500 transition-colors duration-300 dark:text-gray-400">{{ $t('dashboard.noDestinationsSubtitle') }}</p>
        </div>
      </article>
    </section>
  </div>
</template>

