package com.ceaver.assin.assets.overview

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.ceaver.assin.assets.AssetRepository
import com.ceaver.assin.common.SingleLiveEvent

class AssetOverviewViewModel : ViewModel() {

    private val assetOverview = SingleLiveEvent<AssetOverview>()

    fun init(assetOverviewFragment: AssetOverviewFragment, assetOverviewObserver: Observer<AssetOverview>): AssetOverviewViewModel {
        assetOverview.observe(assetOverviewFragment, assetOverviewObserver)
        return this
    }

    fun loadAssetOverview() {
        AssetRepository.loadAssetOverviewAsync(false) { assetOverview.postValue(it) }
    }
}