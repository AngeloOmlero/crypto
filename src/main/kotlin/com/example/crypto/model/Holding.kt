package com.example.crypto.model

import com.fasterxml.jackson.annotation.JsonBackReference
import jakarta.persistence.*

@Entity
@Table(name = "holding")
class Holding(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,
    var assetId: String, // e.g., "bitcoin", "ethereum"
    var quantity: Double,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolio_id")
    @JsonBackReference
    val portfolio: Portfolio
)
