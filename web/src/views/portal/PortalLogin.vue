<script setup>
import { reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { login } from '@/api/auth'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const store = useUserStore()
const formRef = ref()
const loading = ref(false)
const form = reactive({ username: '', password: '' })
const rules = {
  username: [{ required: true, message: '请输入账号' }],
  password: [{ required: true, message: '请输入密码' }],
}

async function submit() {
  await formRef.value.validate()
  loading.value = true
  try {
    const res = await login(form)
    store.setAuth(res.data)
    ElMessage.success('登录成功')
    const redirect = typeof route.query.redirect === 'string' ? route.query.redirect : '/'
    router.push(redirect)
  } catch (e) {
    ElMessage.error(e?.response?.data?.message || '账号或密码错误')
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="auth-page">
    <div class="auth-card">
      <div class="auth-logo">
        <span class="text-brand">✈</span>
        <div>
          <div class="auth-title">欢迎回来</div>
          <div class="auth-sub">登录您的云端旅游账号</div>
        </div>
      </div>
      <el-form ref="formRef" :model="form" :rules="rules" label-position="top" @keyup.enter="submit">
        <el-form-item label="账号" prop="username">
          <el-input v-model="form.username" placeholder="请输入账号" size="large" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" show-password placeholder="请输入密码" size="large" />
        </el-form-item>
        <el-button type="primary" size="large" style="width:100%" :loading="loading" @click="submit">登录</el-button>
      </el-form>
      <div class="auth-footer">
        还没有账号？
        <RouterLink to="/register" style="color:var(--brand);font-weight:600">立即注册</RouterLink>
      </div>
    </div>
  </div>
</template>

<style scoped>
.auth-page {
  min-height: calc(100vh - 140px);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px 24px;
  background: linear-gradient(160deg, #f0f7ff 0%, #ffffff 60%);
}

.auth-card {
  background: #fff;
  border-radius: 20px;
  padding: 40px 36px;
  width: min(420px, 100%);
  box-shadow: var(--shadow-lg);
  animation: fadeInUp 0.4s ease;
}

.auth-logo {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 28px;
  font-size: 36px;
}

.auth-title {
  font-size: 20px;
  font-weight: 700;
  color: var(--brand);
}

.auth-sub {
  font-size: 13px;
  color: var(--text-muted);
  margin-top: 2px;
}

.auth-footer {
  text-align: center;
  margin-top: 16px;
  font-size: 13px;
  color: var(--text-muted);
}

@keyframes fadeInUp {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}
</style>
