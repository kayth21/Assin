package com.ceaver.assin.assets.detail.overview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.map
import com.ceaver.assin.assets.AssetRepository
import com.ceaver.assin.markets.Title

class AssetDetailOverviewViewModel(val title: Title, val label: String?) : ViewModel() {

    val asset = AssetRepository.loadAllAssetsObserved().map { it.single { it.title.id == title.id && it.label == label } }

    class Factory(val title: Title, val label: String?) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return AssetDetailOverviewViewModel(title, label) as T
        }
    }
}