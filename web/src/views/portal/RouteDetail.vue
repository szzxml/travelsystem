<script setup>
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getPublicRoute } from '@/api/public'
import { createOrder } from '@/api/orders'
import { useUserStore } from '@/stores/user'
import { useOrderStore } from '@/stores/order'
import { formatMoney } from '@/utils/format'
import BookingForm from '@/components/BookingForm.vue'
import forestHero from '@/assets/forest-hero.jpg'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const orderStore = useOrderStore()
const detail = ref(null)
const loading = ref(true)
const bookingVisible = ref(false)
const bookingLoading = ref(false)

const fallbackRouteImage = 'https://images.unsplash.com/photo-1534447677768-be436bb09401?auto=format&fit=crop&w=1200&q=80'

function getDetailCover(item) {
  if (item?.coverImage && String(item.coverImage).trim()) {
    return item.coverImage.trim()
  }
  return fallbackRouteImage
}

function handleImageError(event) {
  if (event?.target) {
    event.target.src = forestHero
  }
}

function formatHotelStar(value) {
  return value ? `${value} 星级` : '暂无评级'
}

onMounted(async () => {
  try {
    const res = await getPublicRoute(route.params.id)
    detail.value = res.data
  } catch {
    ElMessage.error('线路不存在')
  } finally {
    loading.value = false
  }
})

function handleBook() {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录后再预订')
    router.push({ path: '/login', query: { redirect: route.fullPath } })
    return
  }

  bookingVisible.value = true
}

async function submitBooking(form) {
  bookingLoading.value = true

  try {
    const res = await createOrder({
      routeId: detail.value.id,
      persons: form.persons,
      travelDate: form.travelDate,
      contactName: form.contactName,
      contactPhone: form.contactPhone,
      remark: form.remark,
    })

    orderStore.setLatestOrder(res.data)
    ElMessage.success('预订提交成功')
    bookingVisible.value = false
    router.push('/my-orders')
  } catch (error) {
    ElMessage.error(error?.response?.data?.message || '预订失败，请稍后重试')
  } finally {
    bookingLoading.value = false
  }
}
</script>

<template>
  <div class="portal-container" v-loading="loading">
    <template v-if="detail">
      <el-breadcrumb separator="/" style="margin-bottom: 20px">
        <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item :to="{ path: '/routes' }">旅游线路</el-breadcrumb-item>
        <el-breadcrumb-item>{{ detail.title }}</el-breadcrumb-item>
      </el-breadcrumb>

      <div class="detail-layout">
        <div class="detail-main">
          <div class="detail-cover">
            <img :src="getDetailCover(detail)" :alt="detail.title" @error="handleImageError">
          </div>

          <el-card style="margin-top: 20px">
            <h2 class="detail-title">{{ detail.title }}</h2>

            <div class="detail-tags">
              <el-tag>{{ detail.days }} 天行程</el-tag>
              <el-tag type="success">{{ detail.departure }} 至 {{ detail.destination }}</el-tag>
              <el-tag v-if="detail.maxGroupSize" type="info">最多 {{ detail.maxGroupSize }} 人</el-tag>
            </div>

            <div class="detail-desc">{{ detail.description || '暂未填写线路介绍。' }}</div>

            <div v-if="detail.hotel" class="detail-hotel">
              <h3 class="detail-hotel__title">推荐酒店</h3>
              <div class="detail-hotel__grid">
                <div><span class="info-label">酒店名称</span><span>{{ detail.hotel.name }}</span></div>
                <div><span class="info-label">所在城市</span><span>{{ detail.hotel.city || '-' }}</span></div>
                <div><span class="info-label">酒店星级</span><span>{{ formatHotelStar(detail.hotel.starLevel) }}</span></div>
                <div><span class="info-label">联系电话</span><span>{{ detail.hotel.phone || '-' }}</span></div>
                <div class="detail-hotel__full"><span class="info-label">酒店地址</span><span>{{ detail.hotel.address || '-' }}</span></div>
                <div v-if="detail.hotel.description" class="detail-hotel__full"><span class="info-label">酒店简介</span><span>{{ detail.hotel.description }}</span></div>
              </div>
            </div>
          </el-card>
        </div>

        <div class="detail-side">
          <el-card class="book-card">
            <div class="book-price">&yen;{{ formatMoney(detail.price) }}<small>/人起</small></div>

            <div class="book-info">
              <div><span class="info-label">出发地</span><span>{{ detail.departure }}</span></div>
              <div><span class="info-label">目的地</span><span>{{ detail.destination }}</span></div>
              <div><span class="info-label">行程天数</span><span>{{ detail.days }} 天</span></div>
              <div v-if="detail.hotel"><span class="info-label">推荐酒店</span><span>{{ detail.hotel.name }}</span></div>
              <div v-if="detail.maxGroupSize"><span class="info-label">人数上限</span><span>{{ detail.maxGroupSize }} 人</span></div>
            </div>

            <el-button type="primary" size="large" style="width: 100%; margin-top: 16px" @click="handleBook">立即预订</el-button>
            <el-button size="large" style="width: 100%; margin-top: 10px" @click="router.push('/routes')">返回列表</el-button>
          </el-card>
        </div>
      </div>
    </template>

    <el-empty v-else-if="!loading" description="线路不存在" />

    <el-dialog v-model="bookingVisible" title="填写预订信息" width="560px" destroy-on-close>
      <BookingForm
        v-if="detail"
        :route="detail"
        :loading="bookingLoading"
        @submit="submitBooking"
        @cancel="bookingVisible = false"
      />
    </el-dialog>
  </div>
