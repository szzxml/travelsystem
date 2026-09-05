<script setup>
import { reactive, ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getAttractions, createAttraction, updateAttraction, deleteAttraction } from '@/api/attractions'
import AdminPageShell from '@/components/admin/AdminPageShell.vue'
import { usePaginatedList } from '@/composables/usePaginatedList'

const query = reactive({ keyword: '', page: 1, size: 10 })
const route = useRoute()
const { loading, list, total, load, search } = usePaginatedList({
  query,
  fetcher: getAttractions,
  errorMessage: '景点数据加载失败',
})

const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref()
const form = reactive({
  name: '',
  location: '',
  description: '',
  coverImage: '',
  ticketPrice: '',
  openTime: '',
  capacity: '',
  status: 'OPEN',
})

const rules = {
  name: [{ required: true, message: '请输入景点名称' }],
}

let editId = null

const statusMap = {
  OPEN: { label: '开放中', type: 'success' },
  CLOSED: { label: '已关闭', type: 'danger' },
  MAINTENANCE: { label: '维护中', type: 'warning' },
}

function openCreate() {
  isEdit.value = false
  editId = null
  Object.assign(form, {
    name: '',
    location: '',
    description: '',
    coverImage: '',
    ticketPrice: '',
    openTime: '',
    capacity: '',
    status: 'OPEN',
  })
  dialogVisible.value = true
}

function openEdit(row) {
  isEdit.value = true
  editId = row.id
  Object.assign(form, {
    ...row,
    ticketPrice: String(row.ticketPrice || ''),
    capacity: String(row.capacity || ''),
  })
  dialogVisible.value = true
}

function formatMoney(value) {
  if (value == null || value === '') return '-'
  return Number(value).toLocaleString('zh-CN')
}

async function submit() {
  await formRef.value.validate()

  const payload = {
    ...form,
    ticketPrice: form.ticketPrice ? Number(form.ticketPrice) : null,
    capacity: form.capacity ? Number(form.capacity) : null,
  }

  try {
    if (isEdit.value) await updateAttraction(editId, payload)
    else await createAttraction(payload)

    ElMessage.success(isEdit.value ? '景点更新成功' : '景点创建成功')
    dialogVisible.value = false
    load()
  } catch (e) {
    ElMessage.error(e?.response?.data?.message || '操作失败')
  }
}

async function removeItem(row) {
  await ElMessageBox.confirm(`确定删除景点「${row.name}」？`, '确认删除', { type: 'warning' })
  try {
    await deleteAttraction(row.id)
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
    title="景点管理"
    subtitle="维护景点资料、开放时间、容量与票价信息"
    :summary="`当前共 ${total} 个景点`"
  >
    <template #actions>
      <el-button type="primary" round @click="openCreate">+ 新增景点</el-button>
    </template>

    <template #filters>
        <el-input
          v-model="query.keyword"
          placeholder="搜索景点名称"
          clearable
          style="width: 240px"
          @keyup.enter="search"
        />
        <el-button type="primary" round @click="search">
          搜索
        </el-button>
    </template>

    <section v-loading="loading">
      <el-table :data="list" class="admin-liquid-table" stripe style="width: 100%">
        <el-table-column prop="name" label="景点名称" min-width="170" />
        <el-table-column prop="location" label="所在地" width="170" />
        <el-table-column prop="ticketPrice" label="门票价格" width="130" align="right">
          <template #default="{ row }">{{ row.ticketPrice != null ? `¥${formatMoney(row.ticketPrice)}` : '-' }}</template>
        </el-table-column>
        <el-table-column prop="openTime" label="开放时间" width="170" />
        <el-table-column prop="capacity" label="日容量" width="110" align="center" />
        <el-table-column prop="status" label="状态" width="110" align="center">
          <template #default="{ row }">
            <el-tag :type="statusMap[row.status]?.type" size="small">{{ statusMap[row.status]?.label || row.status }}</el-tag>
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

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑景点' : '新增景点'" width="560px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="景点名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入景点名称" />
        </el-form-item>

        <el-row :gutter="12">
          <el-col :span="12">
            <el-form-item label="所在地">
              <el-input v-model="form.location" placeholder="如：云南大理" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="门票价格">
              <el-input v-model="form.ticketPrice" placeholder="可选，单位元" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="12">
          <el-col :span="12">
            <el-form-item label="开放时间">
              <el-input v-model="form.openTime" placeholder="如：08:00-18:00" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="日容量">
              <el-input v-model="form.capacity" placeholder="可选" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="状态">
          <el-select v-model="form.status" style="width: 100%">
            <el-option v-for="(value, key) in statusMap" :key="key" :label="value.label" :value="key" />
          </el-select>
        </el-form-item>

        <el-form-item label="封面图">
          <el-input v-model="form.coverImage" placeholder="图片 URL（可选）" />
        </el-form-item>

        <el-form-item label="简介">
          <el-input v-model="form.description" type="textarea" :rows="4" placeholder="景点介绍" />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submit">{{ isEdit ? '保存' : '创建' }}</el-button>
      </template>
    </el-dialog>
</template>
