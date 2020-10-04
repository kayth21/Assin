package com.ceaver.assin.markets

import android.preference.PreferenceManager
import com.ceaver.assin.AssinApplication
import com.ceaver.assin.extensions.setTitle
import com.ceaver.assin.markets.overview.MarketOverview
import com.ceaver.assin.preferences.Preferences
import com.coinpaprika.apiclient.api.CoinpaprikaApi
import com.coinpaprika.apiclient.entity.QuoteEntity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

object MarketRepository {
    fun loadMarketOverview(): MarketOverview {
        lateinit var marketOverview: MarketOverview
        CoinpaprikaApi()
                .global()
                .unsubscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .blockingSubscribe(
                        { marketOverview = MarketOverview.fromMarket(it!!) },
                        { error -> error.printStackTrace() }) // TODO
        return marketOverview
    }

    suspend fun loadAllTitles(): Pair<List<CryptoTitle>, List<FiatTitle>> {
        // load remote titles
        val cryptoItems = CoinpaprikaApi().tickers()
        val fiatItems = CoinpaprikaApi().fiats().filter { it.symbol == "USD" } // TODO Support more fiats
        // val customItems = TitleRepository.loadAllCustomTitles()

        // find reference titles in remote titles
        val cryptoEntity = cryptoItems.single { it.symbol == Preferences.getCryptoTitleSymbol() }.let { it.quotes!!.get("USD") }!!
        val fiatEntity = fiatItems.single { it.symbol == Preferences.getFiatTitleSymbol() }.let { QuoteEntity(price = 1.0, athDate = null, athPrice = 1.0, dailyVolume = 1.0, dailyVolumeDailyChange = 1.0, marketCap = 1.0, marketCapDailyChange = 1.0, percentChange1h = 0.0, percentChange1y = 0.0, percentChange7d = 0.0, percentChange12h = 0.0, percentChange24h = 0.0, percentChange30d = 0.0, percentFromPriceAth = 0.0, volumeAdjusted = 0.0, volumeReported = 0.0) }         // TODO support fiat other USD

        // transform remote items to assin titles
        val localCryptoTitles = TitleRepository.loadAllCryptoTitles()
        val cryptoTitles = cryptoItems.map { CryptoTitle.fromMarket(it, localCryptoTitles.find { localTitle -> it.id == localTitle.id }?.active, cryptoEntity, fiatEntity) }
        val fiatTitles = fiatItems.map { FiatTitle.fromMarket(it, cryptoEntity, fiatEntity) }
        // val customTitles = customItems.map { CustomTitle.fromMarket(it, cryptoEntity, fiatEntity) }

        // update reference titles
        PreferenceManager.getDefaultSharedPreferences(AssinApplication.appContext).edit()
                .setTitle(Preferences.CRYPTO_TITLE, cryptoTitles.single { it.symbol == Preferences.getCryptoTitleSymbol() })
                .setTitle(Preferences.FIAT_TITLE, fiatTitles.single { it.symbol == Preferences.getFiatTitleSymbol() })
                .apply()

        return Pair(cryptoTitles, fiatTitles)
    }
}