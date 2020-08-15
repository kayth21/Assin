package com.ceaver.assin.assets.overview

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ceaver.assin.assets.AssetRepository
import com.ceaver.assin.common.SingleLiveEvent

class AssetOverviewViewModel(lifecycleOwner: LifecycleOwner, observer: Observer<AssetOverview>) : ViewModel() {

    private val assetOverview = SingleLiveEvent<AssetOverview>()

    init {
        assetOverview.observe(lifecycleOwner, observer)
    }

    fun loadAssetOverview() {
        AssetRepository.loadAssetOverviewAsync(false) { assetOverview.postValue(it) }
    }

    class Factory(val lifecycleOwner: LifecycleOwner, val observer: Observer<AssetOverview>) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return AssetOverviewViewModel(lifecycleOwner, observer) as T
        }
    }
}