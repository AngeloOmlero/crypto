<template>
  <div id="app" class="min-h-screen bg-[var(--color-background)] font-sans">
    <!-- Sticky Header -->
    <header class="sticky top-0 z-50 bg-white shadow-md">
      <div
        class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 flex justify-between items-center h-16"
      >
        <!-- Logo -->
        <RouterLink
          to="/prices"
          class="flex items-center gap-2 no-underline hover:no-underline"
        >
          <img src="/icons8-coin-64.png" alt="CoinWatch Logo" class="w-8 h-8" />
          <span class="text-2xl font-bold text-black">CoinWatch</span>
        </RouterLink>


        <!-- Mobile Menu Button -->
        <button
          @click="mobileMenuOpen = !mobileMenuOpen"
          class="md:hidden p-2 rounded-md text-[var(--color-text-dark)] hover:bg-gray-100 transition-colors"
        >
          <svg
            v-if="!mobileMenuOpen"
            xmlns="http://www.w3.org/2000/svg"
            class="h-6 w-6"
            fill="none"
            viewBox="0 0 24 24"
            stroke="currentColor"
          >
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6h16M4 12h16M4 18h16"/>
          </svg>
          <svg
            v-else
            xmlns="http://www.w3.org/2000/svg"
            class="h-6 w-6"
            fill="none"
            viewBox="0 0 24 24"
            stroke="currentColor"
          >
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"/>
          </svg>
        </button>

        <!-- Navigation -->
        <nav
          :class="['flex-col md:flex-row md:flex space-y-2 md:space-y-0 md:space-x-4 absolute md:static bg-white md:bg-transparent w-full md:w-auto left-0 md:left-auto top-16 md:top-auto transition-all duration-300', mobileMenuOpen ? 'flex' : 'hidden']"
        >
          <RouterLink
            to="/prices"
            class="px-4 py-2 rounded-md text-base font-medium transition-colors duration-200 no-underline hover:no-underline"
            :class="$route.path === '/' ? 'bg-[var(--color-primary)] text-white' : 'text-[var(--color-text-dark)] hover:bg-gray-100'"
          >
            Crypto Prices
          </RouterLink>
          <RouterLink
            to="/portfolios"
            class="px-4 py-2 rounded-md text-base font-medium transition-colors duration-200 no-underline hover:no-underline"
            :class="$route.path === '/portfolios' ? 'bg-[var(--color-primary)] text-white' : 'text-[var(--color-text-dark)] hover:bg-gray-100'"
          >
            Portfolio
          </RouterLink>
        </nav>
      </div>
    </header>

    <!-- Main Content -->
    <main class="max-w-7xl mx-auto py-8 px-4 sm:px-6 lg:px-8">
      <RouterView />
    </main>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'

const mobileMenuOpen = ref(false)
</script>

<style scoped>
/* Make sure mobile menu transitions nicely */
@media (min-width: 768px) {
  nav.absolute { display: flex !important; }
}
</style>
