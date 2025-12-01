<template>
  <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4 mb-8">
    <div class="bg-white p-4 rounded-lg shadow">
      <p class="text-gray-500 text-sm">Market Cap</p>
      <p class="text-xl font-bold">{{ formatCurrency(stats.totalMarketCap, 0) }}</p>
      <p :class="globalChange24h >= 0 ? 'text-green-600' : 'text-red-600'">
        {{ globalChange24h >= 0 ? '▲' : '▼' }} {{ formatNumber(globalChange24h) }}%
      </p>
    </div>
    <div class="bg-white p-4 rounded-lg shadow">
      <p class="text-gray-500 text-sm">24h Trading Volume</p>
      <p class="text-xl font-bold">{{ formatCurrency(stats.totalVolume, 0) }}</p>
    </div>
    <div class="bg-white p-4 rounded-lg shadow">
      <p class="text-gray-500 text-sm">Top Gainer</p>
      <p class="text-xl font-bold">{{ topGainer?.name }} ({{ topGainer?.symbol }})</p>
      <p class="text-green-600 font-semibold">
        ▲ {{ formatNumber(topGainer?.changePercent24Hr) }}%
      </p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { formatCurrency, formatNumber } from '../utils/formatters'
import type { CryptoPrice } from '../types'

defineProps<{
  stats: { totalMarketCap: number; totalVolume: number }
  globalChange24h: number
  topGainer: CryptoPrice | undefined
}>()
</script>
