package com.example.crypto.client

import com.example.crypto.dto.AssetDto
import com.example.crypto.dto.CoinCapAssetsResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient


@Component
class CoinCapClient(
    @Value("\${coincap.api.key}") private val apiKey: String
) {
    private val client = WebClient.builder()
        .baseUrl("https://rest.coincap.io/v3")
        .defaultHeader("Authorization", "Bearer $apiKey")
        .build()

    fun getAllAssets(): List<AssetDto>{
        val response = client.get()
            .uri("/assets?limit=500")
            .retrieve()
            .bodyToMono(CoinCapAssetsResponse::class.java)
            .block()

        return response?.data ?: emptyList()
    }

}
