package com.ceaver.assin.assets.overview

import com.ceaver.assin.assets.AssetRepository
import com.ceaver.assin.markets.Title
import com.ceaver.assin.preferences.Preferences
import java.math.BigDecimal

object AssetOverviewValue {

    suspend fun lookupPrice(quoteTitle: Title): BigDecimal {
        val loadAssetOverview = AssetRepository.loadAssetOverview()
        return when (quoteTitle.symbol) {
            Preferences.getFiatTitleSymbol() ->
                loadAssetOverview.valueFiat
            Preferences.getCryptoTitleSymbol() ->
                loadAssetOverview.valueCrypto
            else -> TODO("no implementation for quote title ${quoteTitle.symbol}.")
        }
    }
}