<script setup>
import { reactive, ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getUsers, createUser, updateUser, deleteUser } from '@/api/users'
import AdminPageShell from '@/components/admin/AdminPageShell.vue'
import { usePaginatedList } from '@/composables/usePaginatedList'

const query = reactive({ keyword: '', page: 1, size: 10 })
const route = useRoute()
const { loading, list, total, load, search } = usePaginatedList({
  query,
  fetcher: getUsers,
  errorMessage: '用户数据加载失败',
})

const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref()
const form = reactive({
  username: '',
  password: '',
  realName: '',
  phone: '',
  email: '',
  role: 'USER',
  enabled: true,
})

const rules = {
  username: [{ required: true, message: '请输入用户名' }],
  password: [
    {
      validator: (_rule, value, callback) => {
        if (!isEdit.value && (!value || !String(value).trim())) {
          callback(new Error('请输入密码'))
          return
        }
        callback()
      },
      trigger: 'blur',
    },
  ],
}

let editId = null

const roleMap = {
  ADMIN: { label: '管理员', type: 'danger' },
  USER: { label: '普通用户', type: 'info' },
}

function openCreate() {
  isEdit.value = false
  editId = null
  Object.assign(form, {
    username: '',
    password: '',
    realName: '',
    phone: '',
    email: '',
    role: 'USER',
    enabled: true,
  })
  dialogVisible.value = true
}

function openEdit(row) {
  isEdit.value = true
  editId = row.id
  Object.assign(form, { ...row, password: '' })
  dialogVisible.value = true
}

async function submit() {
  await formRef.value.validate()
  try {
    const payload = { ...form }
    if (isEdit.value && (!payload.password || !String(payload.password).trim())) {
      delete payload.password
    }

    if (isEdit.value) await updateUser(editId, payload)
    else await createUser(payload)

    ElMessage.success(isEdit.value ? '用户更新成功' : '用户创建成功')
    dialogVisible.value = false
    load()
  } catch (e) {
    ElMessage.error(e?.response?.data?.message || '操作失败')
  }
}

async function removeUser(row) {
  await ElMessageBox.confirm(`确定删除用户「${row.username}」？`, '确认删除', { type: 'warning' })
  try {
    await deleteUser(row.id)
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
    title="用户管理"
    subtitle="统一管理后台用户、角色与账号状态"
    :summary="`当前共 ${total} 位用户`"
  >
    <template #actions>
      <el-button type="primary" round @click="openCreate">+ 新增用户</el-button>
    </template>

    <template #filters>
        <el-input
          v-model="query.keyword"
          placeholder="搜索用户名 / 姓名"
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
        <el-table-column prop="username" label="用户名" width="140" />
        <el-table-column prop="realName" label="姓名" width="120" />
        <el-table-column prop="phone" label="手机号" width="140" />
        <el-table-column prop="email" label="邮箱" min-width="180" />
        <el-table-column prop="role" label="角色" width="110" align="center">
          <template #default="{ row }">
            <el-tag :type="roleMap[row.role]?.type" size="small">{{ roleMap[row.role]?.label || row.role }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="enabled" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.enabled ? 'success' : 'warning'" size="small">{{ row.enabled ? '正常' : '禁用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="注册时间" width="180">
          <template #default="{ row }">{{ row.createdAt?.replace('T', ' ').slice(0, 16) || '-' }}</template>
        </el-table-column>
        <el-table-column label="操作" width="170" align="center" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="openEdit(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="removeUser(row)">删除</el-button>
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

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑用户' : '新增用户'" width="520px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" :disabled="isEdit" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input
            v-model="form.password"
            type="password"
            show-password
            :placeholder="isEdit ? '留空则不修改密码' : '请输入密码'"
          />
        </el-form-item>

        <el-row :gutter="12">
          <el-col :span="12">
            <el-form-item label="姓名">
              <el-input v-model="form.realName" placeholder="真实姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="手机号">
              <el-input v-model="form.phone" placeholder="手机号" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="邮箱">
          <el-input v-model="form.email" placeholder="邮箱地址" />
        </el-form-item>

        <el-row :gutter="12">
          <el-col :span="12">
            <el-form-item label="角色">
              <el-select v-model="form.role" style="width: 100%">
                <el-option label="管理员" value="ADMIN" />
                <el-option label="普通用户" value="USER" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态">
              <el-switch v-model="form.enabled" active-text="正常" inactive-text="禁用" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submit">{{ isEdit ? '保存' : '创建' }}</el-button>
      </template>
    </el-dialog>
</template>
