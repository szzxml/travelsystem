import { defineStore } from 'pinia'

export const useOrderStore = defineStore('order', {
  state: () => ({
    latestOrder: null,
    myOrders: [],
    total: 0,
  }),
  actions: {
    setLatestOrder(order) {
      this.latestOrder = order
    },
    setMyOrders(pageResult) {
      this.myOrders = pageResult?.items || []
      this.total = pageResult?.total || 0
    },
    clear() {
      this.latestOrder = null
      this.myOrders = []
      this.total = 0
    },
  },
})
