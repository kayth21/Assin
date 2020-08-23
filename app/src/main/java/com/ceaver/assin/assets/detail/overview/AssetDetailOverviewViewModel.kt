package com.ceaver.assin.assets.detail.overview

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ceaver.assin.assets.Asset
import com.ceaver.assin.assets.AssetRepository
import com.ceaver.assin.markets.Title

class AssetDetailOverviewViewModel(val title: Title) : ViewModel() {

    private val _asset = MutableLiveData<Asset>()
    val asset: LiveData<Asset> get() = _asset

    fun loadAsset() {
        AssetRepository.loadAssetAsync(title, false) { _asset.postValue(it) }
    }

    class Factory(val title: Title) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return AssetDetailOverviewViewModel(title) as T
        }
    }
}