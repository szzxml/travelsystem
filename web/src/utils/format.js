export function formatMoney(value) {
  const amount = Number(value || 0)
  return Number.isFinite(amount) ? amount.toLocaleString('zh-CN') : '0'
}

export function formatDateTime(value) {
  return value?.replace('T', ' ').slice(0, 16) || '-'
}
