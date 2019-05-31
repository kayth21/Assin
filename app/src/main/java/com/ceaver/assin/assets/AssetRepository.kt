package com.ceaver.assin.assets

import android.os.Handler
import android.os.Looper
import com.ceaver.assin.threading.BackgroundThreadExecutor
import com.ceaver.assin.trades.TradeRepository

object AssetRepository {

    fun loadAllAssets(): List<Asset> {
        return TradeRepository.loadAllTrades().map { Asset(it.buySymbol, it.buyAmount) }
    }

    fun loadAllAssetsAsync(callbackInMainThread: Boolean, callback: (List<Asset>) -> Unit) {
        BackgroundThreadExecutor.execute {
            val assets = loadAllAssets()
            if (callbackInMainThread)
                Handler(Looper.getMainLooper()).post { callback.invoke(assets) }
            else
                callback.invoke(assets)
        }
    }

}