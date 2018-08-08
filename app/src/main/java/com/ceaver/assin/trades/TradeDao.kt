package com.ceaver.assin.trades

import android.arch.persistence.room.*
import android.arch.persistence.room.OnConflictStrategy.*

@Dao
interface TradeDao {
    @Query("select * from trade")
    fun loadAllTrades(): List<Trade>

    @Insert(onConflict = FAIL)
    fun insertTrade(trade: Trade)

    @Update
    fun updateTrade(trade: Trade)

    @Delete
    fun deleteTrade(trade: Trade)

    @Query("delete from trade")
    fun deleteAllTrades()

    @Query("select * from trade where id = :id")
    fun loadTrade(id: Long): Trade
}