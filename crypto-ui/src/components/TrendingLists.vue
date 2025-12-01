<template>
  <div class="grid grid-cols-1 sm:grid-cols-2 gap-4 mb-6">
    <div class="bg-white p-4 rounded-lg shadow">
      <p class="text-gray-500 text-sm font-semibold mb-2">Trending Coins</p>
      <ul>
        <li v-for="coin in trending" :key="coin.symbol" class="flex justify-between mb-1">
          <span>{{ coin.name }} ({{ coin.symbol }})</span>
          <span :class="coin.changePercent24Hr >= 0 ? 'text-green-600' : 'text-red-600'">
            {{ coin.changePercent24Hr >= 0 ? '▲' : '▼' }} {{ formatNumber(coin.changePercent24Hr) }}%
          </span>
        </li>
      </ul>
    </div>
    <div class="bg-white p-4 rounded-lg shadow">
      <p class="text-gray-500 text-sm font-semibold mb-2">Top Gainers</p>
      <ul>
        <li v-for="coin in topGainers" :key="coin.symbol" class="flex justify-between mb-1">
          <span>{{ coin.name }} ({{ coin.symbol }})</span>
          <span class="text-green-600">
            ▲ {{ formatNumber(coin.changePercent24Hr) }}%
          </span>
        </li>
      </ul>
    </div>
  </div>
</template>

<script setup lang="ts">
import { formatNumber } from '../utils/formatters'
import type { CryptoPrice } from '../types'

defineProps<{
  trending: CryptoPrice[]
  topGainers: CryptoPrice[]
}>()
</script>
