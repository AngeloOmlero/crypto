<template>
  <div class="min-h-screen bg-slate-50 text-slate-800">
    <PortfoliosHeader
      :search-term="portfolioSearchTerm"
      @update:search-term="portfolioSearchTerm = $event"
      @open-create-modal="openCreateModal"
    />

    <main class="max-w-7xl mx-auto p-6">
      <section class="grid lg:grid-cols-4 md:grid-cols-3 grid-cols-1 gap-6">
        <PortfoliosSidebar
          :portfolio-count="portfolios.length"
          :total-value="totalValue"
          :last-refreshed="lastRefreshed"
        />

        <PortfolioList
          :portfolios="filteredPortfolios"
          :loading="loading"
          :error="error"
          :value-changes="valueChanges"
          @edit="openEditModal"
          @delete="confirmAndDeletePortfolio"
          @add-holding="openAddHoldingModal"
          @view="viewPortfolio"
        />
      </section>

      <CreatePortfolioModal
        :show="showCreate"
        :name="newPortfolioName"
        @update:name="newPortfolioName = $event"
        @close="closeCreate"
        @create="createPortfolio"
      />

      <EditPortfolioModal
        :show="showEdit"
        :name="editingPortfolioName"
        @update:name="editingPortfolioName = $event"
        @close="closeEdit"
        @save="updatePortfolioName"
      />

      <AddHoldingModal
        :show="showAddHolding"
        :is-editing="isEditingHolding"
        :asset-search-term="assetSearchTerm"
        @update:asset-search-term="assetSearchTerm = $event"
        :asset-search-results="assetSearchResults"
        :asset-search-error="assetSearchError"
        :selected-asset="selectedAsset"
        :quantity="newHoldingQuantity"
        @update:quantity="newHoldingQuantity = $event"
        :estimated-value="estimatedHoldingValue"
        @close="closeAddHolding"
        @cancel-edit="cancelEditHolding"
        @select-asset="selectAsset"
        @save="submitEditHolding"
        @add="submitAddHolding"
      />

      <ViewPortfolioModal
        :show="showView"
        :portfolio="viewingPortfolio"
        @close="closeView"
        @add-holding="openAddHoldingModal"
        @edit-holding="startEditHolding"
        @delete-holding="confirmAndDeleteHolding"
      />
    </main>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted, onUnmounted } from 'vue'
import axios from 'axios'
import type { Portfolio, Holding, CachedCryptoPrice } from '../types'
import { formatCurrency } from '../utils/formatters'

import PortfoliosHeader from '../components/PortfoliosHeader.vue'
import PortfoliosSidebar from '../components/PortfoliosSidebar.vue'
import PortfolioList from '../components/PortfolioList.vue'
import CreatePortfolioModal from '../components/CreatePortfolioModal.vue'
import EditPortfolioModal from '../components/EditPortfolioModal.vue'
import AddHoldingModal from '../components/AddHoldingModal.vue'
import ViewPortfolioModal from '../components/ViewPortfolioModal.vue'

// ---------------- State ----------------
const portfolios = ref<Portfolio[]>([])
const loading = ref(true)
const error = ref<string | null>(null)

// Modals
const showCreate = ref(false)
const showEdit = ref(false)
const showAddHolding = ref(false)
const showView = ref(false)

// Portfolio create/edit
const newPortfolioName = ref('')
const editingPortfolioId = ref<number | null>(null)
const editingPortfolioName = ref('')

// Holding create/edit
const currentPortfolioForHolding = ref<number | null>(null)
const newHoldingQuantity = ref<number>(0)
const newHoldingAssetId = ref<string>('')
const selectedAsset = ref<CachedCryptoPrice | null>(null)
const isEditingHolding = ref(false)
const editingHoldingId = ref<number | null>(null)

// Search & assets
const portfolioSearchTerm = ref('')
const assetSearchTerm = ref('')
const assetSearchResults = ref<CachedCryptoPrice[]>([])
const assetSearchError = ref<string | null>(null)
const cachedAssets = ref<CachedCryptoPrice[]>([]) // cache of all assets for fuzzy

// View state
const viewingPortfolio = ref<Portfolio | null>(null)

// timers
let refreshTimer: number | undefined
let assetsRefreshTimer: number | undefined

// UI highlight on change
const valueChanges = ref<Record<number, 'up' | 'down'>>({})

