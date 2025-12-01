<template>
  <div class="overflow-x-auto bg-white rounded-lg shadow">
    <table class="min-w-full table-auto">
      <thead class="bg-gray-100">
        <tr>
          <th class="px-4 py-2">#</th>
          <th class="px-4 py-2">Coin</th>
          <th class="px-4 py-2">Price</th>
          <th class="px-4 py-2">24h</th>
          <th class="px-4 py-2">24h Volume</th>
          <th class="px-4 py-2">Market Cap</th>
        </tr>
      </thead>
      <tbody>
        <tr
          v-for="crypto in prices"
          :key="crypto.symbol"
          class="border-b hover:bg-gray-50 transition"
        >
          <td class="px-4 py-2 text-center">{{ crypto.rank }}</td>
          <td class="px-4 py-2 flex items-center gap-2">
            {{ crypto.name }} <span class="text-gray-400">({{ crypto.symbol }})</span>
          </td>
          <td
            class="px-4 py-2 font-bold transition-colors duration-300 p-1 rounded"
            :class="{
              'bg-green-100 text-green-700': priceChanges[crypto.symbol] === 'up',
              'bg-red-100 text-red-700': priceChanges[crypto.symbol] === 'down'
            }"
          >
            {{ formatCurrency(crypto.priceUsd) }}
          </td>
          <td :class="crypto.changePercent24Hr >= 0 ? 'text-green-600' : 'text-red-600'">
            {{ formatNumber(crypto.changePercent24Hr) }}%
          </td>
          <td>{{ formatCurrency(crypto.volumeUsd24Hr, 0) }}</td>
          <td>{{ formatCurrency(crypto.marketCapUsd, 0) }}</td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script setup lang="ts">
import { formatCurrency, formatNumber } from '../utils/formatters'
import type { CryptoPrice } from '../types'

defineProps<{
  prices: CryptoPrice[]
  priceChanges: Record<string, 'up' | 'down'>
}>()
</script>
