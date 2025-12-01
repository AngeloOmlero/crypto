package com.example.crypto.service

import com.example.crypto.client.CoinCapClient
import com.example.crypto.model.Holding
import com.example.crypto.model.Portfolio
import com.example.crypto.repository.HoldingRepository
import com.example.crypto.repository.PortfolioRepository
import com.example.crypto.repository.UserRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PortfolioService(
    private val portfolioRepository: PortfolioRepository,
    private val holdingRepository: HoldingRepository,
    private val userRepository: UserRepository,
    private val coinCapClient: CoinCapClient
) {

    fun createPortfolio(name: String, userId: Long): Portfolio? {
        val user = userRepository.findByIdOrNull(userId) ?: return null
        val portfolio = Portfolio(name = name, user = user)
        return portfolioRepository.save(portfolio)
    }

    fun getPortfolioById(id: Long, userId: Long): Portfolio? {
        return portfolioRepository.findByIdOrNull(id)?.takeIf { it.user.id == userId }
    }

    fun getAllPortfolios(userId: Long): List<Portfolio> {
        val user = userRepository.findByIdOrNull(userId) ?: return emptyList()
        return user.portfolios
    }

    fun updatePortfolioName(id: Long, newName: String, userId: Long): Portfolio? {
        return portfolioRepository.findByIdOrNull(id)?.takeIf { it.user.id == userId }?.apply {
            name = newName
            portfolioRepository.save(this)
        }
    }

    fun deletePortfolio(id: Long, userId: Long) {
        portfolioRepository.findByIdOrNull(id)?.takeIf { it.user.id == userId }?.let {
            portfolioRepository.delete(it)
        }
    }

    @Transactional
    fun addHoldingToPortfolio(portfolioId: Long, assetId: String, quantity: Double, userId: Long): Portfolio? {
        return portfolioRepository.findByIdOrNull(portfolioId)?.takeIf { it.user.id == userId }?.apply {
            val holding = Holding(assetId = assetId, quantity = quantity, portfolio = this)
            this.holdings.add(holding)
            portfolioRepository.save(this)
        }
    }

    @Transactional
    fun updateHoldingInPortfolio(portfolioId: Long, holdingId: Long, newQuantity: Double, userId: Long): Portfolio? {
        return portfolioRepository.findByIdOrNull(portfolioId)?.takeIf { it.user.id == userId }?.apply {
            val holding = this.holdings.find { it.id == holdingId }
            holding?.apply {
                quantity = newQuantity
                holdingRepository.save(this)
            }
            portfolioRepository.save(this)
        }
    }

    @Transactional
    fun deleteHoldingFromPortfolio(portfolioId: Long, holdingId: Long, userId: Long): Portfolio? {
        return portfolioRepository.findByIdOrNull(portfolioId)?.takeIf { it.user.id == userId }?.apply {
            this.holdings.removeIf { it.id == holdingId }
            portfolioRepository.save(this)
        }
    }

    fun calculatePortfolioValue(portfolioId: Long, userId: Long): Double? {
        val portfolio = portfolioRepository.findByIdOrNull(portfolioId)?.takeIf { it.user.id == userId } ?: return null
        val assets = coinCapClient.getAllAssets()
        val assetPrices = assets.associateBy { it.id }

        var totalValue = 0.0
        for (holding in portfolio.holdings) {
            val priceString = assetPrices[holding.assetId]?.priceUsd
            if (priceString != null) {
                val price = priceString.toDoubleOrNull() ?: 0.0
                totalValue += holding.quantity * price
            }
        }
        return totalValue
    }
}
