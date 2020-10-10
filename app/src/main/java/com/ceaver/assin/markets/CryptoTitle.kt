package com.ceaver.assin.markets

import com.ceaver.assin.AssinApplication
import com.ceaver.assin.R
import com.ceaver.assin.assets.AssetCategory
import com.ceaver.assin.extensions.asFactor
import com.ceaver.assin.extensions.resIdByName
import com.coinpaprika.apiclient.entity.QuoteEntity
import com.coinpaprika.apiclient.entity.TickerEntity
import kotlinx.android.parcel.Parcelize
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.util.*

@Parcelize
data class CryptoTitle(
        override val id: String,
        override val symbol: String,
        override val name: String,
        val active: Int,
        val rank: Int,
        val circulatingSupply: Long,
        val totalSupply: Long,
        val maxSupply: Long,
        val betaValue: Double,
        override val lastUpdated: LocalDateTime,
        override val cryptoQuotes: Quotes,
        override val fiatQuotes: Quotes
) : Title {

    companion object Factory {
        fun fromEntity(entity: TitleEntity): CryptoTitle {
            return CryptoTitle(
                    id = entity.id,
                    symbol = entity.symbol,
                    name = entity.name,
                    active = entity.active,
                    rank = entity.rank,
                    circulatingSupply = entity.circulatingSupply!!,
                    totalSupply = entity.totalSupply!!,
                    maxSupply = entity.maxSupply!!,
                    betaValue = entity.betaValue!!,
                    lastUpdated = entity.lastUpdated,
                    cryptoQuotes = entity.cryptoQuotes,
                    fiatQuotes = entity.fiatQuotes
            )
        }

        fun fromMarket(ticker: TickerEntity, active: Int?, cryptoQuote: QuoteEntity, fiatQuote: QuoteEntity): CryptoTitle {
            val usdQuotes = ticker.quotes!!.get("USD")!!

            return CryptoTitle(
                    id = ticker.id,
                    name = ticker.name,
                    symbol = ticker.symbol,
                    active = active ?: -75,

                    rank = ticker.rank,
                    circulatingSupply = ticker.circulatingSupply,
                    totalSupply = ticker.totalSupply,
                    maxSupply = ticker.maxSupply,
                    betaValue = ticker.betaValue,
                    lastUpdated = if (ticker.lastUpdated == null) LocalDateTime.now() else transformTimestamp(ticker.lastUpdated!!), // TODO is nullcheck necessary?

                    cryptoQuotes = Quotes(
                            price = usdQuotes.price / cryptoQuote.price,
                            volume24h = usdQuotes.dailyVolume / cryptoQuote.price, // TODO fix as soon as needed
                            marketCap = usdQuotes.marketCap / cryptoQuote.price, // TODO fix as soon as needed
                            marketCapChange24h = 100.0 - 100.0 / (usdQuotes.marketCapDailyChange + cryptoQuote.marketCapDailyChange), // TODO fix as soon as needed
                            percentChange1h = usdQuotes.percentChange1h.asFactor() / cryptoQuote.percentChange1h.asFactor() * 100.0 - 100,
                            percentChange24h = usdQuotes.percentChange24h.asFactor() / cryptoQuote.percentChange24h.asFactor() * 100.0 - 100,
                            percentChange7d = usdQuotes.percentChange7d.asFactor() / cryptoQuote.percentChange7d.asFactor() * 100.0 - 100,
                            percentChange30d = usdQuotes.percentChange30d.asFactor() / cryptoQuote.percentChange30d.asFactor() * 100.0 - 100,
                            percentChange1y = usdQuotes.percentChange1y.asFactor() / cryptoQuote.percentChange1y.asFactor() * 100.0 - 100
                    ),
                    fiatQuotes = Quotes(
                            price = usdQuotes.price / fiatQuote.price,
                            volume24h = usdQuotes.dailyVolume / fiatQuote.price, // TODO fix as soon as needed
                            marketCap = usdQuotes.marketCap / fiatQuote.price, // TODO fix as soon as needed
                            marketCapChange24h = 100.0 - 100.0 / (usdQuotes.marketCapDailyChange + fiatQuote.marketCapDailyChange), // TODO fix as soon as needed
                            percentChange1h = usdQuotes.percentChange1h.asFactor() / fiatQuote.percentChange1h.asFactor() * 100.0 - 100,
                            percentChange24h = usdQuotes.percentChange24h.asFactor() / fiatQuote.percentChange24h.asFactor() * 100.0 - 100,
                            percentChange7d = usdQuotes.percentChange7d.asFactor() / fiatQuote.percentChange7d.asFactor() * 100.0 - 100,
                            percentChange30d = usdQuotes.percentChange30d.asFactor() / fiatQuote.percentChange30d.asFactor() * 100.0 - 100,
                            percentChange1y = usdQuotes.percentChange1y.asFactor() / fiatQuote.percentChange1y.asFactor() * 100.0 - 100
                    )
            )

        }

        // TODO
        private fun transformTimestamp(lastUpdated: String) = ZonedDateTime.parse(lastUpdated).toLocalDateTime()
    }

    fun inactive(): Boolean {
        return active == -100
    }

    fun incrementActiveCounter(): CryptoTitle {
        return when {
            active == 100 -> this
            active == 49 -> this.copy(active = 51)
            active == -50 -> this.copy(active = 50)
            else -> this.copy(active = active + 1)
        }
    }

    fun decreaseActiveCounter(): Title {
        return when {
            active == -100 -> this
            active == 51 -> this.copy(active = 49)
            active == 0 -> this.copy(active = -100)
            else -> this.copy(active = active - 1)
        }
    }

    override fun getPercentChange1hString(): String {
        return "%.1f".format(cryptoQuotes.percentChange1h) // TODO let the user decide if primary or secondary
    }

    override fun getPercentChange24hString(): String {
        return "%.1f".format(cryptoQuotes.percentChange24h) // TODO let the user decide if primary or secondary
    }

    override fun getPercentChange7dString(): String {
        return "%.1f".format(cryptoQuotes.percentChange7d) // TODO let the user decide if primary or secondary
    }

    override fun getIcon(): Int {
        val identifier = AssinApplication.appContext!!.resIdByName(symbol.toLowerCase(Locale.ROOT), "drawable")
        return if (identifier == 0 || Character.isDigit(symbol[0])) R.drawable.generic else identifier // TODO some weird bug with symols that start with a number, e.g. 42
    }

    override fun toEntity(): TitleEntity {
        return TitleEntity(
                id = id,
                name = name,
                symbol = symbol,
                category = AssetCategory.CRYPTO,
                active = active,
                rank = rank,
                circulatingSupply = circulatingSupply,
                totalSupply = totalSupply,
                maxSupply = maxSupply,
                betaValue = betaValue,
                lastUpdated = lastUpdated,
                cryptoQuotes = cryptoQuotes,
                fiatQuotes = fiatQuotes
        )
    }
}