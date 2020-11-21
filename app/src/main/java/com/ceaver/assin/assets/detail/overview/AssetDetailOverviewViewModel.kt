package com.ceaver.assin.assets.detail.overview

import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ceaver.assin.assets.AssetRepository
import com.ceaver.assin.markets.Title
import com.ceaver.assin.preferences.Preferences

class AssetDetailOverviewViewModel(val title: Title, val label: String?) : ViewModel() {

    val asset =  Transformations.map(AssetRepository.loadAllAssetsObserved()) { it.single { it.title.id == title.id && it.label == label } }
    val cryptoTitle = Preferences.getCryptoTitle()
    val fiatTitle = Preferences.getFiatTitle()

    class Factory(val title: Title, val label: String?) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return AssetDetailOverviewViewModel(title, label) as T
        }
    }
}