<template>
  <aside class="col-span-1 md:col-span-1 lg:col-span-1">
    <div class="bg-white rounded-2xl shadow p-5 sticky top-6">
      <h3 class="text-sm text-slate-500">Total Portfolios</h3>
      <div class="flex items-baseline justify-between mt-2">
        <div>
          <div class="text-3xl font-bold">{{ portfolioCount }}</div>
          <div class="text-sm text-slate-400">Active portfolios</div>
        </div>
        <div class="text-right">
          <div class="text-base font-semibold">{{ formatCurrency(totalValue) }}</div>
          <div class="text-xs text-slate-400">Total market value</div>
        </div>
      </div>

      <div class="mt-4 border-t pt-4 space-y-3">
        <div class="flex items-center justify-between text-sm text-slate-600">
          <span>Last refresh</span>
          <span>{{ lastRefreshed }}</span>
        </div>
        <div class="flex items-center justify-between text-sm text-slate-600">
          <span>Auto-refresh</span>
          <span>5s</span>
        </div>
      </div>
    </div>
  </aside>
</template>

<script setup lang="ts">
import { formatCurrency } from '../utils/formatters';

defineProps<{
  portfolioCount: number,
  totalValue: number,
  lastRefreshed: string,
}>()
</script>
