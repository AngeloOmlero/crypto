package com.example.crypto.repository

import com.example.crypto.model.Holding
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface HoldingRepository : JpaRepository<Holding, Long>
