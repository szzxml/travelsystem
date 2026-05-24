import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'
import 'element-plus/dist/index.css'
import './style.css'
import './styles/admin.scss'
import App from './App.vue'
import router from './router'
import { motionDirective } from './directives/motion'
import i18n from './i18n'
import { initializeAppPreferences } from './composables/useAppPreferences'

const app = createApp(App)

app.use(createPinia())
app.use(router)
app.use(ElementPlus)
app.use(i18n)

initializeAppPreferences(i18n.global.locale)

app.component('User', User)
app.component('Lock', Lock)

app.directive('motion', motionDirective)

router.isReady().then(() => {
  app.mount('#app')
})
