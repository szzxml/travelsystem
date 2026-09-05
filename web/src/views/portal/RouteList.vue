<script setup>
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { getPublicRoutes } from '@/api/public'

import forestHero from '@/assets/forest-hero.jpg'

const router = useRouter()
const loading = ref(false)
const list = ref([])
const total = ref(0)
const query = reactive({ keyword: '', page: 1, size: 9 })

const fallbackRouteImages = [
  'https://images.unsplash.com/photo-1528127269322-539801943592?auto=format&fit=crop&w=800&q=80',
  'https://images.unsplash.com/photo-1534447677768-be436bb09401?auto=format&fit=crop&w=800&q=80',
  'https://images.unsplash.com/photo-1507525428034-b723cf961d3e?auto=format&fit=crop&w=800&q=80',
  'https://images.unsplash.com/photo-1544644181-1484b3fdfc62?auto=format&fit=crop&w=800&q=80',
  'https://images.unsplash.com/photo-1508804185872-d7badad00f7d?auto=format&fit=crop&w=800&q=80',
  'https://images.unsplash.com/photo-1516483638261-f4dbaf036963?auto=format&fit=crop&w=800&q=80',
]

function getRouteImage(item, index = 0) {
  if (item?.coverImage && String(item.coverImage).trim()) {
    return item.coverImage.trim()
  }
  return fallbackRouteImages[index % fallbackRouteImages.length]
}

function handleImageError(event) {
  if (event?.target) {
    event.target.src = forestHero
  }
}

async function load() {
  loading.value = true
  try {
    const res = await getPublicRoutes(query)
    list.value = res.data.items || []
    total.value = res.data.total || 0
  } finally {
    loading.value = false
  }
}

function search() {
  query.page = 1
  load()
}

onMounted(load)
</script>

<template>
  <div>
    <div class="list-hero">
      <div class="list-hero-inner">
        <h1>精选旅游线路</h1>
        <p>在这里浏览目的地、行程亮点，以及线路关联的推荐酒店。</p>
        <div class="search-bar">
          <el-input
            v-model="query.keyword"
            placeholder="搜索线路名称或目的地"
            size="large"
            clearable
            style="max-width: 440px; flex: 1"
            @keyup.enter="search"
          />
          <el-button type="primary" size="large" @click="search">搜索</el-button>
        </div>
      </div>
    </div>

    <div class="portal-container">
      <div v-loading="loading">
        <div v-if="list.length" class="card-grid">
          <article
            v-for="(item, index) in list"
            :key="item.id"
            class="route-card"
            role="button"
            tabindex="0"
            :aria-label="item.title"
            @click="router.push(`/routes/${item.id}`)"
            @keyup.enter="router.push(`/routes/${item.id}`)"
          >
            <div class="card-cover">
              <img :src="getRouteImage(item, index)" :alt="item.title" @error="handleImageError">
              <div class="card-badge">{{ item.days }} 天</div>
            </div>

            <div class="card-body">
              <div class="card-title">{{ item.title }}</div>
              <div class="card-meta">{{ item.departure }} 至 {{ item.destination }}</div>
              <div v-if="item.hotel" class="card-submeta">
                推荐酒店：{{ item.hotel.name }}<span v-if="item.hotel.city"> · {{ item.hotel.city }}</span>
              </div>
              <div class="card-footer">
                <span class="card-price">&yen;{{ Number(item.price || 0).toLocaleString('zh-CN') }}<small>/人起</small></span>
                <el-button size="small" type="primary">查看详情</el-button>
              </div>
            </div>
          </article>
        </div>

        <el-empty v-else description="暂无旅游线路" />
      </div>

      <div style="display: flex; justify-content: center; margin-top: 32px">
        <el-pagination
          v-model:current-page="query.page"
          v-model:page-size="query.size"
          :total="total"
          layout="total, prev, pager, next"
          background
          @change="load"
        />
      </div>
    </div>
  </div>
</template>

<style scoped>
.list-hero {
  background: linear-gradient(135deg, rgba(var(--brand-dark-rgb), 0.98) 0%, var(--brand) 100%);
  padding: 44px 24px 48px;
  border-radius: 24px;
  border: 1px solid rgba(255, 255, 255, 0.12);
  box-shadow: 0 20px 48px rgba(var(--brand-dark-rgb), 0.18);
}

.list-hero-inner {
  max-width: 1200px;
  margin: 0 auto;
  color: #fff;
}

.list-hero h1 {
  font-size: 32px;
  margin-bottom: 8px;
}

.list-hero p {
  color: rgba(255, 255, 255, 0.78);
  margin-bottom: 20px;
  max-width: 42rem;
}

.search-bar {
  display: flex;
  gap: 10px;
}

.search-bar :deep(.el-input__wrapper) {
  background: #ffffff !important;
  border: 1px solid rgba(255, 255, 255, 0.2) !important;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.08) !important;
}

.search-bar :deep(.el-input__inner) {
  color: #1f2937 !important;
}

.search-bar :deep(.el-input__inner::placeholder) {
  color: #9ca3af !important;
}

.portal-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 28px 24px 60px;
}

.card-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 24px;
}

.route-card {
  background: linear-gradient(180deg, rgba(var(--brand-soft-rgb), 0.12) 0%, hsl(var(--panel)) 52%);
  border-radius: var(--radius);
  border: 1px solid var(--border);
  overflow: hidden;
  cursor: pointer;
  box-shadow: 0 14px 32px rgba(var(--brand-dark-rgb), 0.06);
  transition: transform 0.2s, box-shadow 0.2s, border-color 0.2s;
  animation: fadeInUp 0.4s ease both;
}

.route-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-lg);
  border-color: rgba(var(--brand-primary-ring), 0.24);
}

.card-cover {
  height: 220px;
  background: linear-gradient(180deg, rgba(var(--brand-soft-rgb), 0.42) 0%, rgba(var(--accent-rgb), 0.08) 100%);
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}

.card-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  position: absolute;
  inset: 0;
}

.card-cover-placeholder {
  font-size: 24px;
  opacity: 0.5;
}

.card-badge {
  position: absolute;
  top: 12px;
  right: 12px;
  background: var(--accent);
  color: #fff;
  padding: 4px 12px;
  border-radius: 999px;
  font-size: 12px;
  font-weight: 700;
  box-shadow: 0 6px 16px rgba(var(--accent-rgb), 0.22);
}

.card-body {
  padding: 18px;
}

.card-title {
  font-size: 16px;
  font-weight: 700;
  margin-bottom: 8px;
}

.card-meta {
  font-size: 13px;
  color: var(--text-muted);
  margin-bottom: 10px;
}

.card-submeta {
  font-size: 13px;
  color: var(--brand-dark);
  margin-bottom: 14px;
}

.card-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.card-price {
  color: var(--brand);
  font-size: 20px;
  font-weight: 700;
}

.card-price small {
  font-size: 12px;
  font-weight: 400;
  color: var(--text-muted);
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(16px);
  }

  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>
