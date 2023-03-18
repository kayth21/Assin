package com.ceaver.assin.assets

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.ceaver.assin.action.ActionRepository
import com.ceaver.assin.assets.overview.AssetOverview
import com.ceaver.assin.assets.overview.AssetOverviewFactory

object AssetRepository {

    suspend fun loadAssetOverview(): AssetOverview =
            AssetOverviewFactory.fromAssets(loadAllAssets())

    fun loadAssetOverviewObserved(): LiveData<AssetOverview> = loadAllAssetsObserved().map { AssetOverviewFactory.fromAssets(it) }

    suspend fun loadAllAssets(): List<Asset> =
            AssetFactory.fromActions(ActionRepository.loadAll())

    fun loadAllAssetsObserved(): LiveData<List<Asset>> = ActionRepository.loadAllObserved().map { AssetFactory.fromActions(it) }

}
