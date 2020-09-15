package com.ceaver.assin.assets

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.ceaver.assin.action.ActionRepository
import com.ceaver.assin.assets.overview.AssetOverview
import com.ceaver.assin.markets.Title
import com.ceaver.assin.markets.TitleRepository

object AssetRepository {

    fun loadAssetOverviewObserved(): LiveData<AssetOverview> {
        val assetLiveData = loadAllAssetsObserved()
        return Transformations.map(assetLiveData) {
            it.map { AssetOverview(it.btcValue, it.usdValue) }
                    .fold(AssetOverview()) { x, y -> AssetOverview(x.btcValue + y.btcValue, x.usdValue + y.usdValue) }
        }
    }

    fun loadAllAssetsObserved(): LiveData<List<Asset>> {

        return Transformations.switchMap(TitleRepository.loadActiveCryptoTitles()) {
            Transformations.map(ActionRepository.loadAllActionsObserved()) {
                val actions = it

                val assets = actions.map { it.toActionEntity() } // TODO ActionRepository.loadDeposits, loadWithdraws, etc.
                val buyPairs = assets.filter { it.buyTitle != null }.map { Pair(it.buyTitle!!, it.buyAmount!!) }
                val sellPairs = assets.filter { it.sellTitle != null }.map { Pair(it.sellTitle!!, it.sellAmount!!.unaryMinus()) }
                val allPairs = buyPairs + sellPairs


                allPairs.groupBy { it.first }
                        .map { Pair(it.key, it.value.map { it.second }.reduce { x, y -> x + y }) }
                        .map {
                            Asset(
                                    title = it.first,
                                    amount = it.second,
                                    btcValue = it.first.priceBtc!!.toBigDecimal().times(it.second),
                                    usdValue = it.first.priceUsd!!.toBigDecimal().times(it.second))
                        }

            }
        }
    }

    suspend fun loadAllAssets(): List<Asset> {
        val assets = ActionRepository.loadAllActions().map { it.toActionEntity() } // TODO ActionRepository.loadDeposits, loadWithdraws, etc.
        val buyPairs = assets.filter { it.buyTitle != null }.map { Pair(it.buyTitle!!, it.buyAmount!!) }
        val sellPairs = assets.filter { it.sellTitle != null }.map { Pair(it.sellTitle!!, it.sellAmount!!.unaryMinus()) }
        val allPairs = buyPairs + sellPairs
        return allPairs.groupBy { it.first }
                .map { Pair(it.key, it.value.map { it.second }.reduce { x, y -> x + y }) }
                .map {
                    Asset(
                            title = it.first,
                            amount = it.second,
                            btcValue = it.first.priceBtc!!.toBigDecimal().times(it.second),
                            usdValue = it.first.priceUsd!!.toBigDecimal().times(it.second))
                }
    }

    fun loadAssetObserved(title: Title): LiveData<Asset> {
        return Transformations.switchMap(TitleRepository.loadActiveCryptoTitles()) {
            Transformations.map(ActionRepository.loadAllActionsObserved()) {
                val actions = it

                val actionEntities = actions.map { it.toActionEntity() }.filter { it.buyTitle?.symbol == title.symbol || it.sellTitle?.symbol == title.symbol } // TODO ActionRepository.loadDeposits, loadWithdraws, etc.
                val buyActions = actionEntities.filter { it.buyTitle?.symbol == title.symbol }.map { it.buyAmount!! }
                val sellActions = actionEntities.filter { it.sellTitle?.symbol == title.symbol }.map { it.sellAmount!!.unaryMinus() }
                val allActions = buyActions + sellActions
                val amount = allActions.reduce { x, y -> x.add(y) }
                Asset(
                        title = title,
                        amount = amount,
                        btcValue = title.priceBtc!!.toBigDecimal().times(amount),
                        usdValue = title.priceUsd!!.toBigDecimal().times(amount))
            }

        }
    }
}