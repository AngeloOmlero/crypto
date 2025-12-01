<template>
  <div class="flex justify-center items-center gap-4 mt-4">
    <button
      @click="$emit('prev')"
      :disabled="currentPage === 1"
      class="px-3 py-1 bg-gray-200 rounded disabled:opacity-50 hover:bg-gray-300 transition"
    >
      Previous
    </button>
    <span>Page {{ currentPage }} of {{ totalPages }}</span>
    <button
      @click="$emit('next')"
      :disabled="currentPage === totalPages"
      class="px-3 py-1 bg-gray-200 rounded disabled:opacity-50 hover:bg-gray-300 transition"
    >
      Next
    </button>
  </div>
</template>

<script setup lang="ts">
defineProps<{
  currentPage: number
  totalPages: number
}>()

defineEmits(['prev', 'next'])
</script>
