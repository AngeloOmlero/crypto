<template>
  <transition name="fade">
    <div v-if="show" class="fixed inset-0 z-40 flex items-center justify-center p-4 overflow-auto">
      <div class="absolute inset-0 bg-black/40 backdrop-blur-sm" @click="$emit('close')"></div>
      <div class="relative w-full max-w-3xl bg-white rounded-2xl p-6 shadow-lg">
        <div class="flex items-start justify-between">
          <h3 class="text-lg font-semibold mb-4">{{ portfolio?.name }}</h3>
        </div>

        <div class="grid sm:grid-cols-2 gap-4">
          <div v-for="h in portfolio?.holdings || []" :key="h.id" class="bg-slate-50 rounded-xl p-4 shadow flex flex-col justify-between">
            <div>
              <div class="flex items-center justify-between">
                <div>
                  <div class="font-semibold">{{ h.assetId }}</div>
                  <div class="text-sm text-slate-500">{{ h.quantity }} units</div>
                </div>
                <div class="text-right">
                  <div class="font-semibold">{{ formatCurrency(h.valueUsd) }}</div>
                  <div class="text-xs text-slate-400">@ {{ formatCurrency(h.currentPrice) }}</div>
                </div>
              </div>
            </div>
            <div class="mt-3 flex items-center justify-between gap-2">
              <div class="text-xs text-slate-500">Actions</div>
              <div class="flex gap-2">
                <button @click="$emit('edit-holding', portfolio.id, h)" class="px-2 py-1 rounded border text-sm">Edit</button>
                <button @click="$emit('delete-holding', portfolio.id, h.id)" class="px-2 py-1 rounded border text-sm text-rose-600">Delete</button>
              </div>
            </div>
          </div>
        </div>

        <div class="mt-4 flex justify-end">
          <button @click="$emit('close')" class="px-4 py-2 rounded-lg border">Close</button>
        </div>
      </div>
    </div>
  </transition>
</template>

<script setup lang="ts">
import { formatCurrency } from '../utils/formatters'
import type { Portfolio, Holding } from '../types'

defineProps<{
  show: boolean,
  portfolio: Portfolio | null
}>()

defineEmits(['close', 'add-holding', 'edit-holding', 'delete-holding'])
</script>
