import http from './http'

const base = '/admin/orders'
export const getOrders = (params) => http.get(base, { params })
export const getOrder = (id) => http.get(`${base}/${id}`)
export const updateOrderStatus = (id, status, rejectReason) => http.patch(`${base}/${id}/status`, null, {
  params: {
    status,
    ...(rejectReason ? { rejectReason } : {}),
  },
})
export const deleteOrder = (id) => http.delete(`${base}/${id}`)

export const createOrder = (data) => http.post('/orders', data)
