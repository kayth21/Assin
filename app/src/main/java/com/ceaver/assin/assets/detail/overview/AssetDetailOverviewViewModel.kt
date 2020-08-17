package com.ceaver.assin.assets.detail.overview

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ceaver.assin.assets.Asset
import com.ceaver.assin.assets.AssetRepository
import com.ceaver.assin.markets.Title

class AssetDetailOverviewViewModel() : ViewModel() {

    private val _asset = MutableLiveData<Asset>()
    val asset: LiveData<Asset> get() = _asset


    fun loadAsset(title: Title) {
        AssetRepository.loadAssetAsync(title, false) { _asset.postValue(it) }
    }
}