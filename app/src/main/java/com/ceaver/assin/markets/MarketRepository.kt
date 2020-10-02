package com.ceaver.assin.markets

import android.preference.PreferenceManager
import com.ceaver.assin.AssinApplication
import com.ceaver.assin.extensions.setTitle
import com.ceaver.assin.markets.overview.MarketOverview
import com.ceaver.assin.preferences.Preferences

object MarketRepository {
    fun loadMarketOverview(): MarketOverview {
        return Coinpaprika.loadGlobalStats()
    }

    suspend fun loadAllTitles(): Pair<List<CryptoTitle>, List<FiatTitle>> {
        val allRemoteTitles = Coinpaprika.loadAllTitles()
        val allRemoteCryptoTitles = allRemoteTitles.first
        val allRemoteFiatTitles = allRemoteTitles.second
        val allLocalTitles = TitleRepository.loadAll()

        val cryptoTitle = allRemoteCryptoTitles.single { it.symbol == Preferences.getCryptoTitleSymbol() }
        val fiatTitle = allRemoteFiatTitles.single { it.symbol == Preferences.getFiatTitleSymbol() }

        PreferenceManager.getDefaultSharedPreferences(AssinApplication.appContext).edit()
                .setTitle(Preferences.CRYPTO_TITLE, cryptoTitle)
                .setTitle(Preferences.FIAT_TITLE, fiatTitle)
                .apply()

        return Pair(allRemoteCryptoTitles.map { remoteTitle ->
            val localTitle = allLocalTitles.find { localTitle -> remoteTitle.id == localTitle.id } as CryptoTitle?
            remoteTitle.copy(active = localTitle?.active ?: -75)
        } , allRemoteFiatTitles)
    }
}