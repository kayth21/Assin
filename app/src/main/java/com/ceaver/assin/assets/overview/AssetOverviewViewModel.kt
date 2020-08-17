package com.ceaver.assin.assets.overview

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.ceaver.assin.assets.AssetRepository
import com.ceaver.assin.common.SingleLiveEvent

class AssetOverviewViewModel() : ViewModel() {

    private val _assetOverview = SingleLiveEvent<AssetOverview>()
    val assetOverview: LiveData<AssetOverview> get() = _assetOverview

    fun loadAssetOverview() {
        AssetRepository.loadAssetOverviewAsync(false) { _assetOverview.postValue(it) }
    }
}