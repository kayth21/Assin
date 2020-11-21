package com.ceaver.assin.assets

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.ceaver.assin.action.ActionRepository
import com.ceaver.assin.assets.overview.AssetOverview
import com.ceaver.assin.assets.overview.AssetOverviewFactory

object AssetRepository {

    suspend fun loadAssetOverview(): AssetOverview =
            AssetOverviewFactory.fromAssets(loadAllAssets())

    fun loadAssetOverviewObserved(): LiveData<AssetOverview> =
            Transformations.map(loadAllAssetsObserved()) { AssetOverviewFactory.fromAssets(it) }

    suspend fun loadAllAssets(): List<Asset> =
            AssetFactory.fromActions(ActionRepository.loadAll())

    fun loadAllAssetsObserved(): LiveData<List<Asset>> =
            Transformations.map(ActionRepository.loadAllObserved()) { AssetFactory.fromActions(it) }

}
