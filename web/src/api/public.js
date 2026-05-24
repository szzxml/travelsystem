import http from './http'

export const getPublicRoutes = (params) => http.get('/public/routes', { params })
export const getPublicRoute = (id) => http.get(`/public/routes/${id}`)
export const getPublicAttractions = (params) => http.get('/public/attractions', { params })
export const getPublicAttraction = (id) => http.get(`/public/attractions/${id}`)
export const getPublicNotices = (params) => http.get('/public/notices', { params })
