import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/prices',
      name: 'crypto-prices',
      component: () => import('../views/CryptoPricesView.vue')
    },
    {
      path: '/portfolios',
      name: 'my-portfolios',
      component: () => import('../views/MyPortfoliosView.vue')
    }
  ]
})

export default router