// ---------------- Helpers ----------------
const debounce = (fn: Function, delay = 250) => {
  let t: ReturnType<typeof setTimeout> | null = null
  return (...args: any[]) => {
    if (t) clearTimeout(t)
    t = setTimeout(() => fn(...args), delay)
  }
}

const lastRefreshed = computed(() => new Date().toLocaleTimeString())
const totalValue = computed(() => portfolios.value.reduce((acc, p) => acc + (p.totalValueUsd || 0), 0))

// ---------------- API calls ----------------
const fetchPortfolios = async (initial = false) => {
  if (initial) loading.value = true
  error.value = null
  try {
    const res = await axios.get<Portfolio[]>('/api/crypto/portfolios')
    // normalize array
    const raw = Array.isArray(res.data) ? res.data : []
    // If backend already returns currentPrice/valueUsd, use them; otherwise compute from cached assets
    portfolios.value = raw.map(p => ({ ...p }))
    // compute derived prices & totals client-side
    computeAllPortfolioValues()
  } catch (err) {
    console.error(err)
    error.value = 'Failed to fetch portfolios. Check backend.'
  } finally {
    if (initial) loading.value = false
  }
}

const createPortfolio = async () => {
  const name = newPortfolioName.value.trim()
  if (!name) return
  try {
    await axios.post('/api/crypto/portfolios', { name })
    newPortfolioName.value = ''
    showCreate.value = false
    await fetchPortfolios(true)
  } catch (err) {
    console.error(err)
    error.value = 'Failed to create portfolio.'
  }
}

const updatePortfolioName = async () => {
  if (!editingPortfolioId.value) return
  const name = editingPortfolioName.value.trim()
  if (!name) return
  try {
    // backend expects raw string body
    await axios.put(`/api/crypto/portfolios/${editingPortfolioId.value}`, name, {
      headers: { 'Content-Type': 'application/json' }
    })
    const p = portfolios.value.find(x => x.id === editingPortfolioId.value)
    if (p) p.name = name
    closeEdit()
  } catch (err) {
    console.error(err)
    error.value = 'Failed to update portfolio.'
  }
}

const deletePortfolio = async (id: number) => {
  try {
    await axios.delete(`/api/crypto/portfolios/${id}`)
    portfolios.value = portfolios.value.filter(p => p.id !== id)
  } catch (err) {
    console.error(err)
    error.value = 'Failed to delete portfolio.'
  }
}

// holdings endpoints
const submitAddHolding = async () => {
  if (!currentPortfolioForHolding.value) return
  if (!selectedAsset.value) {
    assetSearchError.value = 'Please select an asset.'
    return
  }
  if (!newHoldingQuantity.value || newHoldingQuantity.value <= 0) {
    assetSearchError.value = 'Enter a valid quantity'
    return
  }
  try {
    await axios.post(`/api/crypto/portfolios/${currentPortfolioForHolding.value}/holdings`, {
      assetId: selectedAsset.value.symbol,
      quantity: newHoldingQuantity.value
    })
    await fetchPortfolios(true)
    closeAddHolding()
  } catch (err) {
    console.error(err)
    assetSearchError.value = 'Failed to add holding.'
  }
}

const submitEditHolding = async () => {
  if (!currentPortfolioForHolding.value || !editingHoldingId.value) return
  if (!newHoldingQuantity.value || newHoldingQuantity.value <= 0) {
    assetSearchError.value = 'Enter a valid quantity'
    return
  }
  try {
    await axios.put(
      `/api/crypto/portfolios/${currentPortfolioForHolding.value}/holdings/${editingHoldingId.value}`,
      { quantity: newHoldingQuantity.value }
    )
    await fetchPortfolios(true)
    closeAddHolding()
  } catch (err) {
    console.error(err)
    assetSearchError.value = 'Failed to update holding.'
  }
}

const deleteHolding = async (portfolioId: number, holdingId: number) => {
  try {
    await axios.delete(`/api/crypto/portfolios/${portfolioId}/holdings/${holdingId}`)
    await fetchPortfolios(true)
  } catch (err) {
    console.error(err)
    error.value = 'Failed to delete holding.'
  }
}

// fetch cached assets used for fuzzy search & price lookup
const fetchAllAssets = async () => {
  try {
    const res = await axios.get<CachedCryptoPrice[]>('/api/crypto/assets')
    cachedAssets.value = Array.isArray(res.data) ? res.data : []
    // once assets loaded, we can compute values in portfolios
    computeAllPortfolioValues()
  } catch (err) {
    console.error(err)
    // non-fatal; show message when searching if empty
  }
}

