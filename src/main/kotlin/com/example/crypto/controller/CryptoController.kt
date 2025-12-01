package com.example.crypto.controller

import com.example.crypto.client.CoinCapClient
import com.example.crypto.dto.AddHoldingRequest
import com.example.crypto.dto.CreatePortfolioRequest
import com.example.crypto.dto.UpdateHoldingRequest
import com.example.crypto.model.Portfolio
import com.example.crypto.model.CachedCryptoPrice
import com.example.crypto.service.PortfolioService
import com.example.crypto.service.CryptoPriceCacheService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/crypto")
class CryptoController(
    private val portfolioService: PortfolioService,
    private val coinCapClient: CoinCapClient, // Keep if other methods still need direct access, otherwise remove
    private val cryptoPriceCacheService: CryptoPriceCacheService // Inject the new cache service
) {
    // Placeholder for userId until authentication is implemented
    private val currentUserId: Long = 1L

    @PostMapping("/portfolios")
    fun createPortfolio(@RequestBody request: CreatePortfolioRequest): ResponseEntity<Portfolio> {
        val portfolio = portfolioService.createPortfolio(request.name, currentUserId)
        return if (portfolio != null) {
            ResponseEntity(portfolio, HttpStatus.CREATED)
        } else {
            ResponseEntity(HttpStatus.BAD_REQUEST) // Or a more specific error if user not found
        }
    }

    @GetMapping("/portfolios/{id}")
    fun getPortfolioById(@PathVariable id: Long): ResponseEntity<Portfolio> {
        val portfolio = portfolioService.getPortfolioById(id, currentUserId)
        return if (portfolio != null) {
            ResponseEntity(portfolio, HttpStatus.OK)
        } else {
            ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

    @GetMapping("/portfolios")
    fun getAllPortfolios(): ResponseEntity<List<Portfolio>> {
        val portfolios = portfolioService.getAllPortfolios(currentUserId)
        return ResponseEntity(portfolios, HttpStatus.OK)
    }

    @PutMapping("/portfolios/{id}")
    fun updatePortfolioName(@PathVariable id: Long, @RequestBody newName: String): ResponseEntity<Portfolio> {
        val updatedPortfolio = portfolioService.updatePortfolioName(id, newName, currentUserId)
        return if (updatedPortfolio != null) {
            ResponseEntity(updatedPortfolio, HttpStatus.OK)
        } else {
            ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

    @DeleteMapping("/portfolios/{id}")
    fun deletePortfolio(@PathVariable id: Long): ResponseEntity<Void> {
        portfolioService.deletePortfolio(id, currentUserId)
        return ResponseEntity(HttpStatus.NO_CONTENT)
    }

    @PostMapping("/portfolios/{portfolioId}/holdings")
    fun addHoldingToPortfolio(
        @PathVariable portfolioId: Long,
        @RequestBody request: AddHoldingRequest
    ): ResponseEntity<Portfolio> {
        val updatedPortfolio = portfolioService.addHoldingToPortfolio(portfolioId, request.assetId, request.quantity, currentUserId)
        return if (updatedPortfolio != null) {
            ResponseEntity(updatedPortfolio, HttpStatus.OK)
        } else {
            ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

    @PutMapping("/portfolios/{portfolioId}/holdings/{holdingId}")
    fun updateHoldingInPortfolio(
        @PathVariable portfolioId: Long,
        @PathVariable holdingId: Long,
        @RequestBody request: UpdateHoldingRequest
    ): ResponseEntity<Portfolio> {
        val updatedPortfolio = portfolioService.updateHoldingInPortfolio(portfolioId, holdingId, request.quantity, currentUserId)
        return if (updatedPortfolio != null) {
            ResponseEntity(updatedPortfolio, HttpStatus.OK)
        } else {
            ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

    @DeleteMapping("/portfolios/{portfolioId}/holdings/{holdingId}")
    fun deleteHoldingFromPortfolio(
        @PathVariable portfolioId: Long,
        @PathVariable holdingId: Long
    ): ResponseEntity<Portfolio> {
        val updatedPortfolio = portfolioService.deleteHoldingFromPortfolio(portfolioId, holdingId, currentUserId)
        return if (updatedPortfolio != null) {
            ResponseEntity(updatedPortfolio, HttpStatus.OK)
        } else {
            ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

    @GetMapping("/portfolios/{portfolioId}/value")
    fun getPortfolioValue(@PathVariable portfolioId: Long): ResponseEntity<Double> {
        val value = portfolioService.calculatePortfolioValue(portfolioId, currentUserId)
        return if (value != null) {
            ResponseEntity(value, HttpStatus.OK)
        } else {
            ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

    @GetMapping("/assets")
    fun getAllAssets(): ResponseEntity<List<CachedCryptoPrice>> {
        val assets = cryptoPriceCacheService.getAllCachedPrices()
        return ResponseEntity(assets, HttpStatus.OK)
    }

    @GetMapping("/assets/{symbol}")
    fun getAssetBySymbol(@PathVariable symbol: String): ResponseEntity<CachedCryptoPrice> {
        val asset = cryptoPriceCacheService.getCachedPriceBySymbol(symbol)
        return if (asset != null) {
            ResponseEntity(asset, HttpStatus.OK)
        } else {
            ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }
}
