package com.ceaver.assin.markets

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.time.LocalDateTime

interface Title : Parcelable {
    val id: String
    val symbol: String
    val name: String
    val lastUpdated: LocalDateTime
    val cryptoQuotes: Quotes
    val fiatQuotes: Quotes
    fun getIcon(): Int
    fun toEntity(): TitleEntity
    fun getPercentChange1hString(): String
    fun getPercentChange24hString(): String
    fun getPercentChange7dString(): String

    @Parcelize
    data class Quotes(
            val price: Double,
            val volume24h: Double? = null,
            val marketCap: Double? = null,
            val marketCapChange24h: Double? = null,
            val percentChange1h: Double? = null,
            val percentChange24h: Double? = null,
            val percentChange7d: Double? = null,
            val percentChange30d: Double? = null,
            val percentChange1y: Double? = null
    ) : Parcelable
}
