package com.ceaver.assin.markets

import android.os.Parcelable
import com.ceaver.assin.preferences.Preferences
import kotlinx.parcelize.Parcelize
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

    fun lookupPrice(quoteTitle: Title): Double {
        return when {
            quoteTitle.symbol == Preferences.getFiatTitleSymbol() -> fiatQuotes.price
            quoteTitle.symbol == Preferences.getCryptoTitleSymbol() -> cryptoQuotes.price
            this::class == quoteTitle::class -> fiatQuotes.price / quoteTitle.fiatQuotes.price
            else -> TODO("no implementation for market pair ${symbol}/${quoteTitle.symbol}.")
        }
    }

    fun lookupMarketcap(quoteTitle: Title): Double {
        return when {
            quoteTitle.symbol == Preferences.getFiatTitleSymbol() -> fiatQuotes.marketCap!!
            quoteTitle.symbol == Preferences.getCryptoTitleSymbol() -> cryptoQuotes.marketCap!!
            this::class == quoteTitle::class -> fiatQuotes.marketCap!! / quoteTitle.fiatQuotes.marketCap!!
            else -> TODO("no implementation for market pair ${symbol}/${quoteTitle.symbol}.")
        }
    }
}
