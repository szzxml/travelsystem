import axios from 'axios'
import { ElMessage } from 'element-plus'

const request = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api',
  timeout: 10000,
})

request.interceptors.request.use((config) => {
  const token = localStorage.getItem('access_token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

request.interceptors.response.use(
  (response) => {
    if (response.config?.rawResponse || response.config?.responseType === 'blob' || response.config?.responseType === 'arraybuffer') {
      return response
    }

    const payload = response.data

    if (payload && typeof payload.code === 'number' && payload.code !== 0) {
      const error = new Error(payload.message || 'Request failed')
      error.name = 'ApiBusinessError'
      error.response = {
        ...response,
        data: payload,
      }
      return Promise.reject(error)
    }

    return payload
  },
  (error) => {
    const status = error?.response?.status
    const currentPath = window.location.pathname

    if (status === 401) {
      localStorage.removeItem('access_token')
      localStorage.removeItem('username')
      localStorage.removeItem('role')
      localStorage.removeItem('realName')
      const loginPath = currentPath.startsWith('/admin') ? '/admin/login' : '/login'
      const redirect = encodeURIComponent(window.location.pathname + window.location.search)
      if (!currentPath.startsWith('/login') && !currentPath.startsWith('/admin/login')) {
        window.location.assign(`${loginPath}?redirect=${redirect}`)
      }
    } else if (status === 403) {
      ElMessage.error(error?.response?.data?.message || '无权执行当前操作')
    } else if (status >= 500) {
      ElMessage.error(error?.response?.data?.message || '服务暂时不可用，请稍后再试')
    }

    return Promise.reject(error)
  }
)

export default request
