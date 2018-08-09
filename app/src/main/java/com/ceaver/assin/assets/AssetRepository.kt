package com.ceaver.assin.assets

import android.os.Handler
import android.os.Looper
import com.ceaver.assin.threading.BackgroundThreadExecutor
import org.greenrobot.eventbus.EventBus

object AssetRepository {
    fun loadTrade(id: Long): Asset {
        return getAssetDao().loadAsset(id)
    }

    fun loadAssetAsync(id: Long, callbackInMainThread: Boolean, callback: (Asset) -> Unit) {
        BackgroundThreadExecutor.execute {
            val asset = getAssetDao().loadAsset(id)
            if (callbackInMainThread)
                Handler(Looper.getMainLooper()).post { callback.invoke(asset) }
            else {
                callback.invoke(asset);
            }
        }
    }

    fun loadAllAssets(): List<Asset> {
        return getAssetDao().loadAllAssets()
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

    fun saveAsset(asset: Asset) {
        if (asset.id > 0) updateAsset(asset) else insertAsset(asset)
    }

    fun saveAssetAsync(asset: Asset) {
        if (asset.id > 0) updateAssetAsync(asset) else insertAssetAsync(asset)
    }

    fun insertAsset(asset: Asset) {
        getAssetDao().insertAsset(asset); EventBus.getDefault().post(AssetEvents.Insert())
    }

    fun insertAssetAsync(asset: Asset) {
        BackgroundThreadExecutor.execute { insertAsset(asset) }
    }

    fun updateAsset(asset: Asset) {
        getAssetDao().updateAsset(asset); EventBus.getDefault().post(AssetEvents.Update())
    }

    fun updateAssetAsync(asset: Asset) {
        BackgroundThreadExecutor.execute { updateAsset(asset) }
    }

    fun deleteAsset(asset: Asset) {
        getAssetDao().deleteAsset(asset); EventBus.getDefault().post(AssetEvents.Delete())
    }

    fun deleteAssetAsync(asset: Asset) {
        BackgroundThreadExecutor.execute { deleteAsset(asset) }
    }

    fun deleteAllAssets() {
        getAssetDao().deleteAllAssets(); EventBus.getDefault().post(AssetEvents.DeleteAll())
    }

    fun deleteAllAssetsAsync() {
        BackgroundThreadExecutor.execute { deleteAllAssets() }
    }

    private fun getAssetDao(): AssetDao {
        return AssetRepository.getDatabase().assetDao()
    }

    private fun getDatabase(): com.ceaver.assin.database.Database {
        return com.ceaver.assin.database.Database.getInstance()
    }
}