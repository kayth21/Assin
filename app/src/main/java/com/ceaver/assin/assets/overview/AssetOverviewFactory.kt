package com.ceaver.assin.assets.overview

import com.ceaver.assin.assets.Asset

object AssetOverviewFactory {
    fun fromAssets(assets: List<Asset>): AssetOverview {
        return assets.map { AssetOverview(it.current.valueCrypto, it.current.valueFiat) }
                .fold(AssetOverview()) { x, y -> AssetOverview(x.valueCrypto + y.valueCrypto, x.valueFiat + y.valueFiat) }
    }
}