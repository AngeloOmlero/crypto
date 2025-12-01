<template>
  <article class="bg-white rounded-2xl shadow hover:shadow-lg transition-shadow duration-200 p-5 flex flex-col">
    <header class="flex items-start justify-between gap-3">
      <div>
        <h2 class="text-lg font-semibold">{{ portfolio.name }}</h2>
        <p class="text-xs text-slate-400">Holdings: {{ portfolio.holdings.length }}</p>
      </div>
      <div class="flex items-center gap-2">
        <button title="Edit" @click="$emit('edit', portfolio)" class="p-2 rounded-md hover:bg-slate-100 transition-colors">
          <svg class="w-5 h-5 text-slate-600" viewBox="0 0 24 24" fill="none">
            <path d="M3 21l3-1 11-11 3 3L10 23 3 21z" stroke="currentColor" stroke-width="1.2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </button>
        <button title="Delete" @click="$emit('delete', portfolio.id)" class="p-2 rounded-md hover:bg-slate-100 transition-colors">
          <svg class="w-5 h-5 text-rose-500" viewBox="0 0 24 24" fill="none">
            <path d="M3 6h18M8 6v12a2 2 0 0 0 2 2h4a2 2 0 0 0 2-2V6" stroke="currentColor" stroke-width="1.2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </button>
      </div>
    </header>

    <!-- Portfolio totals -->
    <div class="mt-4 flex items-baseline justify-between">
      <div>
        <div class="text-sm text-slate-500">Total value</div>
        <div :class="['text-2xl font-bold mt-1 transition-colors', valueClass]">{{ formatCurrency(portfolio.totalValueUsd) }}</div>
      </div>
      <div class="text-right">
        <div class="text-sm text-slate-500">24h change</div>
        <div class="text-sm font-medium mt-1">{{ portfolioChange }}</div>
      </div>
    </div>

    <!-- Holdings preview -->
    <ul class="mt-4 space-y-2 text-sm flex-1 overflow-hidden">
      <li v-for="h in portfolio.holdings.slice(0, 4)" :key="h.id" class="flex items-center justify-between">
        <div class="truncate">
          <div class="font-medium">{{ h.assetId }}</div>
          <div class="text-xs text-slate-400">{{ h.quantity }} units</div>
        </div>
        <div class="text-right">
          <div class="font-semibold">{{ formatCurrency(h.valueUsd) }}</div>
          <div class="text-xs text-slate-400">@ {{ formatCurrency(h.currentPrice) }}</div>
        </div>
      </li>
    </ul>

    <div class="mt-4 flex gap-2">
      <button @click="$emit('add-holding', portfolio.id)" class="flex-1 bg-sky-600 hover:bg-sky-700 text-white py-2 rounded-lg transition-colors duration-200">Add Holding</button>
      <button @click="$emit('view', portfolio)" class="flex-1 border border-slate-200 rounded-lg py-2 hover:bg-slate-50 transition-colors duration-200">View</button>
    </div>
  </article>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { formatCurrency } from '../utils/formatters'
import type { Portfolio } from '../types'

const props = defineProps<{
  portfolio: Portfolio,
  valueChanges: Record<number, 'up' | 'down'>
}>()

defineEmits(['edit', 'delete', 'add-holding', 'view'])

const valueClass = computed(() => {
  const change = props.valueChanges[props.portfolio.id]
  return change === 'up' ? 'text-emerald-600' : change === 'down' ? 'text-rose-500' : ''
})

const portfolioChange = computed(() => {
  const p = props.portfolio
  if (!p.holdings || p.holdings.length === 0) return '0%'
  const change = p.holdings.reduce((acc, h) => {
    const avgPrice = h.quantity ? (h.valueUsd / h.quantity) : h.currentPrice
    return acc + ((h.currentPrice - avgPrice) / (avgPrice || 1)) * 100
  }, 0)
  const avg = change / p.holdings.length
  return `${avg >= 0 ? '+' : ''}${avg.toFixed(2)}%`
})
</script>
