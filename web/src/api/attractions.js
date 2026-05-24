import http from './http'

const base = '/admin/attractions'
export const getAttractions = (params) => http.get(base, { params })
export const getAttraction = (id) => http.get(`${base}/${id}`)
export const createAttraction = (data) => http.post(base, data)
export const updateAttraction = (id, data) => http.put(`${base}/${id}`, data)
export const deleteAttraction = (id) => http.delete(`${base}/${id}`)
