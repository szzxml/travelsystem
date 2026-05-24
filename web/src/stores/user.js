import { defineStore } from 'pinia'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: localStorage.getItem('access_token') || '',
    username: localStorage.getItem('username') || '',
    role: localStorage.getItem('role') || '',
    realName: localStorage.getItem('realName') || '',
  }),
  getters: {
    isLoggedIn: (state) => !!state.token,
    isAdmin: (state) => state.role === 'ADMIN',
  },
  actions: {
    setAuth({ token, username, role, realName }) {
      this.token = token
      this.username = username
      this.role = role
      this.realName = realName
      localStorage.setItem('access_token', token)
      localStorage.setItem('username', username)
      localStorage.setItem('role', role)
      localStorage.setItem('realName', realName)
    },
    clear() {
      this.token = ''
      this.username = ''
      this.role = ''
      this.realName = ''
      localStorage.removeItem('access_token')
      localStorage.removeItem('username')
      localStorage.removeItem('role')
      localStorage.removeItem('realName')
    },
  },
})
