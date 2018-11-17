package com.ceaver.assin.markets

import com.ceaver.assin.assets.Category
import java.time.LocalDateTime
import java.util.*

data class Title(val id: String, val symbol: String, val category: Category, val name: String, val priceUsd: Double, val priceBtc: Double, val marketCapUsd: Long, val rank: Int, val percentChange1h: Optional<Double>, val percentChange24h: Optional<Double>, val percentChange7d: Optional<Double>, val lastUpdated: LocalDateTime) {
    fun getPercentChange24hString(): String {
        return if(percentChange24h.isPresent) "%.1f".format(percentChange24h.get()) else "N/A"
    }
}