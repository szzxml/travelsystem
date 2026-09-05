<script setup>
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { createRoute, deleteRoute, getRoutes, updateRoute } from '@/api/routes'
import { getHotels } from '@/api/hotels'
import AdminPageShell from '@/components/admin/AdminPageShell.vue'
import { usePaginatedList } from '@/composables/usePaginatedList'

const route = useRoute()

const query = reactive({
  keyword: '',
  status: '',
  page: 1,
  size: 10,
})
const { loading, list, total, load, search } = usePaginatedList({
  query,
  fetcher: getRoutes,
  errorMessage: '线路数据加载失败',
})

const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref()
const editId = ref(null)
const hotelOptions = ref([])

const form = reactive({
  title: '',
  description: '',
  coverImage: '',
  days: 1,
  price: '',
  maxGroupSize: '',
  departure: '',
  destination: '',
  hotelId: null,
  status: 'PUBLISHED',
})

const rules = {
  title: [{ required: true, message: '请输入线路名称', trigger: 'blur' }],
  days: [{ required: true, message: '请输入天数', trigger: 'change' }],
  price: [{ required: true, message: '请输入价格', trigger: 'blur' }],
}

const statusOptions = [
  { value: '', label: '全部状态' },
  { value: 'DRAFT', label: '草稿' },
  { value: 'PUBLISHED', label: '已发布' },
  { value: 'OFFLINE', label: '已下线' },
]

const statusMap = {
  DRAFT: { label: '草稿', type: 'info' },
  PUBLISHED: { label: '已发布', type: 'success' },
  OFFLINE: { label: '已下线', type: 'warning' },
}

const publishedCount = computed(() => list.value.filter((item) => item.status === 'PUBLISHED').length)

function statusTagClass(status) {
  if (status === 'PUBLISHED') return 'theme-tag theme-tag--success'
  if (status === 'OFFLINE') return 'theme-tag theme-tag--accent'
  return 'theme-tag theme-tag--muted'
}

function resetForm() {
  Object.assign(form, {
    title: '',
    description: '',
    coverImage: '',
    days: 1,
    price: '',
    maxGroupSize: '',
    departure: '',
    destination: '',
    hotelId: null,
    status: 'PUBLISHED',
  })
}

function formatMoney(value) {
  const num = Number(value || 0)
  return Number.isFinite(num) ? num.toLocaleString('zh-CN') : '0'
}

function hotelLabel(hotel) {
  if (!hotel) return '-'
  return hotel.city ? `${hotel.name} · ${hotel.city}` : hotel.name
}

async function loadHotelOptions() {
  try {
    const res = await getHotels({ page: 1, size: 200 })
    hotelOptions.value = res.data.items || []
  } catch (error) {
    ElMessage.error(error?.response?.data?.message || '酒店选项加载失败')
  }
}

function openCreate() {
  isEdit.value = false
  editId.value = null
  resetForm()
  dialogVisible.value = true
}

function openEdit(row) {
  isEdit.value = true
  editId.value = row.id
  Object.assign(form, {
    title: row.title || '',
    description: row.description || '',
    coverImage: row.coverImage || '',
    days: row.days || 1,
    price: String(row.price ?? ''),
    maxGroupSize: row.maxGroupSize != null ? String(row.maxGroupSize) : '',
    departure: row.departure || '',
    destination: row.destination || '',
    hotelId: row.hotel?.id ?? null,
    status: row.status || 'PUBLISHED',
  })
  dialogVisible.value = true
}

async function submit() {
  await formRef.value.validate()

  const payload = {
    ...form,
    days: Number(form.days),
    price: Number(form.price),
    maxGroupSize: form.maxGroupSize ? Number(form.maxGroupSize) : null,
    hotelId: form.hotelId || null,
  }

  try {
    if (isEdit.value && editId.value != null) {
      await updateRoute(editId.value, payload)
    } else {
      await createRoute(payload)
    }

    ElMessage.success(isEdit.value ? '线路更新成功' : '线路创建成功')
    dialogVisible.value = false
    load()
  } catch (error) {
    ElMessage.error(error?.response?.data?.message || '保存失败')
  }
}

