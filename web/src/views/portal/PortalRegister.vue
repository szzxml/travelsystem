<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import http from '@/api/http'
import { ElMessage } from 'element-plus'

const router = useRouter()
const store = useUserStore()
const formRef = ref()
const loading = ref(false)
const form = reactive({ username: '', password: '', confirmPassword: '', realName: '', phone: '' })
const rules = {
  username: [{ required: true, message: '请输入用户名' }, { min: 3, message: '用户名至少3位' }],
  password: [{ required: true, message: '请输入密码' }, { min: 6, message: '密码至少6位' }],
  confirmPassword: [
    { required: true, message: '请确认密码' },
    { validator: (rule, val, cb) => val === form.password ? cb() : cb(new Error('两次密码不一致')), trigger: 'blur' }
  ],
}

async function submit() {
  await formRef.value.validate()
  loading.value = true
  try {
    const res = await http.post('/auth/register', {
      username: form.username,
      password: form.password,
      realName: form.realName,
      phone: form.phone,
    })
    store.setAuth(res.data)
    ElMessage.success('注册成功，欢迎加入！')
    router.push('/')
  } catch (e) {
    ElMessage.error(e?.response?.data?.message || '注册失败')
  } finally { loading.value = false }
}
</script>

<template>
  <div class="auth-page">
    <div class="auth-card">
      <div class="auth-logo">
        <span class="text-brand">☁️</span>
        <div>
          <div class="auth-title">创建账号</div>
          <div class="auth-sub">加入我们，开启云端旅行之旅</div>
        </div>
      </div>
      <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="用户名" />
        </el-form-item>
        <el-form-item label="真实姓名">
          <el-input v-model="form.realName" placeholder="可选" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" show-password placeholder="至少6位" />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="form.confirmPassword" type="password" show-password placeholder="再次输入密码" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="form.phone" placeholder="可选" />
        </el-form-item>
        <el-button type="primary" size="large" style="width:100%" :loading="loading" @click="submit">注 册</el-button>
      </el-form>
      <div class="auth-footer">
        已有账号？
        <RouterLink to="/login" style="color:var(--brand);font-weight:600">立即登录</RouterLink>
      </div>
    </div>
  </div>
</template>

<style scoped>
.auth-page {
  min-height: calc(100vh - 140px);
  display: flex; align-items: center; justify-content: center;
  padding: 40px 24px;
  background: linear-gradient(160deg, #f0f7ff 0%, #ffffff 60%);
}
.auth-card {
  background: #fff; border-radius: 20px; padding: 40px 36px;
  width: min(480px, 100%); box-shadow: var(--shadow-lg);
  animation: fadeInUp 0.4s ease;
}
.auth-logo { display: flex; align-items: center; gap: 12px; margin-bottom: 24px; font-size: 36px; }
.auth-title { font-size: 20px; font-weight: 700; color: var(--brand); }
.auth-sub { font-size: 13px; color: var(--text-muted); margin-top: 2px; }
.auth-footer { text-align: center; margin-top: 16px; font-size: 13px; color: var(--text-muted); }
@keyframes fadeInUp {
  from { opacity:0; transform:translateY(20px); }
  to { opacity:1; transform:translateY(0); }
}
</style>
