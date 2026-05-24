<script setup>
import { reactive, ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getNotices, createNotice, updateNotice, deleteNotice } from '@/api/notices'
import AdminPageShell from '@/components/admin/AdminPageShell.vue'
import { usePaginatedList } from '@/composables/usePaginatedList'

const query = reactive({ keyword: '', page: 1, size: 10 })
const route = useRoute()
const { loading, list, total, load, search } = usePaginatedList({
  query,
  fetcher: getNotices,
  errorMessage: '公告数据加载失败',
})

const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref()
const form = reactive({
  title: '',
  content: '',
  type: 'NOTICE',
  published: false,
})

const rules = {
  title: [{ required: true, message: '请输入标题' }],
  content: [{ required: true, message: '请输入内容' }],
}

let editId = null

const typeMap = {
  NOTICE: { label: '公告', type: 'info' },
  NEWS: { label: '新闻', type: 'success' },
  ACTIVITY: { label: '活动', type: 'warning' },
}

function openCreate() {
  isEdit.value = false
  editId = null
  Object.assign(form, {
    title: '',
    content: '',
    type: 'NOTICE',
    published: false,
  })
  dialogVisible.value = true
}

function openEdit(row) {
  isEdit.value = true
  editId = row.id
  Object.assign(form, { ...row })
  dialogVisible.value = true
}

async function submit() {
  await formRef.value.validate()
  try {
    if (isEdit.value) await updateNotice(editId, form)
    else await createNotice(form)

    ElMessage.success(isEdit.value ? '公告更新成功' : '公告发布成功')
    dialogVisible.value = false
    load()
  } catch (e) {
    ElMessage.error(e?.response?.data?.message || '操作失败')
  }
}

async function removeItem(row) {
  await ElMessageBox.confirm(`确定删除公告「${row.title}」？`, '确认删除', { type: 'warning' })
  try {
    await deleteNotice(row.id)
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
    title="公告管理"
    subtitle="发布公告、新闻与活动信息，并控制展示状态"
    :summary="`当前共 ${total} 条公告`"
  >
    <template #actions>
      <el-button type="primary" round @click="openCreate">+ 发布公告</el-button>
    </template>

    <template #filters>
        <el-input
          v-model="query.keyword"
          placeholder="搜索公告标题"
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
        <el-table-column prop="title" label="标题" min-width="240" />
        <el-table-column prop="type" label="类型" width="120" align="center">
          <template #default="{ row }">
            <el-tag :type="typeMap[row.type]?.type" size="small">{{ typeMap[row.type]?.label || row.type }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="published" label="发布状态" width="120" align="center">
          <template #default="{ row }">
            <el-tag :type="row.published ? 'success' : 'warning'" size="small">{{ row.published ? '已发布' : '草稿' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="180">
          <template #default="{ row }">{{ row.createdAt?.replace('T', ' ').slice(0, 16) || '-' }}</template>
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

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑公告' : '发布公告'" width="620px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入公告标题" />
        </el-form-item>

        <el-row :gutter="12">
          <el-col :span="12">
            <el-form-item label="类型">
              <el-select v-model="form.type" style="width: 100%">
                <el-option v-for="(value, key) in typeMap" :key="key" :label="value.label" :value="key" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="发布">
              <el-switch v-model="form.published" active-text="立即发布" inactive-text="保存草稿" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="内容" prop="content">
          <el-input v-model="form.content" type="textarea" :rows="7" placeholder="请输入公告内容" />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submit">{{ isEdit ? '保存' : '发布' }}</el-button>
      </template>
    </el-dialog>
</template>
