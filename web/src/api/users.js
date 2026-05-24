import http from './http'

const base = '/admin/users'
export const getUsers = (params) => http.get(base, { params })
export const getUser = (id) => http.get(`${base}/${id}`)
export const createUser = (data) => http.post(base, data)
export const updateUser = (id, data) => http.put(`${base}/${id}`, data)
export const deleteUser = (id) => http.delete(`${base}/${id}`)
