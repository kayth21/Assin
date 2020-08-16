package com.ceaver.assin.assets.detail.overview

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ceaver.assin.assets.Asset
import com.ceaver.assin.assets.AssetRepository
import com.ceaver.assin.markets.Title

class AssetDetailOverviewViewModel() : ViewModel() {

    val asset = MutableLiveData<Asset>()

    fun loadAsset(title: Title) {
        AssetRepository.loadAssetAsync(title, false) { asset.postValue(it) }
    }
}