import http from './http'

const base = '/admin/routes'
export const getRoutes = (params) => http.get(base, { params })
export const getRoute = (id) => http.get(`${base}/${id}`)
export const createRoute = (data) => http.post(base, data)
export const updateRoute = (id, data) => http.put(`${base}/${id}`, data)
export const deleteRoute = (id) => http.delete(`${base}/${id}`)
