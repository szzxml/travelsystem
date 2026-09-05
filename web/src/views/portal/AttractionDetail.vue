<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getPublicAttraction } from '@/api/public'
import { formatMoney } from '@/utils/format'
import forestHero from '@/assets/forest-hero.jpg'

const route = useRoute()
const router = useRouter()
const detail = ref(null)
const loading = ref(true)

const fallbackAttractionImage = 'https://images.unsplash.com/photo-1548013146-72479768bada?auto=format&fit=crop&w=1200&q=80'

function getDetailCover(item) {
  if (item?.coverImage && String(item.coverImage).trim()) {
    return item.coverImage.trim()
  }
  return fallbackAttractionImage
}

function handleImageError(event) {
  if (event?.target) {
    event.target.src = forestHero
  }
}

const statusType = computed(() => {
  if (detail.value?.status === 'OPEN') return 'success'
  if (detail.value?.status === 'MAINTENANCE') return 'warning'
  return 'danger'
})

const statusLabel = computed(() => {
  if (detail.value?.status === 'OPEN') return '开放中'
  if (detail.value?.status === 'MAINTENANCE') return '维护中'
  if (detail.value?.status === 'CLOSED') return '已关闭'
  return '信息更新中'
})

const ticketPriceLabel = computed(() => {
  const amount = Number(detail.value?.ticketPrice)
  if (!Number.isFinite(amount) || amount <= 0) return '免费开放'
  return `¥${formatMoney(amount)}`
})

onMounted(async () => {
  try {
    const res = await getPublicAttraction(route.params.id)
    detail.value = res.data
  } catch {
    ElMessage.error('景点信息不存在')
  } finally {
    loading.value = false
  }
})
</script>

<template>
  <div class="portal-container" v-loading="loading">
    <template v-if="detail">
      <el-breadcrumb separator="/" class="detail-breadcrumb">
        <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item :to="{ path: '/attractions' }">景点信息</el-breadcrumb-item>
        <el-breadcrumb-item>{{ detail.name }}</el-breadcrumb-item>
      </el-breadcrumb>

      <div class="detail-layout">
        <div class="detail-main">
          <div class="detail-cover">
            <img :src="getDetailCover(detail)" :alt="detail.name" @error="handleImageError">
          </div>

          <el-card class="detail-card" shadow="never">
            <div class="detail-head">
              <div>
                <h1 class="detail-title">{{ detail.name }}</h1>
                <p class="detail-location">{{ detail.location || '位置待补充' }}</p>
              </div>
              <el-tag :type="statusType" effect="plain">{{ statusLabel }}</el-tag>
            </div>

            <div class="detail-tags">
              <el-tag v-if="detail.openTime" effect="plain">开放时间 {{ detail.openTime }}</el-tag>
              <el-tag v-if="detail.capacity" effect="plain" type="info">日容量 {{ detail.capacity }}</el-tag>
              <el-tag effect="plain" type="success">{{ ticketPriceLabel }}</el-tag>
            </div>

            <div class="detail-desc">
              {{ detail.description || '当前景点暂无详细介绍，可先查看开放时间、位置和门票参考信息。' }}
            </div>
          </el-card>
        </div>

        <div class="detail-side">
          <el-card class="info-card" shadow="never">
            <div class="info-title">景点信息</div>

            <div class="info-list">
              <div class="info-row">
                <span class="info-label">门票价格</span>
                <span class="info-val info-val--brand">{{ ticketPriceLabel }}</span>
              </div>
              <div class="info-row">
                <span class="info-label">当前状态</span>
                <span class="info-val">{{ statusLabel }}</span>
              </div>
              <div class="info-row" v-if="detail.openTime">
                <span class="info-label">开放时间</span>
                <span class="info-val">{{ detail.openTime }}</span>
              </div>
              <div class="info-row" v-if="detail.capacity">
                <span class="info-label">日容量</span>
                <span class="info-val">{{ detail.capacity }}</span>
              </div>
              <div class="info-row" v-if="detail.location">
                <span class="info-label">所在区域</span>
                <span class="info-val">{{ detail.location }}</span>
              </div>
            </div>

            <div class="info-actions">
              <el-button type="primary" size="large" @click="router.push('/attractions')">返回列表</el-button>
              <el-button size="large" @click="router.push('/')">返回首页</el-button>
            </div>
          </el-card>
        </div>
      </div>
    </template>

    <el-empty v-else-if="!loading" description="景点信息不存在" />
  </div>
</template>

<style scoped>
.portal-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 28px 24px 60px;
}

.detail-breadcrumb {
  margin-bottom: 20px;
}

.detail-layout {
  display: grid;
  grid-template-columns: 1fr 320px;
  gap: 24px;
}

.detail-cover {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 400px;
  overflow: hidden;
  border: 1px solid var(--border);
  border-radius: var(--radius);
  background: linear-gradient(180deg, rgba(var(--brand-soft-rgb), 0.38) 0%, rgba(var(--accent-rgb), 0.12) 100%);
}

.detail-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.cover-placeholder {
  padding: 0 24px;
  color: hsl(var(--text-80));
  font-size: 28px;
  font-weight: 700;
  text-align: center;
  opacity: 0.72;
}

.detail-card {
  margin-top: 20px;
}

.detail-head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
}

.detail-title {
  margin-bottom: 8px;
  color: hsl(var(--text-100));
  font-size: 28px;
  font-weight: 800;
}

.detail-location {
  color: var(--text-muted);
  font-size: 14px;
}

.detail-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin: 18px 0 0;
}

.detail-tags :deep(.el-tag) {
  --el-tag-bg-color: rgba(var(--brand-dark-rgb), 0.08);
  --el-tag-border-color: rgba(var(--brand-dark-rgb), 0.14);
  --el-tag-text-color: hsl(var(--text-80));
  font-weight: 700;
}

.detail-tags :deep(.el-tag.el-tag--info) {
  --el-tag-bg-color: rgba(var(--accent-rgb), 0.12);
  --el-tag-border-color: rgba(var(--accent-rgb), 0.22);
  --el-tag-text-color: #9c6b1e;
}

.detail-tags :deep(.el-tag.el-tag--success) {
  --el-tag-bg-color: rgba(var(--brand-primary-ring), 0.12);
  --el-tag-border-color: rgba(var(--brand-primary-ring), 0.22);
  --el-tag-text-color: var(--brand-dark);
}

.detail-desc {
  margin-top: 18px;
  color: var(--text-muted);
  font-size: 15px;
  line-height: 1.85;
}

.info-card {
  position: sticky;
  top: 80px;
}

.info-title {
  margin-bottom: 16px;
  color: hsl(var(--text-100));
  font-size: 16px;
  font-weight: 700;
}

.info-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.info-row {
  display: flex;
  justify-content: space-between;
  gap: 14px;
  padding-bottom: 12px;
  border-bottom: 1px solid var(--border);
  font-size: 14px;
}

.info-row:last-child {
  border-bottom: none;
}

.info-label {
  color: var(--text-muted);
}

.info-val {
  color: hsl(var(--text-100));
  font-weight: 600;
  text-align: right;
}

.info-val--brand {
  color: var(--brand);
  font-size: 18px;
  font-weight: 800;
}

.info-actions {
  display: flex;
  flex-direction: column;
  gap: 10px;
  margin-top: 18px;
}

.info-actions :deep(.el-button) {
  width: 100%;
}

@media (max-width: 860px) {
  .detail-layout {
    grid-template-columns: 1fr;
  }

  .detail-head {
    flex-direction: column;
  }
}
</style>
