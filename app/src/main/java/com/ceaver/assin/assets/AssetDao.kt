package com.ceaver.assin.assets

import android.arch.persistence.room.*

@Dao
interface AssetDao {
    @Query("select * from asset")
    fun loadAllAssets(): List<Asset>

    @Insert(onConflict = OnConflictStrategy.FAIL)
    fun insertAsset(asset: Asset)

    @Update
    fun updateAsset(asset: Asset)

    @Delete
    fun deleteAsset(asset: Asset)

    @Query("delete from asset")
    fun deleteAllAssets()

    @Query("select * from asset where id = :id")
    fun loadAsset(id: Long): Asset
}