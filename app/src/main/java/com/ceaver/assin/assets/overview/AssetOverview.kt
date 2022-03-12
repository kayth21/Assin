package com.ceaver.assin.assets.overview

import com.ceaver.assin.markets.Title
import com.ceaver.assin.preferences.Preferences
import java.math.BigDecimal

data class AssetOverview(
        val valueCrypto: BigDecimal = BigDecimal.ZERO,
        val valueFiat: BigDecimal = BigDecimal.ZERO
) {
    fun lookupValue(quoteTitle: Title): BigDecimal {
        return when (quoteTitle.id) {
            Preferences.getFiatTitleId() -> valueFiat
            Preferences.getCryptoTitleId() -> valueCrypto
            else -> TODO("no implementation for quote title ${quoteTitle.id}.")
        }
    }
}