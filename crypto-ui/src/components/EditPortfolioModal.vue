<template>
  <transition name="fade">
    <div v-if="show" class="fixed inset-0 z-50 flex items-center justify-center p-4">
      <div class="absolute inset-0 bg-black/40 backdrop-blur-sm" @click="$emit('close')"></div>
      <div class="relative w-full max-w-lg bg-white rounded-2xl p-6 shadow-lg">
        <h3 class="text-lg font-semibold">Edit Portfolio</h3>
        <input
          :value="name"
          @input="$emit('update:name', $event.target.value)"
          type="text"
          class="w-full p-3 border rounded-lg mt-4"
        />
        <div class="mt-4 flex justify-end gap-3">
          <button @click="$emit('close')" class="px-4 py-2 rounded-lg border">Cancel</button>
          <button @click="$emit('save')" class="px-4 py-2 rounded-lg bg-emerald-600 text-white">Save</button>
        </div>
      </div>
    </div>
  </transition>
</template>

<script setup lang="ts">
defineProps<{
  show: boolean,
  name: string
}>()

defineEmits(['close', 'save', 'update:name'])
</script>
