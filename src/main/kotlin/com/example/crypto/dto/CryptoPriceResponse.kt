package com.example.crypto.dto

data class AssetDto(
    val id: String,
    val rank: String?, // Changed to String, can be null
    val symbol: String,
    val name: String,
    val supply: String?, // Can be null
    val maxSupply: String?, // Can be null
    val marketCapUsd: String?, // Can be null
    val volumeUsd24Hr: String?, // Can be null
    val priceUsd: String?, // Can be null
    val changePercent24Hr: String?, // Can be null
    val vwap24Hr: String?, // Can be null
    val explorer: String? // Optional field
)
