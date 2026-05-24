<script setup>
import { reactive, ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { createHotel, deleteHotel, getHotels, updateHotel } from '@/api/hotels'
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
  fetcher: getHotels,
  errorMessage: '酒店数据加载失败',
})

const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref()
const editId = ref(null)

const form = reactive({
  name: '',
  city: '',
  address: '',
  phone: '',
  starLevel: 4,
  description: '',
  status: 'ACTIVE',
})

const rules = {
  name: [{ required: true, message: '请输入酒店名称', trigger: 'blur' }],
}

const statusOptions = [
  { value: '', label: '全部状态' },
  { value: 'ACTIVE', label: '营业中' },
  { value: 'INACTIVE', label: '已停用' },
]

const statusMap = {
  ACTIVE: { label: '营业中', type: 'success' },
  INACTIVE: { label: '已停用', type: 'info' },
}

function resetForm() {
  Object.assign(form, {
    name: '',
    city: '',
    address: '',
    phone: '',
    starLevel: 4,
    description: '',
    status: 'ACTIVE',
  })
}

function formatStarLevel(value) {
  return value ? `${value} 星` : '-'
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
    name: row.name || '',
    city: row.city || '',
    address: row.address || '',
    phone: row.phone || '',
    starLevel: row.starLevel || 4,
    description: row.description || '',
    status: row.status || 'ACTIVE',
  })
  dialogVisible.value = true
}

async function submit() {
  await formRef.value.validate()

  const payload = {
    ...form,
    starLevel: form.starLevel ? Number(form.starLevel) : null,
  }

  try {
    if (isEdit.value && editId.value != null) {
      await updateHotel(editId.value, payload)
    } else {
      await createHotel(payload)
    }

    ElMessage.success(isEdit.value ? '酒店更新成功' : '酒店创建成功')
    dialogVisible.value = false
    load()
  } catch (error) {
    ElMessage.error(error?.response?.data?.message || '保存失败')
  }
}

async function removeItem(row) {
  await ElMessageBox.confirm(`确定删除酒店「${row.name}」？`, '确认删除', { type: 'warning' })
  try {
    await deleteHotel(row.id)
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
</script>

<template>
  <AdminPageShell
    title="酒店管理"
    subtitle="集中维护酒店资源、星级、地址与营业状态"
    :summary="`当前共 ${total} 家酒店`"
  >
    <template #actions>
      <el-button type="primary" round @click="openCreate">+ 新增酒店</el-button>
    </template>

    <template #filters>
      <el-input
        v-model="query.keyword"
        placeholder="搜索酒店名称 / 城市"
        clearable
        style="width: 260px"
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
        <el-table-column prop="name" label="酒店名称" min-width="180" />
        <el-table-column prop="city" label="所在城市" width="130" />
        <el-table-column prop="address" label="地址" min-width="220" show-overflow-tooltip />
        <el-table-column prop="phone" label="联系电话" width="140" />
        <el-table-column prop="starLevel" label="星级" width="90" align="center">
          <template #default="{ row }">{{ formatStarLevel(row.starLevel) }}</template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="110" align="center">
          <template #default="{ row }">
            <el-tag :type="statusMap[row.status]?.type" size="small">{{ statusMap[row.status]?.label || row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="170" align="center" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="openEdit(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="removeItem(row)">删除</el-button>
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

  <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑酒店' : '新增酒店'" width="620px" destroy-on-close>
    <el-form ref="formRef" :model="form" :rules="rules" label-width="96px">
      <el-form-item label="酒店名称" prop="name">
        <el-input v-model="form.name" placeholder="请输入酒店名称" />
      </el-form-item>

      <el-row :gutter="12">
        <el-col :span="12">
          <el-form-item label="所在城市">
            <el-input v-model="form.city" placeholder="如：杭州" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="联系电话">
            <el-input v-model="form.phone" placeholder="酒店联系电话" />
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="12">
        <el-col :span="12">
          <el-form-item label="酒店星级">
            <el-input-number v-model="form.starLevel" :min="1" :max="5" style="width: 100%" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="状态">
            <el-select v-model="form.status" style="width: 100%">
              <el-option v-for="item in statusOptions.filter((x) => x.value)" :key="item.value" :label="item.label" :value="item.value" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>

      <el-form-item label="地址">
        <el-input v-model="form.address" placeholder="详细地址" />
      </el-form-item>

      <el-form-item label="简介">
        <el-input v-model="form.description" type="textarea" :rows="4" placeholder="酒店简介、配套或服务说明" />
      </el-form-item>
    </el-form>

    <template #footer>
      <el-button @click="dialogVisible = false">取消</el-button>
      <el-button type="primary" @click="submit">{{ isEdit ? '保存修改' : '创建酒店' }}</el-button>
    </template>
  </el-dialog>
</template>