// ---------------- Compute derived prices & totals ----------------

// helper to map symbol -> price quickly
const buildAssetPriceMap = () => {
  const map = new Map<string, CachedCryptoPrice>()
  for (const a of cachedAssets.value) map.set(a.symbol.toUpperCase(), a)
  return map
}

// calculate currentPrice and valueUsd for all holdings and portfolio totals
function computeAllPortfolioValues() {
  const map = buildAssetPriceMap()
  const oldTotals = new Map(portfolios.value.map(p => [p.id, p.totalValueUsd || 0]))

  portfolios.value = portfolios.value.map(p => {
    const updatedHoldings = (p.holdings || []).map(h => {
      // find asset in cache (symbol matching case-insensitive)
      const asset = map.get((h.assetId || '').toUpperCase())
      const currentPrice = asset ? asset.priceUsd : (h.currentPrice ?? 0)
      const valueUsd = (currentPrice || 0) * (h.quantity || 0)
      return {
        ...h,
        currentPrice,
        valueUsd
      }
    })
    const totalValueUsd = updatedHoldings.reduce((acc, hh) => acc + (hh.valueUsd || 0), 0)
    return {
      ...p,
      holdings: updatedHoldings,
      totalValueUsd
    }
  })

  // compute change highlights
  const newTotals = new Map(portfolios.value.map(p => [p.id, p.totalValueUsd || 0]))
  const changes: Record<number, 'up' | 'down'> = {}
  portfolios.value.forEach(p => {
    const old = oldTotals.get(p.id)
    if (old !== undefined && p.totalValueUsd !== old) changes[p.id] = p.totalValueUsd > old ? 'up' : 'down'
  })
  valueChanges.value = changes
  // clear highlights shortly
  setTimeout(() => (valueChanges.value = {}), 800)
}

// ---------------- Fuzzy search (client-side) ----------------
// Simple fuzzy function (subsequence) and debounced search
function fuzzyMatch(text = '', pattern = '') {
  text = text.toLowerCase()
  pattern = pattern.toLowerCase()
  let ti = 0
  let pi = 0
  while (ti < text.length && pi < pattern.length) {
    if (text[ti] === pattern[pi]) pi++
    ti++
  }
  return pi === pattern.length
}

const doAssetFuzzySearch = debounce((term = '') => {
  assetSearchError.value = null
  const q = term.trim().toLowerCase()
  if (!q) {
    assetSearchResults.value = []
    return
  }

  // find matches by symbol or name with fuzzyMatch
  const res = cachedAssets.value.filter(a =>
    fuzzyMatch(a.symbol, q) || fuzzyMatch(a.name, q) || a.symbol.toLowerCase().includes(q) || a.name.toLowerCase().includes(q)
  )

  if (res.length === 0) {
    // fallback: partial token inclusion
    const parts = q.split(/\s+/).filter(Boolean)
    const fallback = cachedAssets.value.filter(a => parts.every(p => a.name.toLowerCase().includes(p) || a.symbol.toLowerCase().includes(p)))
    assetSearchResults.value = fallback.slice(0, 50)
  } else {
    assetSearchResults.value = res.slice(0, 50)
  }

  if (assetSearchResults.value.length === 0) assetSearchError.value = 'No matching assets.'
}, 200)

// ---------------- UI / Modal helpers ----------------
const openCreateModal = () => {
  newPortfolioName.value = ''
  showCreate.value = true
}

const closeCreate = () => (showCreate.value = false)

const openEditModal = (p: Portfolio) => {
  editingPortfolioId.value = p.id
  editingPortfolioName.value = p.name
  showEdit.value = true
}

const closeEdit = () => {
  showEdit.value = false
  editingPortfolioId.value = null
  editingPortfolioName.value = ''
}

const confirmAndDeletePortfolio = (id: number) => {
  if (!confirm('Delete this portfolio?')) return
  deletePortfolio(id)
}

const confirmAndDeleteHolding = (portfolioId: number, holdingId: number) => {
  if (!confirm('Delete this holding?')) return
  deleteHolding(portfolioId, holdingId)
}

const openAddHoldingModal = (portfolioId: number) => {
  currentPortfolioForHolding.value = portfolioId
  selectedAsset.value = null
  newHoldingAssetId.value = ''
  newHoldingQuantity.value = 0
  assetSearchTerm.value = ''
  assetSearchResults.value = []
  assetSearchError.value = null
  isEditingHolding.value = false
  editingHoldingId.value = null
  showAddHolding.value = true
}

