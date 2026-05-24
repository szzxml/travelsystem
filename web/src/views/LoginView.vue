<script setup>
import { reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { login } from '@/api/auth'
import { ElMessage } from 'element-plus'
import { Lock, User, Sparkles } from 'lucide-vue-next'

const route = useRoute()
const router = useRouter()
const store = useUserStore()
const formRef = ref()
const loading = ref(false)
const showDefaultHint = import.meta.env.DEV

const form = reactive({ username: '', password: '' })
const rules = {
  username: [{ required: true, message: '请输入账号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
}

async function submit() {
  await formRef.value.validate()
  loading.value = true
  try {
    const res = await login(form)
    store.setAuth(res.data)
    ElMessage.success('登录成功')
    const redirect = typeof route.query.redirect === 'string' ? route.query.redirect : '/admin/dashboard'
    await router.replace(redirect)
  } catch (e) {
    ElMessage.error(e?.response?.data?.message || '账号或密码错误')
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div>
    <div class="scene-bg" aria-hidden="true"></div>

    <div class="app-shell grid min-h-screen place-items-center px-4 py-8">
      <div class="w-full max-w-md rounded-[2.5rem] bg-white p-8 shadow-[0_32px_80px_rgba(0,122,255,0.12)] border border-gray-100">
        <div class="flex items-center justify-between">
          <div class="flex items-center gap-3">
            <img src="/logo.png" alt="logo" class="h-10 w-10 rounded-xl object-cover shadow-sm">
            <div>
              <p class="text-2xl font-bold tracking-tight text-gray-900">voyage admin</p>
              <p class="text-xs font-medium text-gray-500">云端旅游运营后台</p>
            </div>
          </div>
          <span class="inline-flex h-10 w-10 items-center justify-center rounded-full bg-brand/10">
            <Sparkles class="h-5 w-5 text-brand" />
          </span>
        </div>

        <h1 class="mt-10 text-4xl font-bold tracking-tight text-gray-900">
          欢迎回到
          <span class="text-brand">管理中心</span>
        </h1>
        <p class="mt-3 text-base text-gray-500 font-medium">登录后继续高效管理线路、酒店及订单。</p>

        <el-form ref="formRef" :model="form" :rules="rules" class="mt-8 space-y-5" @keyup.enter="submit">
          <el-form-item prop="username" class="!mb-5">
            <div class="flex items-center gap-3 rounded-2xl bg-gray-50 border border-gray-100 px-5 py-3.5 focus-within:border-brand focus-within:ring-4 focus-within:ring-brand/5 transition-all">
              <User class="h-5 w-5 text-gray-400" />
              <el-input v-model="form.username" placeholder="请输入账号" class="azure-input" />
            </div>
          </el-form-item>

          <el-form-item prop="password" class="!mb-6">
            <div class="flex items-center gap-3 rounded-2xl bg-gray-50 border border-gray-100 px-5 py-3.5 focus-within:border-brand focus-within:ring-4 focus-within:ring-brand/5 transition-all">
              <Lock class="h-5 w-5 text-gray-400" />
              <el-input v-model="form.password" type="password" show-password placeholder="请输入密码" class="azure-input" />
            </div>
          </el-form-item>

          <el-button
            type="primary"
            class="!h-14 !w-full !rounded-2xl !text-lg !font-bold bg-gradient-to-r from-blue-600 to-indigo-600 border-none shadow-lg shadow-blue-200 transition-all hover:scale-[1.02] active:scale-95"
            :loading="loading"
            @click="submit"
          >
            登录
          </el-button>
        </el-form>

        <p v-if="showDefaultHint" class="mt-6 text-center text-sm text-gray-400">
          开发环境默认账号：<span class="font-bold text-gray-600">admin / admin123</span>
        </p>
      </div>
    </div>
  </div>
</template>

<style scoped>
:deep(.el-form-item__error) {
  color: #e11d48;
  margin-top: 4px;
  margin-left: 4px;
}

:deep(.el-input) {
  width: 100%;
}

:deep(.el-input__wrapper) {
  box-shadow: none !important;
  background: transparent !important;
  padding: 0 !important;
}

:deep(.el-input__inner) {
  color: #1d1d1f;
  font-weight: 500;
}

:deep(.el-input__inner::placeholder) {
  color: #a1a1aa;
}
</style>
