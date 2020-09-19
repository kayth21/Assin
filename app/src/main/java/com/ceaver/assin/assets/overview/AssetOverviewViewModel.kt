package com.ceaver.assin.assets.overview

import androidx.lifecycle.ViewModel
import com.ceaver.assin.assets.AssetRepository
import com.ceaver.assin.preferences.Preferences

class AssetOverviewViewModel() : ViewModel() {

    val assetOverview = AssetRepository.loadAssetOverviewObserved()
    val cryptoTitleSymbol = Preferences.getCryptoTitleSymbol()
    val fiatTitleSymbol = Preferences.getFiatTitleSymbol()
}