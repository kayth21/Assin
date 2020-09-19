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

    suspend fun loadAllTitles(): Set<Title> {
        val allRemoteTitles = Coinpaprika.loadAllTitles()
        val allLocalTitles = TitleRepository.loadAllTitles()

        val cryptoTitle = allRemoteTitles.single { it.symbol == Preferences.getCryptoTitleSymbol() }
        val fiatTitle = allRemoteTitles.single { it.symbol == Preferences.getFiatTitleSymbol() }

        PreferenceManager.getDefaultSharedPreferences(AssinApplication.appContext).edit()
                .setTitle(Preferences.CRYPTO_TITLE, cryptoTitle)
                .setTitle(Preferences.FIAT_TITLE, fiatTitle)
                .apply()

        return allRemoteTitles.map { remoteTitle ->
            val localTitle = allLocalTitles.find { localTitle -> remoteTitle.id == localTitle.id }
            remoteTitle.copy(active = localTitle?.active ?: -75)
        }.toSet()
    }
}