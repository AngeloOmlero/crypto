<template>
  <div class="min-h-screen bg-gray-50 p-6">
    <h1 class="text-3xl font-bold mb-6 text-center">Cryptocurrency Prices by Market Cap</h1>

    <GlobalStats :stats="globalStats" :global-change24h="globalChange24h" :top-gainer="topGainer" />

    <TrendingLists :trending="trendingCoins" :top-gainers="topGainers" />

    <div class="flex justify-end mb-2">
      <label class="mr-2 text-gray-700">Show:</label>
      <select v-model="itemsPerPage" class="border rounded px-2 py-1">
        <option :value="50">50</option>
        <option :value="100">100</option>
        <option :value="300">300</option>
      </select>
    </div>

    <CryptoTable :prices="paginatedCryptoPrices" :price-changes="priceChanges" />

    <Pagination :current-page="currentPage" :total-pages="totalPages" @prev="prevPage" @next="nextPage" />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted, watch } from 'vue'
import axios from 'axios'
import type { CryptoPrice } from '../types'
import GlobalStats from '../components/GlobalStats.vue'
import TrendingLists from '../components/TrendingLists.vue'
import CryptoTable from '../components/CryptoTable.vue'
import Pagination from '../components/Pagination.vue'

// --- State
const cryptoPrices = ref<CryptoPrice[]>([])
const priceChanges = ref<Record<string, 'up' | 'down'>>({})
let refreshInterval: number | undefined

const currentPage = ref(1)
const itemsPerPage = ref(50)

// --- Global stats & top gainer
const globalStats = computed(() => ({
  totalMarketCap: cryptoPrices.value.reduce((a, b) => a + b.marketCapUsd, 0),
  totalVolume: cryptoPrices.value.reduce((a, b) => a + b.volumeUsd24Hr, 0)
}))
const globalChange24h = ref(-3.2) // example
const topGainer = computed(() =>
  cryptoPrices.value.reduce(
    (prev, curr) => (curr.changePercent24Hr > (prev?.changePercent24Hr ?? -Infinity) ? curr : prev),
    cryptoPrices.value[0]
  )
)

// --- Trending & Top Gainers
const trendingCoins = computed(() => cryptoPrices.value.slice(0, 5))
const topGainers = computed(() =>
  [...cryptoPrices.value].sort((a, b) => b.changePercent24Hr - a.changePercent24Hr).slice(0, 5)
)

// --- Pagination
const paginatedCryptoPrices = computed(() => {
  const start = (currentPage.value - 1) * itemsPerPage.value
  return cryptoPrices.value.slice(start, start + itemsPerPage.value)
})
const totalPages = computed(() => Math.ceil(cryptoPrices.value.length / itemsPerPage.value))
const nextPage = () => {
  if (currentPage.value < totalPages.value) currentPage.value++
}
const prevPage = () => {
  if (currentPage.value > 1) currentPage.value--
}
watch(itemsPerPage, () => (currentPage.value = 1))

// --- Watch for price changes for live highlights
const updatePriceChanges = (newPrices: CryptoPrice[], oldPrices: CryptoPrice[]) => {
  const oldMap = new Map(oldPrices.map(c => [c.symbol, c.priceUsd]))
  const changes: Record<string, 'up' | 'down'> = {}
  newPrices.forEach(c => {
    const old = oldMap.get(c.symbol)
    if (old !== undefined && old !== c.priceUsd) {
      changes[c.symbol] = c.priceUsd > old ? 'up' : 'down'
    }
  })
  priceChanges.value = changes
  setTimeout(() => {
    priceChanges.value = {}
  }, 700) // clear highlights after 0.7s
}

// --- Fetch data
const fetchCrypto = async () => {
  try {
    const res = await axios.get<CryptoPrice[]>('/api/crypto/assets')
    if (!res.data) return
    const newData = res.data.sort((a, b) => a.rank - b.rank)

    if (cryptoPrices.value.length > 0) {
      updatePriceChanges(newData, cryptoPrices.value)
      // merge old & new to keep pagination stable
      const map = new Map(newData.map(c => [c.symbol, c]))
      cryptoPrices.value = cryptoPrices.value.map(c => ({ ...c, ...map.get(c.symbol) }))
    } else {
      cryptoPrices.value = newData
    }
  } catch (e) {
    console.error(e)
  }
}

// --- Live updates every 5 seconds
onMounted(() => {
  fetchCrypto()
  refreshInterval = setInterval(fetchCrypto, 5000) as unknown as number
})
onUnmounted(() => {
  if (refreshInterval) clearInterval(refreshInterval)
})
</script>
