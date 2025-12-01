package com.example.crypto.repository

import com.example.crypto.model.CachedCryptoPrice
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface CachedCryptoPriceRepository : JpaRepository<CachedCryptoPrice, String> {
    @Query("SELECT c FROM CachedCryptoPrice c WHERE lower(c.symbol) = lower(?1)")
    fun findBySymbolIgnoreCase(symbol: String): CachedCryptoPrice?
}
