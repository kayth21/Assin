package com.ceaver.assin.logging

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.ceaver.assin.database.BaseEntityDao

@Dao
interface LogEntityDao : BaseEntityDao<LogEntity> {

    @Query("select * from log where id = :id")
    suspend fun loadById(id: Long): LogEntity

    @Query("select * from log")
    suspend fun loadAll(): List<LogEntity>

    @Query("select * from log")
    fun loadAllObserved(): LiveData<List<LogEntity>>

    @Query("delete from log")
    suspend fun deleteAllLog()

}