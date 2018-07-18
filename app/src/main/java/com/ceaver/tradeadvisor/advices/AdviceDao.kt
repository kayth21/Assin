package com.ceaver.adviceadvisor.advices

import android.arch.persistence.room.*
import android.arch.persistence.room.OnConflictStrategy.*
import com.ceaver.tradeadvisor.advices.Advice

@Dao
interface AdviceDao {
    @Query("select * from advice")
    fun loadAdvices(): List<Advice>

    @Insert(onConflict = FAIL)
    fun insertAdvice(advice: Advice)

    @Update
    fun updateAdvice(advice: Advice)

    @Delete
    fun deleteAdvice(advice: Advice)

    @Query("delete from advice")
    fun deleteAllAdvices()

    @Query("select * from advice where id = :id")
    fun loadAdvice(id: Long): Advice
}