</template>

<style scoped>
.portal-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 36px 24px 60px;
}

.detail-layout {
  display: grid;
  grid-template-columns: 1fr 320px;
  gap: 24px;
}

@media (max-width: 860px) {
  .detail-layout {
    grid-template-columns: 1fr;
  }
}

.detail-cover {
  height: 400px;
  border-radius: var(--radius);
  overflow: hidden;
  background: linear-gradient(180deg, rgba(var(--brand-soft-rgb), 0.38) 0%, rgba(var(--accent-rgb), 0.12) 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px solid var(--border);
}

.detail-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.cover-placeholder {
  font-size: 24px;
  opacity: 0.5;
}

.detail-title {
  font-size: 24px;
  font-weight: 700;
  margin-bottom: 12px;
}

.detail-tags {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  margin-bottom: 16px;
}

.detail-tags :deep(.el-tag) {
  --el-tag-bg-color: rgba(var(--brand-dark-rgb), 0.08);
  --el-tag-border-color: rgba(var(--brand-dark-rgb), 0.14);
  --el-tag-text-color: hsl(var(--text-80));
  font-weight: 700;
}

.detail-tags :deep(.el-tag.el-tag--success) {
  --el-tag-bg-color: rgba(var(--brand-primary-ring), 0.12);
  --el-tag-border-color: rgba(var(--brand-primary-ring), 0.22);
  --el-tag-text-color: var(--brand-dark);
}

.detail-tags :deep(.el-tag.el-tag--info) {
  --el-tag-bg-color: rgba(var(--accent-rgb), 0.12);
  --el-tag-border-color: rgba(var(--accent-rgb), 0.22);
  --el-tag-text-color: var(--accent);
}

.detail-desc {
  color: var(--text-muted);
  line-height: 1.8;
  font-size: 15px;
  margin-top: 16px;
}

.detail-hotel {
  margin-top: 24px;
  padding-top: 20px;
  border-top: 1px solid var(--border);
}

.detail-hotel__title {
  font-size: 18px;
  font-weight: 700;
  margin-bottom: 12px;
}

.detail-hotel__grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px 18px;
}

.detail-hotel__grid div {
  display: flex;
  flex-direction: column;
  gap: 4px;
  font-size: 14px;
}

.detail-hotel__full {
  grid-column: 1 / -1;
}

.book-card {
  position: sticky;
  top: 80px;
}

.book-price {
  font-size: 32px;
  font-weight: 800;
  color: var(--brand);
  margin-bottom: 16px;
}

.book-price small {
  font-size: 14px;
  font-weight: 400;
  color: var(--text-muted);
}

.book-info {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.book-info div {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  font-size: 14px;
}

.info-label {
  color: var(--text-muted);
}

@media (max-width: 860px) {
  .detail-hotel__grid {
    grid-template-columns: 1fr;
  }
}
</style>
