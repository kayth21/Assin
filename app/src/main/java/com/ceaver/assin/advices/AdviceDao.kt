package com.ceaver.adviceadvisor.advices

import android.arch.persistence.room.*
import android.arch.persistence.room.OnConflictStrategy.*

@Dao
interface AdviceDao {

    @Query("select * from advice where id = :id")
    fun loadAdvice(id: Long): com.ceaver.assin.advices.Advice

    @Query("select * from advice where tradeId = :tradeId")
    fun loadAdvicesFromTrade(tradeId: Long): List<com.ceaver.assin.advices.Advice>

    @Query("select * from advice")
    fun loadAllAdvices(): List<com.ceaver.assin.advices.Advice>

    @Insert(onConflict = FAIL)
    fun insertAdvice(advice: com.ceaver.assin.advices.Advice)

    @Update
    fun updateAdvice(advice: com.ceaver.assin.advices.Advice)

    @Delete
    fun deleteAdvice(advice: com.ceaver.assin.advices.Advice)

    @Query("delete from advice")
    fun deleteAllAdvices()

}