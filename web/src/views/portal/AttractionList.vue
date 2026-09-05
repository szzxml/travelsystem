<script setup>
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { getPublicAttractions } from '@/api/public'
import { formatMoney } from '@/utils/format'
import forestHero from '@/assets/forest-hero.jpg'

const router = useRouter()
const loading = ref(false)
const list = ref([])
const total = ref(0)
const query = reactive({ keyword: '', page: 1, size: 9 })

const fallbackAttractionImages = [
  'https://images.unsplash.com/photo-1464822759023-fed622ff2c3b?auto=format&fit=crop&w=800&q=80',
  'https://images.unsplash.com/photo-1548013146-72479768bada?auto=format&fit=crop&w=800&q=80',
  'https://images.unsplash.com/photo-1477959858617-67f30bc75b82?auto=format&fit=crop&w=800&q=80',
  'https://images.unsplash.com/photo-1441974231531-c6227db76b6e?auto=format&fit=crop&w=800&q=80',
  'https://images.unsplash.com/photo-1501785888041-af3ef285b470?auto=format&fit=crop&w=800&q=80',
  'https://images.unsplash.com/photo-1513836279014-a89f7a76ae86?auto=format&fit=crop&w=800&q=80',
]

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

async function load() {
  loading.value = true
  try {
    const res = await getPublicAttractions(query)
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

function openDetail(id) {
  router.push(`/attractions/${id}`)
}

function getStatusLabel(status) {
  if (status === 'OPEN') return '开放中'
  if (status === 'MAINTENANCE') return '维护中'
  if (status === 'CLOSED') return '已关闭'
  return '信息更新中'
}

function getStatusClass(status) {
  if (status === 'OPEN') return 'card-status--open'
  if (status === 'MAINTENANCE') return 'card-status--maintenance'
  return 'card-status--closed'
}

function formatTicketPrice(value) {
  const amount = Number(value)
  if (!Number.isFinite(amount) || amount <= 0) return '免费开放'
  return `¥${formatMoney(amount)}`
}

function excerpt(text, maxLength = 66) {
  if (!text) return '提供景点简介、开放时间与门票参考信息。'
  const normalized = String(text).replace(/\s+/g, ' ').trim()
  return normalized.length > maxLength ? `${normalized.slice(0, maxLength)}...` : normalized
}

onMounted(load)
</script>

<template>
  <div>
    <section class="list-hero">
      <div class="list-hero-inner">
        <h1>景点信息</h1>
        <p>集中浏览景点位置、开放时间和门票参考，为路线选择和出行安排提供统一入口。</p>
        <div class="search-bar">
          <el-input
            v-model="query.keyword"
            placeholder="搜索景点名称或区域"
            size="large"
            clearable
            style="max-width: 440px; flex: 1"
            @keyup.enter="search"
          />
          <el-button type="primary" size="large" @click="search">搜索</el-button>
        </div>
      </div>
    </section>

    <div class="portal-container">
      <div v-loading="loading">
        <div v-if="list.length" class="card-grid">
          <article
            v-for="(item, index) in list"
            :key="item.id"
            class="attraction-card"
            role="button"
            tabindex="0"
            :aria-label="item.name"
            @click="openDetail(item.id)"
            @keyup.enter="openDetail(item.id)"
          >
            <div class="card-cover">
              <img :src="getAttractionImage(item, index)" :alt="item.name" @error="handleImageError">
              <span class="card-status" :class="getStatusClass(item.status)">
                {{ getStatusLabel(item.status) }}
              </span>
            </div>

            <div class="card-body">
              <div class="card-title-row">
                <h2 class="card-title">{{ item.name }}</h2>
                <span v-if="item.openTime" class="card-chip">{{ item.openTime }}</span>
              </div>
              <p class="card-meta">{{ item.location || '位置待补充' }}</p>
              <p class="card-description">{{ excerpt(item.description) }}</p>

              <div class="card-footer">
                <div class="card-price-block">
                  <span class="card-price">{{ formatTicketPrice(item.ticketPrice) }}</span>
                  <small>{{ item.capacity ? `日容量 ${item.capacity}` : '支持查看详细信息' }}</small>
                </div>
                <el-button size="small" type="primary">查看详情</el-button>
              </div>
            </div>
          </article>
        </div>

        <el-empty v-else description="暂无景点信息" />
      </div>

      <div class="pagination-wrap">
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
  max-width: 42rem;
  margin-bottom: 20px;
  color: rgba(255, 255, 255, 0.78);
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

.attraction-card {
  overflow: hidden;
  border: 1px solid var(--border);
  border-radius: var(--radius);
  background: linear-gradient(180deg, rgba(var(--brand-soft-rgb), 0.12) 0%, hsl(var(--panel)) 52%);
  box-shadow: 0 14px 32px rgba(var(--brand-dark-rgb), 0.06);
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s, border-color 0.2s;
  animation: fadeInUp 0.4s ease both;
}

.attraction-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-lg);
  border-color: rgba(var(--brand-primary-ring), 0.24);
}

.card-cover {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  height: 220px;
  overflow: hidden;
  background: linear-gradient(180deg, rgba(var(--brand-soft-rgb), 0.42) 0%, rgba(var(--accent-rgb), 0.08) 100%);
}

.card-cover img {
  position: absolute;
  inset: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.card-cover-placeholder {
  padding: 0 24px;
  color: hsl(var(--text-80));
  font-size: 22px;
  font-weight: 700;
  text-align: center;
  opacity: 0.72;
}

.card-status {
  position: absolute;
  top: 12px;
  left: 12px;
  padding: 4px 12px;
  border-radius: 999px;
  font-size: 12px;
  font-weight: 700;
  backdrop-filter: blur(2px);
}

.card-status--open {
  background: rgba(31, 138, 112, 0.12);
  color: #1f8a70;
}

.card-status--maintenance {
  background: rgba(var(--accent-rgb), 0.14);
  color: #9c6b1e;
}

.card-status--closed {
  background: rgba(217, 72, 95, 0.12);
  color: #d9485f;
}

.card-body {
  padding: 18px;
}

.card-title-row {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 10px;
  margin-bottom: 8px;
}

.card-title {
  font-size: 18px;
  font-weight: 700;
  color: hsl(var(--text-100));
}

.card-chip {
  flex: 0 0 auto;
  padding: 4px 10px;
  border-radius: 999px;
  background: rgba(var(--brand-primary-ring), 0.08);
  color: var(--brand);
  font-size: 12px;
  font-weight: 700;
}

.card-meta {
  margin-bottom: 10px;
  color: var(--text-muted);
  font-size: 13px;
}

.card-description {
  min-height: 44px;
  margin-bottom: 16px;
  color: hsl(var(--text-80));
  font-size: 14px;
  line-height: 1.7;
}

.card-footer {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 16px;
}

.card-price-block {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.card-price {
  color: var(--brand);
  font-size: 20px;
  font-weight: 700;
}

.card-price-block small {
  color: var(--text-muted);
  font-size: 12px;
}

.pagination-wrap {
  display: flex;
  justify-content: center;
  margin-top: 32px;
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

@media (max-width: 768px) {
  .search-bar,
  .card-footer {
    flex-direction: column;
  }

  .card-footer :deep(.el-button) {
    width: 100%;
  }
}
</style>
