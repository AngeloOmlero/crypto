export interface Holding {
  id: number
  assetId: string
  quantity: number
  currentPrice: number
  valueUsd: number
}

export interface Portfolio {
  id: number
  name: string
  userId: number
  holdings: Holding[]
  totalValueUsd: number
}

export interface CachedCryptoPrice {
  symbol: string
  name: string
  priceUsd: number
  changePercent24Hr?: number
}

export interface CryptoPrice {
  symbol: string
  rank: number
  name: string
  priceUsd: number
  changePercent24Hr: number
  volumeUsd24Hr: number
  marketCapUsd: number
}
