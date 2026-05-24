import http from './http'

const base = '/admin/hotels'
export const getHotels = (params) => http.get(base, { params })
export const getHotel = (id) => http.get(`${base}/${id}`)
export const createHotel = (data) => http.post(base, data)
export const updateHotel = (id, data) => http.put(`${base}/${id}`, data)
export const deleteHotel = (id) => http.delete(`${base}/${id}`)
