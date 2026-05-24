<script setup>
import { ref, onMounted } from 'vue'
import http from '@/api/http'
import { ElMessage } from 'element-plus'
import { useOrderStore } from '@/stores/order'
import { formatDateTime, formatMoney } from '@/utils/format'

const loading = ref(false)
const page = ref(1)
const orderStore = useOrderStore()

const statusMap = {
  PENDING: { label: '待确认' },
  CONFIRMED: { label: '已确认' },
  REJECTED: { label: '已拒绝' },
  PAID: { label: '已支付' },
  CANCELLED: { label: '已取消' },
  REFUNDING: { label: '退款中' },
  REFUNDED: { label: '已退款' },
  COMPLETED: { label: '已完成' },
}

function statusTagClass(status) {
  if (status === 'PENDING' || status === 'REFUNDING') return 'theme-tag theme-tag--accent'
  if (status === 'REJECTED' || status === 'CANCELLED') return 'theme-tag theme-tag--danger'
  if (status === 'CONFIRMED' || status === 'PAID' || status === 'COMPLETED' || status === 'REFUNDED') return 'theme-tag theme-tag--success'
  return 'theme-tag theme-tag--muted'
}

async function load() {
  loading.value = true
  try {
    const res = await http.get('/orders', { params: { page: page.value, size: 10 } })
    orderStore.setMyOrders(res.data)
  } catch (error) {
    ElMessage.error(error?.response?.data?.message || '加载失败')
  } finally {
    loading.value = false
  }
}

onMounted(load)
</script>

<template>
  <div class="portal-container">
    <div class="my-orders-header">
      <h2>我的订单</h2>
      <p>查看您的旅行预订记录</p>
    </div>

    <div v-loading="loading">
      <template v-if="orderStore.myOrders.length">
        <div v-for="order in orderStore.myOrders" :key="order.id" class="order-card">
          <div class="order-top">
            <span class="order-no">订单号：{{ order.orderNo }}</span>
            <el-tag :class="statusTagClass(order.status)" effect="light" size="small">{{ statusMap[order.status]?.label }}</el-tag>
          </div>
          <div class="order-body">
            <div class="order-route">{{ order.route?.title || '旅游线路' }}</div>
            <div class="order-meta">
              <span>出行日期：{{ order.travelDate || '-' }}</span>
              <span>人数：{{ order.persons }} 人</span>
              <span>联系人：{{ order.contactName || '-' }}</span>
            </div>
          </div>
          <div v-if="order.status === 'REJECTED' && order.rejectReason" class="order-alert">
            <span class="order-alert__label">拒绝原因</span>
            <span class="order-alert__text">{{ order.rejectReason }}</span>
          </div>
          <div class="order-foot">
            <span class="order-amount">￥{{ formatMoney(order.totalAmount) }}</span>
            <span class="order-time">{{ formatDateTime(order.createdAt) }}</span>
          </div>
        </div>

        <div style="display:flex;justify-content:center;margin-top:24px">
          <el-pagination
            v-model:current-page="page"
            :total="orderStore.total"
            layout="total, prev, pager, next"
            background
            @change="load"
          />
        </div>
      </template>
      <el-empty v-else description="暂无订单记录" />
    </div>
  </div>
</template>

<style scoped>
.portal-container { max-width: 900px; margin: 0 auto; padding: 36px 24px 60px; }
.my-orders-header {
  margin-bottom: 24px;
  padding: 24px 28px;
  border-radius: 24px;
  border: 1px solid var(--border);
  background: linear-gradient(135deg, rgba(var(--brand-soft-rgb), 0.48) 0%, hsl(var(--panel)) 100%);
  box-shadow: 0 14px 34px rgba(var(--brand-dark-rgb), 0.06);
}
.my-orders-header h2 { font-size: 24px; font-weight: 700; margin-bottom: 4px; }
.my-orders-header p { color: var(--text-muted); font-size: 14px; }

.order-card {
  background: linear-gradient(180deg, rgba(var(--brand-soft-rgb), 0.14) 0%, hsl(var(--panel)) 52%);
  border-radius: var(--radius);
  border: 1px solid var(--border);
  margin-bottom: 16px;
  overflow: hidden;
  box-shadow: 0 14px 30px rgba(var(--brand-dark-rgb), 0.05);
  transition: box-shadow 0.2s, transform 0.2s, border-color 0.2s;
  animation: fadeInUp 0.3s ease both;
}

.order-card:hover {
  box-shadow: var(--shadow-lg);
  border-color: rgba(var(--brand-primary-ring), 0.2);
  transform: translateY(-2px);
}

.order-top {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 14px 20px;
  background: linear-gradient(180deg, rgba(var(--brand-soft-rgb), 0.5) 0%, rgba(var(--brand-soft-rgb), 0.18) 100%);
  border-bottom: 1px solid var(--border);
}

.order-no { font-size: 13px; color: var(--text-muted); font-family: monospace; }

.order-body { padding: 16px 20px; }
.order-route { font-size: 17px; font-weight: 700; margin-bottom: 10px; }
.order-meta { display: flex; gap: 20px; font-size: 13px; color: var(--text-muted); flex-wrap: wrap; }

.order-alert {
  display: flex;
  gap: 10px;
  align-items: flex-start;
  padding: 12px 20px;
  background: rgba(var(--accent-rgb), 0.08);
  border-top: 1px solid rgba(var(--accent-rgb), 0.18);
}

.order-alert__label {
  flex: 0 0 auto;
  color: var(--accent);
  font-size: 12px;
  font-weight: 700;
}

.order-alert__text {
  color: hsl(var(--text-80));
  font-size: 13px;
  line-height: 1.6;
}

.order-foot {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 20px;
  border-top: 1px solid var(--border);
}

.order-amount { font-size: 20px; font-weight: 700; color: var(--brand); }
.order-time { font-size: 12px; color: var(--text-muted); }

@keyframes fadeInUp {
  from { opacity: 0; transform: translateY(12px); }
  to { opacity: 1; transform: translateY(0); }
}
</style>
