package com.ceaver.assin.markets

import com.ceaver.assin.MyApplication
import com.ceaver.assin.assets.Category
import com.coinpaprika.apiclient.api.CoinpaprikaApi
import com.coinpaprika.apiclient.entity.TickerEntity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.time.ZonedDateTime
import java.util.*

object Coinpaprika {

    fun load(id: String): Optional<Title> {
        var titleOptional: Optional<Title> = Optional.empty()
        CoinpaprikaApi(MyApplication.appContext!!)
                .ticker(id)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .blockingSubscribe(
                        { ticker -> titleOptional = transform(ticker) },
                        { error -> error.printStackTrace() }) // TODO

        return titleOptional
    }

    fun loadAllTitles(): Set<Title> {

        CoinpaprikaApi(MyApplication.appContext!!)
                .fiats()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .blockingSubscribe(
                        { ticker -> println(ticker) },
                        { error -> error.printStackTrace() }) // TODO


        val resultSet = mutableSetOf<Title>();
        CoinpaprikaApi(MyApplication.appContext!!)
                .tickers()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .blockingSubscribe(
                        { next ->
                            for (ticker in next) {
                                val titleOptional = transform(ticker)
                                titleOptional.ifPresent { resultSet.add(titleOptional.get()) } // TODO Else
                            }
                        },
                        { error -> error.printStackTrace() }) // TODO
        return resultSet
    }

    private fun transform(ticker: TickerEntity): Optional<Title> {
        val id = ticker.id
        val name = ticker.name
        val rank = ticker.rank
        val symbol = ticker.symbol
        val priceUsd = ticker.quotes!!.get("USD")!!.price
        val priceBtc = if (ticker.quotes!!.get("BTC") == null) 1.0 else ticker.quotes!!.get("BTC")!!.price // TODO There should always be BTC Quotes
        val marketCapUsd = ticker.quotes!!.get("USD")!!.marketCap
        val percentChange1h = ticker.quotes!!.get("USD")!!.percentChange1h
        val percentChange24h = ticker.quotes!!.get("USD")!!.percentChange24h
        val percentChange7d = ticker.quotes!!.get("USD")!!.percentChange7d
        val lastUpdated = ticker.lastUpdated

        return Optional.of(Title(id, name, symbol, Category.CRYPTO, priceUsd, priceBtc, marketCapUsd, rank, Optional.of(percentChange1h), Optional.of(percentChange24h), Optional.of(percentChange7d), transformTimestamp(lastUpdated!!), Integer(1818)))
    }

    private fun transformTimestamp(lastUpdated: String) = ZonedDateTime.parse(lastUpdated).toLocalDateTime()
}