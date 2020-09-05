package com.ceaver.assin.assets.list

import androidx.databinding.ObservableBoolean
import androidx.lifecycle.ViewModel
import com.ceaver.assin.AssinWorkers
import com.ceaver.assin.assets.AssetRepository

class AssetListViewModel : ViewModel() {

    val assets = AssetRepository.loadAllAssetsObserved()
    val loading = ObservableBoolean()

    fun refresh() {
        loading.set(true)
        AssinWorkers.completeUpdate()
    }

}