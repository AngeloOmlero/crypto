package com.example.crypto.repository

import com.example.crypto.model.Portfolio
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PortfolioRepository : JpaRepository<Portfolio, Long>
