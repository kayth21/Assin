package com.ceaver.assin.markets

import com.ceaver.assin.assets.AssetCategory
import com.ceaver.assin.extensions.asFactor
import com.ceaver.assin.markets.overview.MarketOverview
import com.ceaver.assin.preferences.Preferences
import com.coinpaprika.apiclient.api.CoinpaprikaApi
import com.coinpaprika.apiclient.entity.FiatEntity
import com.coinpaprika.apiclient.entity.GlobalStatsEntity
import com.coinpaprika.apiclient.entity.QuoteEntity
import com.coinpaprika.apiclient.entity.TickerEntity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.time.ZonedDateTime

object Coinpaprika {

    fun loadGlobalStats(): MarketOverview {
        lateinit var marketOverview: MarketOverview
        CoinpaprikaApi()
                .global()
                .unsubscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .blockingSubscribe(
                        { marketOverview = transformGlobalStats(it!!) },
                        { error -> error.printStackTrace() }) // TODO
        return marketOverview
    }

    private fun transformGlobalStats(globalStatsEntity: GlobalStatsEntity): MarketOverview {
        return MarketOverview(
                marketCapUsd = globalStatsEntity.marketCapUsd,
                dailyMarketCapChange = globalStatsEntity.dailyMarketCapChange,
                marketCapAthValue = globalStatsEntity.marketCapAthValue,
                marketCapAthDate = globalStatsEntity.marketCapAthDate,
                dailyVolumeUsd = globalStatsEntity.dailyVolumeUsd,
                dailyVolumeChange = globalStatsEntity.dailyVolumeChange,
                volumeAthValue = globalStatsEntity.volumeAthValue,
                volumeAthDate = globalStatsEntity.volumeAthDate,
                btcDominancePercentage = globalStatsEntity.btcDominancePercentage,
                cryptocurrenciesAmount = globalStatsEntity.cryptocurrenciesAmount,
                lastUpdated = globalStatsEntity.lastUpdated
        )
    }

    suspend fun loadAllTitles(): Set<Title> {
        val cryptoTitleSymbol = Preferences.getCryptoTitleSymbol()
        val fiatTitleSymbol = Preferences.getFiatTitleSymbol()

        val resultSet = mutableSetOf<Title>()

        val tickers = CoinpaprikaApi().tickers()
        val fiats = CoinpaprikaApi().fiats()

        val cryptoEntity = tickers.single { it.symbol == cryptoTitleSymbol }.let { it.quotes!!.get("USD") }!!
        // TODO support fiat other USD
        val fiatEntity = fiats.single { it.symbol == fiatTitleSymbol }.let { QuoteEntity(price = 1.0, athDate = null, athPrice = 1.0, dailyVolume = 1.0, dailyVolumeDailyChange = 1.0, marketCap = 1.0, marketCapDailyChange = 1.0, percentChange1h = 0.0, percentChange1y = 0.0, percentChange7d = 0.0, percentChange12h = 0.0, percentChange24h = 0.0, percentChange30d = 0.0, percentFromPriceAth = 0.0, volumeAdjusted = 0.0, volumeReported = 0.0) }

        tickers.forEach { resultSet.add(transform(it, cryptoEntity, fiatEntity)) }
        fiats.forEach { resultSet.add(transform(it, cryptoEntity, fiatEntity)) }

        return resultSet
    }

    private fun transform(fiatEntity: FiatEntity, cryptoQuote: QuoteEntity, fiatQuote: QuoteEntity): Title {
        return Title(
                id = fiatEntity.id,
                name = fiatEntity.name,
                symbol = fiatEntity.symbol,
                category = AssetCategory.FIAT,
                active = 1818,
                cryptoQuotes = Quotes(1.0 * cryptoQuote.price), // TODO
                fiatQuotes = Quotes(1.0 * fiatQuote.price)
        )
    }

    private fun transform(ticker: TickerEntity, cryptoQuote: QuoteEntity, fiatQuote: QuoteEntity): Title {
        val usdQuotes = ticker.quotes!!.get("USD")!!

        return Title(
                id = ticker.id,
                name = ticker.name,
                symbol = ticker.symbol,
                category = AssetCategory.CRYPTO,
                active = 1818,

                rank = ticker.rank,
                circulatingSupply = ticker.circulatingSupply,
                totalSupply = ticker.totalSupply,
                maxSupply = ticker.maxSupply,
                betaValue = ticker.betaValue,
                lastUpdated = if (ticker.lastUpdated == null) null else transformTimestamp(ticker.lastUpdated!!),

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

    private fun transformTimestamp(lastUpdated: String) = ZonedDateTime.parse(lastUpdated).toLocalDateTime()
}