const closeAddHolding = () => {
  showAddHolding.value = false
  selectedAsset.value = null
  currentPortfolioForHolding.value = null
  newHoldingQuantity.value = 0
  newHoldingAssetId.value = ''
  assetSearchTerm.value = ''
  assetSearchResults.value = []
  assetSearchError.value = null
  isEditingHolding.value = false
  editingHoldingId.value = null
}

const viewPortfolio = (p: Portfolio) => {
  viewingPortfolio.value = p
  showView.value = true
}

const closeView = () => {
  showView.value = false
  viewingPortfolio.value = null
}

const selectAsset = (asset: CachedCryptoPrice) => {
  selectedAsset.value = asset
  newHoldingAssetId.value = asset.symbol
  assetSearchTerm.value = asset.symbol
  assetSearchResults.value = []
  assetSearchError.value = null
}

// start editing an existing holding
const startEditHolding = (portfolioId: number, h: Holding) => {
  currentPortfolioForHolding.value = portfolioId
  editingHoldingId.value = h.id
  isEditingHolding.value = true
  // try to find the asset in cache
  const asset = cachedAssets.value.find(a => a.symbol.toUpperCase() === h.assetId.toUpperCase())
  selectedAsset.value = asset ?? { symbol: h.assetId, name: h.assetId, priceUsd: h.currentPrice }
  newHoldingQuantity.value = h.quantity
  assetSearchTerm.value = h.assetId
  assetSearchResults.value = []
  showAddHolding.value = true
}

// cancel editing holding back to add mode
const cancelEditHolding = () => {
  isEditingHolding.value = false
  editingHoldingId.value = null
  newHoldingQuantity.value = 0
  selectedAsset.value = null
  assetSearchTerm.value = ''
  assetSearchResults.value = []
}

// ---------------- Watchers & computed ----------------
watch(assetSearchTerm, (v) => {
  doAssetFuzzySearch(v)
})

// filtered portfolios for the grid search box (searches portfolio name and holdings' assetId)
const filteredPortfolios = computed(() => {
  const term = portfolioSearchTerm.value.trim().toLowerCase()
  if (!term) return portfolios.value
  return portfolios.value.filter(p =>
    p.name.toLowerCase().includes(term) ||
    p.holdings.some(h => (h.assetId || '').toLowerCase().includes(term))
  )
})

// estimate value shown in add/edit holding modal
const estimatedHoldingValue = computed(() => {
  const price = selectedAsset.value ? selectedAsset.value.priceUsd : 0
  const qty = newHoldingQuantity.value || 0
  return (price || 0) * qty
})

// highlight value changes when portfolios change
watch(portfolios, (newP, oldP) => {
  const oldMap = new Map(oldP.map(pp => [pp.id, pp.totalValueUsd || 0]))
  const changes: Record<number, 'up' | 'down'> = {}
  newP.forEach(pp => {
    const old = oldMap.get(pp.id)
    if (old !== undefined && pp.totalValueUsd !== old) changes[pp.id] = pp.totalValueUsd > old ? 'up' : 'down'
  })
  valueChanges.value = changes
  setTimeout(() => (valueChanges.value = {}), 800)
}, { deep: false })

// ---------------- Lifecycle ----------------
onMounted(async () => {
  loading.value = true
  await fetchAllAssetsAndPortfoliosOnce()
  // refresh portfolios every 5s
  refreshTimer = window.setInterval(() => fetchPortfolios(), 5000)
  // refresh assets every 60s to keep prices fresh for fuzzy and calculations
  assetsRefreshTimer = window.setInterval(() => fetchAllAssets(), 60_000)
})

onUnmounted(() => {
  if (refreshTimer) clearInterval(refreshTimer)
  if (assetsRefreshTimer) clearInterval(assetsRefreshTimer)
})

// helper to fetch both on start
async function fetchAllAssetsAndPortfoliosOnce() {
  await fetchAllAssets()
  await fetchPortfolios(true)
}
</script>

<style scoped>
.fade-enter-active, .fade-leave-active { transition: opacity 0.2s; }
.fade-enter-from, .fade-leave-to { opacity: 0; }

/* small responsive tweaks */
@media (max-width: 640px) {
  header .max-w-7xl { padding: 1rem; }
}
</style>
