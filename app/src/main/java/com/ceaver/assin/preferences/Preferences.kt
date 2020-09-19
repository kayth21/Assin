package com.ceaver.assin.preferences

import android.preference.PreferenceManager
import com.ceaver.assin.AssinApplication
import com.ceaver.assin.extensions.getString
import com.ceaver.assin.extensions.getTitle

object Preferences {
    const val CRYPTO_TITLE = "cryptoTitle"
    const val FIAT_TITLE = "fiatTitle"

    const val CRYPTO_TITLE_SYMBOL = "cryptoTitleSymbol"
    const val FIAT_TITLE_SYMBOL = "fiatTitleSymbol"

    fun getCryptoTitleSymbol() = PreferenceManager.getDefaultSharedPreferences(AssinApplication.appContext).getString(CRYPTO_TITLE_SYMBOL)
    fun getFiatTitleSymbol() = PreferenceManager.getDefaultSharedPreferences(AssinApplication.appContext).getString(FIAT_TITLE_SYMBOL)

    fun getCryptoTitle() = PreferenceManager.getDefaultSharedPreferences(AssinApplication.appContext).getTitle(CRYPTO_TITLE)
    fun getFiatTitle() = PreferenceManager.getDefaultSharedPreferences(AssinApplication.appContext).getTitle(FIAT_TITLE)
}

