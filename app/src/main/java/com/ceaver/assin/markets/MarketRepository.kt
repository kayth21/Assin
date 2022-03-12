package com.ceaver.assin.markets

import androidx.preference.PreferenceManager
import com.ceaver.assin.AssinApplication
import com.ceaver.assin.extensions.setTitle
import com.ceaver.assin.markets.overview.MarketOverview
import com.ceaver.assin.preferences.Preferences
import com.coinpaprika.apiclient.COINPAPRIKA_BASE_URL
import com.coinpaprika.apiclient.FiatsService
import com.coinpaprika.apiclient.GlobalStatsService
import com.coinpaprika.apiclient.TickersService
import com.coinpaprika.apiclient.entity.QuoteEntity
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object MarketRepository {

    suspend fun loadMarketOverview(): MarketOverview {
        val globalStatsService = createCoinpaprikaService(GlobalStatsService::class.java)
        return MarketOverview.fromMarket(globalStatsService.getGlobalStats())
    }

    suspend fun loadAllTitles(): Triple<List<CryptoTitle>, List<FiatTitle>, List<CustomTitle>> {
        // load remote titles
        val tickersService = createCoinpaprikaService(TickersService::class.java)
        val fiatsService = createCoinpaprikaService(FiatsService::class.java)
        val cryptoItems = tickersService.getTickers("USD")
        val fiatItems = fiatsService.getFiats().filter { it.id == "usd-us-dollars" } // TODO Support more fiats
         val customItems = TitleRepository.loadAllCustomTitles()

        // find reference titles in remote titles
        val cryptoEntity = cryptoItems.single { it.id == Preferences.getCryptoTitleId() }.let { it.quotes!!.get("USD") }!!
        val fiatEntity = fiatItems.single { it.id == Preferences.getFiatTitleId() }.let { QuoteEntity(price = 1.0, athDate = null, athPrice = 1.0, dailyVolume = 1.0, dailyVolumeDailyChange = 1.0, marketCap = 1.0, marketCapDailyChange = 1.0, percentChange1h = 0.0, percentChange1y = 0.0, percentChange7d = 0.0, percentChange12h = 0.0, percentChange24h = 0.0, percentChange30d = 0.0, percentFromPriceAth = 0.0) }         // TODO support fiat other USD

        // transform remote items to assin titles
        val localCryptoTitles = TitleRepository.loadAllCryptoTitles()
        val cryptoTitles = cryptoItems.map { CryptoTitle.fromMarket(it, localCryptoTitles.find { localTitle -> it.id == localTitle.id }?.active, cryptoEntity, fiatEntity) }
        val fiatTitles = fiatItems.map { FiatTitle.fromMarket(it, cryptoEntity, fiatEntity) }
         val customTitles = customItems.map { CustomTitle.fromMarket(it, cryptoEntity, fiatEntity) }

        // update reference titles
        PreferenceManager.getDefaultSharedPreferences(AssinApplication.appContext!!).edit()
                .setTitle(Preferences.CRYPTO_TITLE, cryptoTitles.single { it.id == Preferences.getCryptoTitleId() })
                .setTitle(Preferences.FIAT_TITLE, fiatTitles.single { it.id == Preferences.getFiatTitleId() })
                .apply()

        return Triple(cryptoTitles, fiatTitles, customTitles)
    }

    private fun <T : Any?> createCoinpaprikaService(serviceClass: Class<T>) = Retrofit.Builder()
        .baseUrl(COINPAPRIKA_BASE_URL)
        .addConverterFactory(MoshiConverterFactory.create())
        .client(OkHttpClient.Builder().build())
        .build().create(serviceClass)
}