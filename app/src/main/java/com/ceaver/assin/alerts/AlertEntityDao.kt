package com.ceaver.assin.alerts

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.ceaver.assin.database.BaseEntityDao

@Dao
interface AlertEntityDao : BaseEntityDao<AlertEntity> {
    @Transaction
    @Query("select * from alert")
    fun loadAll(): List<AlertDto>

    @Transaction
    @Query("select * from alert")
    fun loadAllObserved(): LiveData<List<AlertDto>>

    @Transaction
    @Query("select * from alert where id = :id")
    suspend fun loadById(id: Long): AlertDto

    @Query("delete from alert")
    suspend fun deleteAll()
}