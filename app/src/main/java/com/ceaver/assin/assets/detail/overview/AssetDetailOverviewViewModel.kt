package com.ceaver.assin.assets.detail.overview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ceaver.assin.assets.AssetRepository
import com.ceaver.assin.markets.Title
import com.ceaver.assin.preferences.Preferences

class AssetDetailOverviewViewModel(val title: Title) : ViewModel() {

    val asset = AssetRepository.loadAssetObserved(title)
    val primaryTitle = Preferences.getCryptoTitle()
    val secondaryTitle = Preferences.getFiatTitle()

    class Factory(val title: Title) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return AssetDetailOverviewViewModel(title) as T
        }
    }
}