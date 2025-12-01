<template>
  <header class="bg-slate-50 shadow-sm">
    <div class="max-w-7xl mx-auto p-6 flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4">
      <div class="flex items-center gap-4">
        <div class="text-white flex items-center justify-center text-xl font-bold">
          <img src="/icons8-coin-64.png" alt="CoinWatch Logo" class="w-8 h-8" />
        </div>
        <div>
          <h1 class="text-2xl font-extrabold tracking-tight">CoinWatch — Portfolios</h1>
          <p class="text-xs text-slate-400">Manage portfolios & holdings — values are shown in USD</p>
        </div>
      </div>

      <div class="flex flex-col sm:flex-row items-start sm:items-center gap-3 w-full sm:w-auto">
        <div class="relative w-full sm:w-64">
          <input
            :value="searchTerm"
            @input="$emit('update:searchTerm', $event.target.value)"
            type="search"
            placeholder="Search portfolios or holdings (name or asset)..."
            class="w-full bg-white border rounded-lg px-4 py-2 pr-10 shadow-sm focus:outline-none focus:ring-2 focus:ring-sky-400"
          />
          <svg class="w-4 h-4 absolute right-3 top-1/2 -translate-y-1/2 text-slate-400" viewBox="0 0 24 24" fill="none">
            <path d="M21 21L15 15" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            <circle cx="11" cy="11" r="6" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </div>

        <button @click="$emit('open-create-modal')" class="inline-flex items-center gap-2 bg-emerald-600 hover:bg-emerald-700 text-white px-4 py-2 rounded-lg shadow transition-colors duration-200">
          <svg class="w-4 h-4" viewBox="0 0 24 24" fill="none">
            <path d="M12 5v14M5 12h14" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
          New Portfolio
        </button>
      </div>
    </div>
  </header>
</template>

<script setup lang="ts">
defineProps<{
  searchTerm: string
}>()

defineEmits(['update:searchTerm', 'open-create-modal'])
</script>
