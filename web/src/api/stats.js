import http from './http'

export const getStats = () => http.get('/admin/stats')
export const exportStatsCsv = () => http.get('/admin/stats/export', {
  responseType: 'blob',
  rawResponse: true,
})
