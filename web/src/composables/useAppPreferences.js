import { reactive, readonly } from 'vue'

const STORAGE_KEY = 'ts_app_preferences'

const brandThemes = {
  ocean: {
    key: 'ocean',
    light: '#0f87a7',
    dark: '#0a637c',
    soft: '#dceff5',
    ring: '15, 135, 167',
  },
  emerald: {
    key: 'emerald',
    light: '#1f8a70',
    dark: '#176b57',
    soft: '#d8f3e6',
    ring: '31, 138, 112',
  },
  sunset: {
    key: 'sunset',
    light: '#c68a2d',
    dark: '#9c6b1e',
    soft: '#f7ead0',
    ring: '198, 138, 45',
  },
  rose: {
    key: 'rose',
    light: '#c55a4f',
    dark: '#a44439',
    soft: '#f7e0db',
    ring: '197, 90, 79',
  },
  azure: {
    key: 'azure',
    light: '#2563eb',
    dark: '#1d4ed8',
    soft: '#dbeafe',
    ring: '37, 99, 235',
  },
}

const defaultPreferences = {
  theme: 'light',
  locale: 'zh',
  notificationsEnabled: true,
  brandTheme: 'ocean',
}

const preferences = reactive({ ...defaultPreferences })

let initialized = false

function persist() {
  localStorage.setItem(STORAGE_KEY, JSON.stringify(preferences))
}

function hexToRgb(hex) {
  const normalized = hex.replace('#', '')
  const value = normalized.length === 3
    ? normalized.split('').map((char) => char + char).join('')
    : normalized

  const parsed = Number.parseInt(value, 16)
  return {
    r: (parsed >> 16) & 255,
    g: (parsed >> 8) & 255,
    b: parsed & 255,
  }
}

function toRgba(hex, alpha) {
  const { r, g, b } = hexToRgb(hex)
  return `rgba(${r}, ${g}, ${b}, ${alpha})`
}

function mixWithWhiteRgb(hex, ratio) {
  const { r, g, b } = hexToRgb(hex)
  const blend = (channel) => Math.round(channel + (255 - channel) * ratio)
  return {
    r: blend(r),
    g: blend(g),
    b: blend(b),
  }
}

function mixWithWhite(hex, ratio) {
  const { r, g, b } = mixWithWhiteRgb(hex, ratio)
  return `rgb(${r}, ${g}, ${b})`
}

function applyBrandTheme() {
  const activeTheme = brandThemes[preferences.brandTheme] || brandThemes.azure
  const accentColor = mixWithWhiteRgb(activeTheme.light, 0.18)
  const softColor = hexToRgb(activeTheme.soft)
  const darkColor = hexToRgb(activeTheme.dark)
  const borderAlpha = preferences.theme === 'dark' ? 0.22 : 0.1
  const borderStrongAlpha = preferences.theme === 'dark' ? 0.34 : 0.18
  const panelShadowAlpha = preferences.theme === 'dark' ? 0.24 : 0.12
  const cardShadowAlpha = preferences.theme === 'dark' ? 0.28 : 0.1
  const root = document.documentElement
  root.style.setProperty('--brand-primary', activeTheme.light)
  root.style.setProperty('--brand-primary-dark', activeTheme.dark)
  root.style.setProperty('--brand-primary-soft', activeTheme.soft)
  root.style.setProperty('--brand-primary-ring', activeTheme.ring)
  root.style.setProperty('--brand-soft-rgb', `${softColor.r}, ${softColor.g}, ${softColor.b}`)
  root.style.setProperty('--brand-dark-rgb', `${darkColor.r}, ${darkColor.g}, ${darkColor.b}`)
  root.style.setProperty('--brand', activeTheme.light)
  root.style.setProperty('--brand-dark', activeTheme.dark)
  root.style.setProperty('--brand-ghost', toRgba(activeTheme.light, 0.14))
  root.style.setProperty('--accent', `rgb(${accentColor.r}, ${accentColor.g}, ${accentColor.b})`)
  root.style.setProperty('--accent-rgb', `${accentColor.r}, ${accentColor.g}, ${accentColor.b}`)
  root.style.setProperty('--border', toRgba(activeTheme.light, borderAlpha))
  root.style.setProperty('--border-strong', toRgba(activeTheme.light, borderStrongAlpha))
  root.style.setProperty('--shadow-panel', `0 30px 80px ${toRgba(activeTheme.light, panelShadowAlpha)}`)
  root.style.setProperty('--shadow-lg', `0 20px 50px ${toRgba(activeTheme.dark, cardShadowAlpha)}`)
  root.style.setProperty('--el-color-primary', activeTheme.light)
  root.style.setProperty('--el-color-primary-dark-2', activeTheme.dark)
  root.style.setProperty('--el-color-primary-light-3', mixWithWhite(activeTheme.light, 0.22))
  root.style.setProperty('--el-color-primary-light-5', mixWithWhite(activeTheme.light, 0.4))
  root.style.setProperty('--el-color-primary-light-7', mixWithWhite(activeTheme.light, 0.6))
  root.style.setProperty('--el-color-primary-light-8', mixWithWhite(activeTheme.light, 0.72))
  root.style.setProperty('--el-color-primary-light-9', mixWithWhite(activeTheme.light, 0.84))
}

function applyTheme() {
  document.documentElement.classList.toggle('dark', preferences.theme === 'dark')
  document.documentElement.style.colorScheme = preferences.theme === 'dark' ? 'dark' : 'light'
  applyBrandTheme()
}

function applyLocale(localeRef) {
  if (localeRef) {
    localeRef.value = preferences.locale
  }
}

export function initializeAppPreferences(localeRef) {
  if (!initialized) {
    const stored = localStorage.getItem(STORAGE_KEY)
    let parsed = null
    if (stored) {
      parsed = JSON.parse(stored)
      Object.assign(preferences, defaultPreferences, parsed)
    }
    initialized = true
  }

  applyTheme()
  applyBrandTheme()
  applyLocale(localeRef)
  persist()
}

export function useAppPreferences() {
  function setTheme(theme) {
    preferences.theme = theme
    applyTheme()
    persist()
  }

  function toggleTheme() {
    setTheme(preferences.theme === 'dark' ? 'light' : 'dark')
  }

  function setLocale(nextLocale, localeRef) {
    preferences.locale = nextLocale
    applyLocale(localeRef)
    persist()
  }

  function toggleLocale(localeRef) {
    setLocale(preferences.locale === 'zh' ? 'en' : 'zh', localeRef)
  }

  function setNotificationsEnabled(enabled) {
    preferences.notificationsEnabled = enabled
    persist()
  }

  function setBrandTheme(themeKey) {
    preferences.brandTheme = brandThemes[themeKey] ? themeKey : defaultPreferences.brandTheme
    applyBrandTheme()
    persist()
  }

  function resetPreferences(localeRef) {
    Object.assign(preferences, defaultPreferences)
    applyTheme()
    applyBrandTheme()
    applyLocale(localeRef)
    persist()
  }

  return {
    preferences: readonly(preferences),
    brandThemes,
    setTheme,
    toggleTheme,
    setLocale,
    toggleLocale,
    setNotificationsEnabled,
    setBrandTheme,
    resetPreferences,
  }
}