async function removeItem(row) {
  await ElMessageBox.confirm(`确定删除线路「${row.title}」？`, '确认删除', { type: 'warning' })
  try {
    await deleteRoute(row.id)
    ElMessage.success('删除成功')
    load()
  } catch (error) {
    ElMessage.error(error?.response?.data?.message || '删除失败')
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

onMounted(loadHotelOptions)
</script>

<template>
  <AdminPageShell
    title="线路管理"
    subtitle="维护线路状态、价格、出发地与目的地信息"
    :summary="`已发布 ${publishedCount} / ${total}`"
  >
    <template #actions>
      <el-button type="primary" round @click="openCreate">+ 新增线路</el-button>
    </template>

    <template #filters>
      <el-input
        v-model="query.keyword"
        placeholder="搜索线路名称 / 出发地 / 目的地"
        clearable
        style="width: 280px"
        @keyup.enter="search"
      />

      <el-select v-model="query.status" style="width: 170px" @change="search">
        <el-option v-for="item in statusOptions" :key="item.value" :label="item.label" :value="item.value" />
      </el-select>

      <el-button type="primary" round @click="search">
        搜索
      </el-button>
    </template>

    <section v-loading="loading">
      <el-table :data="list" class="admin-liquid-table" stripe style="width: 100%">
        <el-table-column prop="title" label="线路名称" min-width="190" />
        <el-table-column prop="departure" label="出发地" width="120" />
        <el-table-column prop="destination" label="目的地" width="120" />
        <el-table-column label="关联酒店" min-width="180">
          <template #default="{ row }">{{ hotelLabel(row.hotel) }}</template>
        </el-table-column>
        <el-table-column prop="days" label="天数" width="80" align="center" />
        <el-table-column prop="price" label="价格" width="120" align="right">
          <template #default="{ row }">¥{{ formatMoney(row.price) }}</template>
        </el-table-column>
        <el-table-column prop="maxGroupSize" label="人数上限" width="110" align="center" />
        <el-table-column prop="status" label="状态" width="110" align="center">
          <template #default="{ row }">
            <el-tag :class="statusTagClass(row.status)" effect="light" size="small">{{ statusMap[row.status]?.label || row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="170" align="center" fixed="right">
          <template #default="{ row }">
            <div class="flex items-center justify-center gap-1.5">
              <el-button size="small" @click="openEdit(row)">编辑</el-button>
              <el-button size="small" type="danger" @click="removeItem(row)">删除</el-button>
            </div>
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

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑线路' : '新增线路'" width="680px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="96px">
        <el-form-item label="线路名称" prop="title">
          <el-input v-model="form.title" placeholder="请输入线路名称" />
        </el-form-item>

        <el-row :gutter="12">
          <el-col :span="12">
            <el-form-item label="出发地">
              <el-input v-model="form.departure" placeholder="如：上海" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="目的地">
              <el-input v-model="form.destination" placeholder="如：云南" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="关联酒店">
          <el-select v-model="form.hotelId" clearable filterable placeholder="可选，选择线路合作酒店" style="width: 100%">
            <el-option
              v-for="item in hotelOptions"
              :key="item.id"
              :label="hotelLabel(item)"
              :value="item.id"
            />
          </el-select>
        </el-form-item>

        <el-row :gutter="12">
          <el-col :span="8">
            <el-form-item label="天数" prop="days">
              <el-input-number v-model="form.days" :min="1" :max="30" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="价格" prop="price">
              <el-input v-model="form.price" placeholder="请输入价格" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="人数上限">
              <el-input v-model="form.maxGroupSize" placeholder="可选" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="状态">
          <el-select v-model="form.status" style="width: 100%">
            <el-option v-for="item in statusOptions.filter((x) => x.value)" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>

        <el-form-item label="封面图">
          <el-input v-model="form.coverImage" placeholder="图片 URL（可选）" />
        </el-form-item>

        <el-form-item label="简介">
          <el-input v-model="form.description" type="textarea" :rows="4" placeholder="请输入线路简介" />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submit">{{ isEdit ? '保存修改' : '创建线路' }}</el-button>
      </template>
    </el-dialog>
</template>

<style scoped>
.theme-tag {
  letter-spacing: 0.01em;
}
</style>
