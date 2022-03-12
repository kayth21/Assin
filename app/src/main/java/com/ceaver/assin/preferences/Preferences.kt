package com.ceaver.assin.preferences

import androidx.preference.PreferenceManager
import com.ceaver.assin.AssinApplication
import com.ceaver.assin.extensions.getCryptoTitle
import com.ceaver.assin.extensions.getFiatTitle
import com.ceaver.assin.extensions.getString

object Preferences {
    const val CRYPTO_TITLE = "cryptoTitle"
    const val FIAT_TITLE = "fiatTitle"

    const val CRYPTO_TITLE_ID = "cryptoTitleId"
    const val FIAT_TITLE_ID = "fiatTitleId"

    fun getCryptoTitleId() = PreferenceManager.getDefaultSharedPreferences(AssinApplication.appContext!!).getString(CRYPTO_TITLE_ID)
    fun getFiatTitleId() = PreferenceManager.getDefaultSharedPreferences(AssinApplication.appContext!!).getString(FIAT_TITLE_ID)

    fun getCryptoTitle() = PreferenceManager.getDefaultSharedPreferences(AssinApplication.appContext!!).getCryptoTitle(CRYPTO_TITLE)
    fun getFiatTitle() = PreferenceManager.getDefaultSharedPreferences(AssinApplication.appContext!!).getFiatTitle(FIAT_TITLE)
}

