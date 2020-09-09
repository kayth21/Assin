package com.ceaver.assin.assets.list

import androidx.lifecycle.ViewModel
import com.ceaver.assin.AssinWorkers
import com.ceaver.assin.assets.AssetRepository

class AssetListViewModel : ViewModel() {

    val assets = AssetRepository.loadAllAssetsObserved()
    val loading = AssinWorkers.running

    fun refresh() {
        AssinWorkers.completeUpdate()
    }

}