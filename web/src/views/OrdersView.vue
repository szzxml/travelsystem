<script setup>
import { reactive, ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getOrders, updateOrderStatus, deleteOrder } from '@/api/orders'
import AdminPageShell from '@/components/admin/AdminPageShell.vue'
import { usePaginatedList } from '@/composables/usePaginatedList'

const query = reactive({ keyword: '', status: '', page: 1, size: 10 })
const route = useRoute()
const { loading, list, total, load, search } = usePaginatedList({
  query,
  fetcher: getOrders,
  errorMessage: '订单数据加载失败',
})

const statusMap = {
  PENDING: { label: '待确认', type: 'warning' },
  CONFIRMED: { label: '已确认', type: 'info' },
  REJECTED: { label: '已拒绝', type: 'danger' },
  PAID: { label: '已支付', type: 'success' },
  CANCELLED: { label: '已取消', type: 'danger' },
  REFUNDING: { label: '退款中', type: 'warning' },
  REFUNDED: { label: '已退款', type: 'info' },
  COMPLETED: { label: '已完成', type: 'success' },
}

const nextStatus = {
  PENDING: 'CONFIRMED',
  CONFIRMED: 'PAID',
  PAID: 'COMPLETED',
  REFUNDING: 'REFUNDED',
}

function statusTagClass(status) {
  if (status === 'PENDING' || status === 'REFUNDING') return 'theme-tag theme-tag--accent'
  if (status === 'REJECTED' || status === 'CANCELLED') return 'theme-tag theme-tag--danger'
  if (status === 'CONFIRMED' || status === 'PAID' || status === 'COMPLETED' || status === 'REFUNDED') return 'theme-tag theme-tag--success'
  return 'theme-tag theme-tag--muted'
}

function formatMoney(value) {
  const num = Number(value || 0)
  return Number.isFinite(num) ? num.toLocaleString('zh-CN') : '0'
}

async function advance(row) {
  const next = nextStatus[row.status]
  if (!next) return
  try {
    await updateOrderStatus(row.id, next)
    ElMessage.success(`订单已更新为${statusMap[next]?.label || next}`)
    load()
  } catch (error) {
    ElMessage.error(error?.response?.data?.message || '状态更新失败')
  }
}

async function cancelOrder(row) {
  await ElMessageBox.confirm(`确定取消订单 ${row.orderNo} ？`, '确认取消', { type: 'warning' })
  try {
    await updateOrderStatus(row.id, 'CANCELLED')
    ElMessage.success('订单已取消')
    load()
  } catch (error) {
    ElMessage.error(error?.response?.data?.message || '取消失败')
  }
}

async function rejectOrder(row) {
  let reason = ''

  try {
    const result = await ElMessageBox.prompt('请输入拒绝原因，系统将记录到订单状态中。', '拒绝订单', {
      confirmButtonText: '确认拒绝',
      cancelButtonText: '取消',
      inputType: 'textarea',
      inputPlaceholder: '例如：联系人信息不完整、线路已停止接待',
      inputPattern: /\S+/,
      inputErrorMessage: '请输入拒绝原因',
    })
    reason = result.value
  } catch {
    return
  }

  try {
    await updateOrderStatus(row.id, 'REJECTED', reason)
    ElMessage.success('订单已拒绝')
    load()
  } catch (error) {
    ElMessage.error(error?.response?.data?.message || '拒绝失败')
  }
}

async function removeOrder(row) {
  await ElMessageBox.confirm(`确定删除订单 ${row.orderNo} ？`, '确认删除', { type: 'warning' })
  try {
    await deleteOrder(row.id)
    ElMessage.success('删除成功')
    load()
  } catch {
    ElMessage.error('删除失败')
  }
}

watch(
  () => route.query.keyword,
  (keyword) => {
    query.keyword = typeof keyword === 'string' ? keyword : ''
    search()
  },
  { immediate: true }
)
</script>

<template>
  <AdminPageShell
    title="订单管理"
    subtitle="处理订单流转、取消、退款及历史记录"
    :summary="`当前共 ${total} 条订单`"
  >
    <template #filters>
        <el-input
          v-model="query.keyword"
          placeholder="搜索订单号"
          clearable
          style="width: 220px"
          @keyup.enter="search"
        />
        <el-select v-model="query.status" clearable placeholder="全部状态" style="width: 160px" @change="search">
          <el-option v-for="(item, key) in statusMap" :key="key" :label="item.label" :value="key" />
        </el-select>
        <el-button type="primary" round @click="search">
          搜索
        </el-button>
    </template>

    <section v-loading="loading">
      <el-table :data="list" class="admin-liquid-table" stripe style="width: 100%">
        <el-table-column prop="orderNo" label="订单号" width="190" />
        <el-table-column label="线路" min-width="150">
          <template #default="{ row }">{{ row.route?.title || '-' }}</template>
        </el-table-column>
        <el-table-column label="联系人" width="130">
          <template #default="{ row }">{{ row.contactName || row.user?.username || '-' }}</template>
        </el-table-column>
        <el-table-column prop="persons" label="人数" width="80" align="center" />
        <el-table-column prop="totalAmount" label="金额" width="130" align="right">
          <template #default="{ row }">¥{{ formatMoney(row.totalAmount) }}</template>
        </el-table-column>
        <el-table-column prop="travelDate" label="出行日期" width="130" />
        <el-table-column prop="status" label="状态" width="110" align="center">
          <template #default="{ row }">
            <div class="flex flex-col items-center">
              <el-tag :class="statusTagClass(row.status)" effect="light" size="small">{{ statusMap[row.status]?.label || row.status }}</el-tag>
              <span v-if="row.status === 'REJECTED' && row.rejectReason" class="mt-1 text-[11px] leading-4 text-slate-500">
                {{ row.rejectReason }}
              </span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="320" align="center" fixed="right">
          <template #default="{ row }">
            <el-button v-if="nextStatus[row.status]" size="small" type="primary" @click="advance(row)">
              流转至{{ statusMap[nextStatus[row.status]]?.label }}
            </el-button>
            <el-button v-if="row.status === 'PENDING'" size="small" type="danger" plain @click="rejectOrder(row)">
              拒绝
            </el-button>
            <el-button v-if="['PENDING', 'CONFIRMED'].includes(row.status)" size="small" type="warning" @click="cancelOrder(row)">
              取消
            </el-button>
            <el-button size="small" type="danger" @click="removeOrder(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="mt-4 flex justify-end">
        <el-pagination
          v-model:current-page="query.page"
          v-model:page-size="query.size"
          :total="total"
          layout="total, prev, pager, next"
          background
          @change="load"
        />
      </div>
    </section>
  </AdminPageShell>
</template>

<style scoped>
.theme-tag {
  letter-spacing: 0.01em;
}
</style>
