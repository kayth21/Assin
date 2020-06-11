package com.ceaver.assin.trades

import androidx.room.*

@Dao
interface TradeDao {

    @Query("select * from trade where id = :id")
    fun loadTrade(id: Long): Trade

    @Query("select * from trade")
    fun loadAllTrades(): List<Trade>

    @Insert(onConflict = OnConflictStrategy.FAIL)
    fun insertTrade(trade: Trade)

    @Insert(onConflict = OnConflictStrategy.FAIL)
    fun insertTrades(trade: List<Trade>)

    @Update
    fun updateTrade(trade: Trade)

    @Delete
    fun deleteTrade(trade: Trade)

    @Query("delete from trade")
    fun deleteAllTrades()
}