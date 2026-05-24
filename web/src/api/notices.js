import http from './http'

const base = '/admin/notices'
export const getNotices = (params) => http.get(base, { params })
export const getNotice = (id) => http.get(`${base}/${id}`)
export const createNotice = (data) => http.post(base, data)
export const updateNotice = (id, data) => http.put(`${base}/${id}`, data)
export const deleteNotice = (id) => http.delete(`${base}/${id}`)
