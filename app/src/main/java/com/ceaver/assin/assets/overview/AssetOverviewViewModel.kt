package com.ceaver.assin.assets.overview

import androidx.lifecycle.ViewModel
import com.ceaver.assin.assets.AssetRepository
import com.ceaver.assin.common.SingleLiveEvent

class AssetOverviewViewModel() : ViewModel() {

    val assetOverview = SingleLiveEvent<AssetOverview>()

    fun loadAssetOverview() {
        AssetRepository.loadAssetOverviewAsync(false) { assetOverview.postValue(it) }
    }
}