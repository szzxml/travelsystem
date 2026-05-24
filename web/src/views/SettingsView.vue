<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { ElMessage } from 'element-plus'
import { useAppPreferences } from '@/composables/useAppPreferences'
import AdminPageShell from '@/components/admin/AdminPageShell.vue'

const router = useRouter()
const { locale } = useI18n({ useScope: 'global' })
const {
  preferences,
  brandThemes,
  setTheme,
  setLocale,
  setNotificationsEnabled,
  setBrandTheme,
  resetPreferences,
} = useAppPreferences()

const themeLabel = computed(() => (preferences.theme === 'dark' ? 'settings.darkMode' : 'settings.lightMode'))
const localeLabel = computed(() => (preferences.locale === 'zh' ? 'settings.chinese' : 'settings.english'))
const notificationsLabel = computed(() => (preferences.notificationsEnabled ? 'common.enabled' : 'common.disabled'))
const themeOptions = computed(() => Object.values(brandThemes))

function applyBrandTheme(themeKey) {
  setBrandTheme(themeKey)
  ElMessage.success('品牌主题已更新')
}

function applyTheme(theme) {
  setTheme(theme)
  ElMessage.success(theme === 'dark' ? '深色模式已启用' : '浅色模式已启用')
}

function applyLocale(nextLocale) {
  setLocale(nextLocale, locale)
  ElMessage.success(nextLocale === 'zh' ? '界面语言已切换为中文' : 'Switched to English')
}

function applyNotifications(enabled) {
  setNotificationsEnabled(enabled)
  ElMessage.success(enabled ? '通知面板已开启' : '通知面板已关闭')
}

function resetAll() {
  resetPreferences(locale)
  ElMessage.success('设置已恢复默认值')
}
</script>

<template>
  <AdminPageShell
    :title="$t('settings.title')"
    :subtitle="$t('settings.subtitle')"
    :summary="`${$t(themeLabel)} / ${$t(localeLabel)} / ${$t(notificationsLabel)}`"
  >
    <template #actions>
      <el-button round plain type="warning" @click="resetAll">
        {{ $t('common.reset') }}
      </el-button>
    </template>

    <div class="admin-settings-grid">
      <el-card shadow="never" class="admin-settings-card">
        <template #header>
          <div class="admin-settings-card__header">
            <p class="admin-settings-card__title">{{ $t('settings.appearanceTitle') }}</p>
            <p class="admin-settings-card__description">{{ $t('settings.appearanceDescription') }}</p>
          </div>
        </template>

        <div class="admin-settings-card__body">
          <el-radio-group
            :model-value="preferences.theme"
            class="admin-settings-option-group"
            @update:model-value="applyTheme"
          >
            <el-radio-button label="light">{{ $t('settings.lightMode') }}</el-radio-button>
            <el-radio-button label="dark">{{ $t('settings.darkMode') }}</el-radio-button>
          </el-radio-group>
        </div>
      </el-card>

      <el-card shadow="never" class="admin-settings-card">
        <template #header>
          <div class="admin-settings-card__header">
            <p class="admin-settings-card__title">{{ $t('settings.languageTitle') }}</p>
            <p class="admin-settings-card__description">{{ $t('settings.languageDescription') }}</p>
          </div>
        </template>

        <div class="admin-settings-card__body">
          <el-radio-group
            :model-value="preferences.locale"
            class="admin-settings-option-group"
            @update:model-value="applyLocale"
          >
            <el-radio-button label="zh">{{ $t('settings.chinese') }}</el-radio-button>
            <el-radio-button label="en">{{ $t('settings.english') }}</el-radio-button>
          </el-radio-group>
        </div>
      </el-card>

      <el-card shadow="never" class="admin-settings-card admin-settings-card--wide">
        <template #header>
          <div class="admin-settings-card__header">
            <p class="admin-settings-card__title">{{ $t('settings.summaryTitle') }}</p>
            <p class="admin-settings-card__description">{{ $t('settings.summaryDescription') }}</p>
          </div>
        </template>

        <div class="admin-settings-card__body">
          <div class="admin-settings-theme-list">
            <el-button
              v-for="theme in themeOptions"
              :key="theme.key"
              :type="preferences.brandTheme === theme.key ? 'primary' : 'default'"
              :plain="preferences.brandTheme !== theme.key"
              class="admin-settings-theme-button"
              @click="applyBrandTheme(theme.key)"
            >
              <span class="admin-settings-theme-button__dot" :style="{ backgroundColor: theme.dark }" />
              <span>{{ theme.key }}</span>
            </el-button>
          </div>
        </div>
      </el-card>

      <el-card shadow="never" class="admin-settings-card">
        <template #header>
          <div class="admin-settings-card__header">
            <p class="admin-settings-card__title">{{ $t('settings.notificationTitle') }}</p>
            <p class="admin-settings-card__description">{{ $t('settings.notificationDescription') }}</p>
          </div>
        </template>

        <div class="admin-settings-card__body">
          <div class="admin-settings-status">
            <div class="admin-settings-status__label">
              <span>{{ $t('settings.notifications') }}</span>
              <el-tag
                size="small"
                effect="plain"
                :type="preferences.notificationsEnabled ? 'success' : 'warning'"
              >
                {{ $t(notificationsLabel) }}
              </el-tag>
            </div>
            <el-switch
              :model-value="preferences.notificationsEnabled"
              @change="applyNotifications"
            />
          </div>
        </div>
      </el-card>

      <el-card shadow="never" class="admin-settings-card">
        <template #header>
          <div class="admin-settings-card__header">
            <p class="admin-settings-card__title">{{ $t('settings.quickActionsTitle') }}</p>
            <p class="admin-settings-card__description">{{ $t('settings.quickActionsDescription') }}</p>
          </div>
        </template>

        <div class="admin-settings-card__body">
          <div class="admin-settings-actions">
            <el-button type="primary" round @click="router.push('/admin/notices')">
              {{ $t('settings.openNotices') }}
            </el-button>
            <el-button round @click="router.push('/admin/orders')">
              {{ $t('settings.openOrders') }}
            </el-button>
          </div>
        </div>
      </el-card>
    </div>
  </AdminPageShell>
