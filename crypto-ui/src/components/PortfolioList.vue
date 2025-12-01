<template>
  <div class="lg:col-span-3 md:col-span-2 col-span-1">
    <!-- loading skeleton -->
    <div v-if="loading" class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
      <div v-for="n in 6" :key="n" class="animate-pulse bg-white rounded-2xl p-6 h-40"></div>
    </div>

    <div v-else>
      <div v-if="error" class="bg-rose-50 text-rose-700 rounded-lg p-4 mb-4">{{ error }}</div>

      <div v-if="portfolios.length === 0" class="bg-white rounded-2xl p-8 text-center text-slate-500">
        No portfolios found — create your first one.
      </div>

      <div class="grid sm:grid-cols-2 lg:grid-cols-3 gap-6">
        <PortfolioCard
          v-for="portfolio in portfolios"
          :key="portfolio.id"
          :portfolio="portfolio"
          :value-changes="valueChanges"
          @edit="$emit('edit', $event)"
          @delete="$emit('delete', $event)"
          @add-holding="$emit('add-holding', $event)"
          @view="$emit('view', $event)"
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import PortfolioCard from './PortfolioCard.vue'
import type { Portfolio } from '../types'

defineProps<{
  portfolios: Portfolio[],
  loading: boolean,
  error: string | null,
  valueChanges: Record<number, 'up' | 'down'>
}>()

defineEmits(['edit', 'delete', 'add-holding', 'view'])
</script>
