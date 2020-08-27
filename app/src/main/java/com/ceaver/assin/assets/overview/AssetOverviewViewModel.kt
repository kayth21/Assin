package com.ceaver.assin.assets.overview

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ceaver.assin.assets.AssetRepository
import com.ceaver.assin.common.SingleLiveEvent
import kotlinx.coroutines.launch

class AssetOverviewViewModel() : ViewModel() {

    private val _assetOverview = SingleLiveEvent<AssetOverview>()
    val assetOverview: LiveData<AssetOverview> get() = _assetOverview

    fun loadAssetOverview() {
        viewModelScope.launch {
            val assertOverview = AssetRepository.loadAssetOverview()
            _assetOverview.postValue(assertOverview)
        }
    }
}