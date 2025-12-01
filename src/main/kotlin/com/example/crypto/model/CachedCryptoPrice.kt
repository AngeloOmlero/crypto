package com.example.crypto.model

import jakarta.persistence.Entity
import jakarta.persistence.Id
import java.time.Instant

@Entity
data class CachedCryptoPrice(
    @Id
    var symbol: String, // Renamed from assetSymbol
    var rank: String?, // Made nullable
    var name: String,
    var supply: Double,
    var maxSupply: Double?, // Can be null
    var marketCapUsd: Double, // Renamed from marketCap
    var volumeUsd24Hr: Double, // Renamed from volume24h
    var priceUsd: Double,
    var changePercent24Hr: Double,
    var lastUpdated: Instant
)
