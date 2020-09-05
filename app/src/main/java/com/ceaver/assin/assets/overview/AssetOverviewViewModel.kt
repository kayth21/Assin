package com.ceaver.assin.assets.overview

import androidx.lifecycle.ViewModel
import com.ceaver.assin.assets.AssetRepository

class AssetOverviewViewModel() : ViewModel() {

    val assetOverview = AssetRepository.loadAssetOverviewObserved()

}