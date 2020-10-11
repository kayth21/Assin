package com.ceaver.assin.assets

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.ceaver.assin.action.ActionRepository
import com.ceaver.assin.assets.overview.AssetOverview
import com.ceaver.assin.assets.overview.AssetOverviewFactory

object AssetRepository {

    fun loadAssetOverviewObserved(): LiveData<AssetOverview> =
            Transformations.map(loadAllAssetsObserved()) { AssetOverviewFactory.fromAssets(it) }

    fun loadAllAssetsObserved(): LiveData<List<Asset>> =
            Transformations.map(ActionRepository.loadAllObserved()) { AssetFactory.fromActions(it) }

}
