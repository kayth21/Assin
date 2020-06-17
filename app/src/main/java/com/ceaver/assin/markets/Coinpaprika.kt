package com.ceaver.assin.markets

import com.ceaver.assin.assets.AssetCategory
import com.ceaver.assin.markets.overview.MarketOverview
import com.coinpaprika.apiclient.api.CoinpaprikaApi
import com.coinpaprika.apiclient.entity.FiatEntity
import com.coinpaprika.apiclient.entity.GlobalStatsEntity
import com.coinpaprika.apiclient.entity.TickerEntity
import com.coinpaprika.apiclient.exception.NotFoundError
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.math.BigDecimal
import java.time.ZonedDateTime
import java.util.*

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

    suspend fun load(id: String): Optional<Title> {
        // TODO handle other Errors
        return try { Optional.of(transform(CoinpaprikaApi().ticker(id))) } catch (e: NotFoundError) { Optional.empty<Title>() }
    }

    suspend fun loadAllTitles(): Set<Title> {
        val resultSet = mutableSetOf<Title>()
        CoinpaprikaApi().fiats().forEach { resultSet.add(transform(it)) }
        CoinpaprikaApi().tickers().forEach { resultSet.add(transform(it)) }
        return resultSet
    }

    private fun transform(fiatEntity: FiatEntity): Title {
        return Title(
                id = fiatEntity.id,
                name = fiatEntity.name,
                symbol = fiatEntity.symbol,
                category = AssetCategory.FIAT,
                active = 1818,
                priceUsd = BigDecimal.ONE // TODO this is only valid for USD
        )
    }

    private fun transform(ticker: TickerEntity): Title {
        val usdQuotes = ticker.quotes!!.get("USD")!!
        val btcQuotes = ticker.quotes!!.get("BTC")!!
        val ethQuotes = ticker.quotes!!.get("ETH")!!

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

                priceUsd = usdQuotes.price.toBigDecimal(),
                volume24hUsd = usdQuotes.dailyVolume.toBigDecimal(),
                marketCapUsd = usdQuotes.marketCap.toBigDecimal(),
                marketCapChange24hUsd = usdQuotes.marketCapDailyChange.toBigDecimal(),
                percentChange1hUsd = usdQuotes.percentChange1h.toBigDecimal(),
                percentChange24hUsd = usdQuotes.percentChange24h.toBigDecimal(),
                percentChange7dUsd = usdQuotes.percentChange7d.toBigDecimal(),
                percentChange30dUsd = usdQuotes.percentChange30d.toBigDecimal(),
                percentChange1yUsd = usdQuotes.percentChange1y.toBigDecimal(),
                athPriceUsd = usdQuotes.athPrice.toBigDecimal(),
                athDateUsd = if (usdQuotes.athDate == null) null else transformTimestamp(usdQuotes.athDate!!),
                athPercentUsd = usdQuotes.athPrice.toBigDecimal(),

                priceBtc = btcQuotes.price.toBigDecimal(),
                volume24hBtc = btcQuotes.dailyVolume.toBigDecimal(),
                marketCapBtc = btcQuotes.marketCap.toBigDecimal(),
                marketCapChange24hBtc = btcQuotes.marketCapDailyChange.toBigDecimal(),
                percentChange1hBtc = btcQuotes.percentChange1h.toBigDecimal(),
                percentChange24hBtc = btcQuotes.percentChange24h.toBigDecimal(),
                percentChange7dBtc = btcQuotes.percentChange7d.toBigDecimal(),
                percentChange30dBtc = btcQuotes.percentChange30d.toBigDecimal(),
                percentChange1yBtc = btcQuotes.percentChange1y.toBigDecimal(),
                athPriceBtc = btcQuotes.athPrice.toBigDecimal(),
                athDateBtc = if (btcQuotes.athDate == null) null else transformTimestamp(btcQuotes.athDate!!),
                athPercentBtc = btcQuotes.athPrice.toBigDecimal(),

                priceEth = ethQuotes.price.toBigDecimal(),
                volume24hEth = ethQuotes.dailyVolume.toBigDecimal(),
                marketCapEth = ethQuotes.marketCap.toBigDecimal(),
                marketCapChange24hEth = ethQuotes.marketCapDailyChange.toBigDecimal(),
                percentChange1hEth = ethQuotes.percentChange1h.toBigDecimal(),
                percentChange24hEth = ethQuotes.percentChange24h.toBigDecimal(),
                percentChange7dEth = ethQuotes.percentChange7d.toBigDecimal(),
                percentChange30dEth = ethQuotes.percentChange30d.toBigDecimal(),
                percentChange1yEth = ethQuotes.percentChange1y.toBigDecimal(),
                athPriceEth = ethQuotes.athPrice.toBigDecimal(),
                athDateEth = if (ethQuotes.athDate == null) null else transformTimestamp(ethQuotes.athDate!!),
                athPercentEth = ethQuotes.athPrice.toBigDecimal()
        )
    }

    private fun transformTimestamp(lastUpdated: String) = ZonedDateTime.parse(lastUpdated).toLocalDateTime()
}