package com.ceaver.assin.assets.detail.overview

import androidx.lifecycle.*
import com.ceaver.assin.assets.Asset
import com.ceaver.assin.assets.AssetRepository
import com.ceaver.assin.markets.Title
import kotlinx.coroutines.launch

class AssetDetailOverviewViewModel(val title: Title) : ViewModel() {

    private val _asset = MutableLiveData<Asset>()
    val asset: LiveData<Asset> get() = _asset

    fun loadAsset() {
        viewModelScope.launch {
            val asset = AssetRepository.loadAsset(title)
            _asset.postValue(asset)
        }
    }

    class Factory(val title: Title) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return AssetDetailOverviewViewModel(title) as T
        }
    }
}