import { ref } from 'vue'
import { ElMessage } from 'element-plus'

export function usePaginatedList({ query, fetcher, errorMessage }) {
  const loading = ref(false)
  const list = ref([])
  const total = ref(0)

  let activeRequestId = 0

  async function load() {
    const requestId = ++activeRequestId
    loading.value = true

    try {
      const res = await fetcher({ ...query })

      if (requestId !== activeRequestId) return

      const nextList = res?.data?.items || []
      const nextTotal = Number(res?.data?.total || 0)

      // If the current page becomes empty after delete/filter changes,
      // move back to the last valid page instead of leaving the table blank.
      if (!nextList.length && nextTotal > 0 && query.page > 1) {
        const maxPage = Math.max(1, Math.ceil(nextTotal / query.size))
        if (maxPage !== query.page) {
          query.page = maxPage
          return await load()
        }
      }

      list.value = nextList
      total.value = nextTotal
    } catch (error) {
      if (requestId !== activeRequestId) return
      ElMessage.error(error?.response?.data?.message || errorMessage)
    } finally {
      if (requestId === activeRequestId) {
        loading.value = false
      }
    }
  }

  function search() {
    query.page = 1
    return load()
  }

  return {
    loading,
    list,
    total,
    load,
    search,
  }
}
