package com.ceaver.assin.assets.detail.overview

import androidx.lifecycle.*
import com.ceaver.assin.assets.Asset
import com.ceaver.assin.assets.AssetRepository
import com.ceaver.assin.markets.Title

class AssetDetailOverviewViewModel(lifecycleOwner: LifecycleOwner, observer: Observer<Asset>) : ViewModel() {

    val asset = MutableLiveData<Asset>()

    init {
        asset.observe(lifecycleOwner, observer)
    }

    fun loadAsset(title: Title) {
        AssetRepository.loadAssetAsync(title, false) { asset.postValue(it) }
    }

    class Factory(val lifecycleOwner: LifecycleOwner, val observer: Observer<Asset>) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return AssetDetailOverviewViewModel(lifecycleOwner, observer) as T
        }
    }
}