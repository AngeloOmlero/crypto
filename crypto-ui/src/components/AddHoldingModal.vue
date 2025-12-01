<template>
  <transition name="fade">
    <div v-if="show" class="fixed inset-0 z-50 flex items-center justify-center p-4">
      <div class="absolute inset-0 bg-black/40 backdrop-blur-sm" @click="$emit('close')"></div>
      <div class="relative w-full max-w-md bg-white rounded-2xl p-6 shadow-lg">
        <div class="flex items-center justify-between">
          <h3 class="text-lg font-semibold">{{ isEditing ? 'Edit Holding' : 'Add Holding' }}</h3>
          <button v-if="isEditing" @click="$emit('cancel-edit')" class="text-sm text-slate-500">Cancel edit</button>
        </div>

        <input
          :value="assetSearchTerm"
          @input="$emit('update:assetSearchTerm', $event.target.value)"
          type="text"
          class="w-full p-3 border rounded-lg mt-4"
          placeholder="Search by name or symbol (fuzzy)..."
          :disabled="isEditing"
        />

        <ul v-if="assetSearchResults.length > 0" class="border rounded-lg mt-2 max-h-40 overflow-auto">
          <li v-for="asset in assetSearchResults" :key="asset.symbol" class="p-2 hover:bg-slate-100 cursor-pointer flex items-center justify-between" @click="$emit('select-asset', asset)">
            <div>
              <div class="font-medium">{{ asset.name }}</div>
              <div class="text-xs text-slate-400">{{ asset.symbol }}</div>
            </div>
            <div class="text-sm text-slate-500">@ {{ formatCurrency(asset.priceUsd) }}</div>
          </li>
        </ul>

        <div v-if="assetSearchError" class="text-xs text-rose-500 mt-1">{{ assetSearchError }}</div>

        <div class="mt-3">
          <label class="text-xs text-slate-500">Selected</label>
          <div class="mt-1 p-2 border rounded-lg" v-if="selectedAsset">
            <div class="font-medium">{{ selectedAsset.name }} ({{ selectedAsset.symbol }})</div>
            <div class="text-xs text-slate-400">@ {{ formatCurrency(selectedAsset.priceUsd) }}</div>
          </div>
          <div class="text-xs text-slate-400" v-else>None selected</div>
        </div>

        <input
          :value="quantity"
          @input="$emit('update:quantity', Number($event.target.value))"
          type="number"
          min="0"
          class="w-full p-3 border rounded-lg mt-4"
          placeholder="Quantity"
        />

        <div class="mt-4 flex justify-between items-center gap-3">
          <div class="text-sm text-slate-500">
            Estimated value: <span class="font-semibold">{{ formatCurrency(estimatedValue) }}</span>
          </div>
          <div class="flex gap-3">
            <button @click="$emit('close')" class="px-4 py-2 rounded-lg border">Cancel</button>
            <button v-if="isEditing" @click="$emit('save')" class="px-4 py-2 rounded-lg bg-emerald-600 text-white">Save</button>
            <button v-else @click="$emit('add')" class="px-4 py-2 rounded-lg bg-sky-600 text-white">Add</button>
          </div>
        </div>
      </div>
    </div>
  </transition>
</template>

<script setup lang="ts">
import { formatCurrency } from '../utils/formatters'
import type { CachedCryptoPrice } from '../types'

defineProps<{
  show: boolean,
  isEditing: boolean,
  assetSearchTerm: string,
  assetSearchResults: CachedCryptoPrice[],
  assetSearchError: string | null,
  selectedAsset: CachedCryptoPrice | null,
  quantity: number,
  estimatedValue: number
}>()

defineEmits(['close', 'cancel-edit', 'update:assetSearchTerm', 'select-asset', 'update:quantity', 'save', 'add'])
</script>
