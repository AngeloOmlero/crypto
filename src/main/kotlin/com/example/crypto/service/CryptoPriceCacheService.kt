package com.example.crypto.service

import com.example.crypto.client.CoinCapClient
import com.example.crypto.model.CachedCryptoPrice
import com.example.crypto.repository.CachedCryptoPriceRepository
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class CryptoPriceCacheService(
    private val coinCapClient: CoinCapClient,
    private val cacheRepo: CachedCryptoPriceRepository
) {

    @Scheduled(fixedRate = 60000)
    fun refreshCache() {
        val prices = coinCapClient.getAllAssets()
        val now = Instant.now()

        prices.forEach { assetDto ->
            val cached = CachedCryptoPrice(
                symbol = assetDto.symbol,
                rank = assetDto.rank,
                name = assetDto.name,
                supply = assetDto.supply?.toDoubleOrNull() ?: 0.0,
                maxSupply = assetDto.maxSupply?.toDoubleOrNull(),
                marketCapUsd = assetDto.marketCapUsd?.toDoubleOrNull() ?: 0.0,
                volumeUsd24Hr = assetDto.volumeUsd24Hr?.toDoubleOrNull() ?: 0.0,
                priceUsd = assetDto.priceUsd?.toDoubleOrNull() ?: 0.0,
                changePercent24Hr = assetDto.changePercent24Hr?.toDoubleOrNull() ?: 0.0,
                lastUpdated = now
            )
            cacheRepo.save(cached)
        }
    }

    fun getAllCachedPrices(): List<CachedCryptoPrice> {
        return cacheRepo.findAll()
    }

    fun getCachedPriceBySymbol(symbol: String): CachedCryptoPrice? {
        return cacheRepo.findBySymbolIgnoreCase(symbol)
    }
}
