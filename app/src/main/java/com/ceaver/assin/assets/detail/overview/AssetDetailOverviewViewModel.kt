package com.ceaver.assin.assets.detail.overview

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.ceaver.assin.assets.Asset
import com.ceaver.assin.assets.AssetRepository
import com.ceaver.assin.markets.TitleRepository

class AssetDetailOverviewViewModel : ViewModel() {

    val asset = MutableLiveData<Asset>()

    fun init(owner: LifecycleOwner, assetObserver: Observer<Asset>): AssetDetailOverviewViewModel {
        asset.observe(owner, assetObserver)
        return this
    }

    fun loadAsset(symbol: String) {
        TitleRepository.loadTitleBySymbolAsync(symbol, false) {
            AssetRepository.loadAssetAsync(it, false) { asset.postValue(it) }
        }
    }
}