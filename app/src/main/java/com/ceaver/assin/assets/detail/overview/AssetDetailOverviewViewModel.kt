package com.ceaver.assin.assets.detail.overview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ceaver.assin.assets.AssetRepository
import com.ceaver.assin.markets.Title

class AssetDetailOverviewViewModel(val title: Title) : ViewModel() {

    val asset = AssetRepository.loadAssetObserved(title)

    class Factory(val title: Title) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return AssetDetailOverviewViewModel(title) as T
        }
    }
}