</template>

<style scoped>
.admin-settings-grid {
  display: grid;
  gap: 16px;
  grid-template-columns: repeat(2, minmax(0, 1fr));
}

.admin-settings-card {
  height: 100%;
}

.admin-settings-card--wide {
  grid-column: span 2;
}

.admin-settings-card__header {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.admin-settings-card__title {
  font-size: 16px;
  font-weight: 700;
  color: hsl(var(--text-100));
}

.admin-settings-card__description {
  font-size: 13px;
  line-height: 1.6;
  color: var(--text-muted);
}

.admin-settings-card__body {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.admin-settings-option-group {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.admin-settings-option-group :deep(.el-radio-button__inner) {
  min-width: 124px;
  border-radius: 999px;
  border-left: 1px solid var(--border);
  padding: 10px 18px;
  background: linear-gradient(180deg, rgba(var(--brand-soft-rgb), 0.22) 0%, hsl(var(--panel)) 100%);
  color: hsl(var(--text-80));
  box-shadow: none;
}

.admin-settings-option-group :deep(.el-radio-button:first-child .el-radio-button__inner) {
  border-left: 1px solid var(--border);
}

.admin-settings-option-group :deep(.el-radio-button__original-radio:checked + .el-radio-button__inner) {
  background: var(--brand);
  border-color: var(--brand);
  box-shadow: none;
  color: #fff;
}

.admin-settings-theme-list,
.admin-settings-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.admin-settings-theme-button {
  min-width: 120px;
}

.admin-settings-theme-button__dot {
  display: inline-block;
  width: 10px;
  height: 10px;
  margin-right: 8px;
  border-radius: 999px;
  box-shadow: inset 0 0 0 1px rgba(255, 255, 255, 0.35);
}

.admin-settings-status {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
}

.admin-settings-status__label {
  display: flex;
  align-items: center;
  gap: 10px;
  color: hsl(var(--text-80));
  font-size: 14px;
}

.admin-settings-card :deep(.el-card__header) {
  border-bottom: 1px solid var(--border);
}

.admin-settings-card :deep(.el-card__body) {
  padding-top: 18px;
}

@media (max-width: 1024px) {
  .admin-settings-grid {
    grid-template-columns: minmax(0, 1fr);
  }

  .admin-settings-card--wide {
    grid-column: auto;
  }
}

@media (max-width: 640px) {
  .admin-settings-status {
    align-items: flex-start;
    flex-direction: column;
  }

  .admin-settings-option-group :deep(.el-radio-button),
  .admin-settings-option-group :deep(.el-radio-button__inner),
  .admin-settings-theme-button {
    width: 100%;
  }
}
</style>
