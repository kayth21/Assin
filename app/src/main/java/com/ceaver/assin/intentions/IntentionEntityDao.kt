package com.ceaver.assin.intentions

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.ceaver.assin.database.BaseEntityDao

@Dao
interface IntentionEntityDao : BaseEntityDao<IntentionEntity> {

    @Transaction
    @Query("select * from intention where id = :id")
    suspend fun loadById(id: Long): IntentionDto

    @Transaction
    @Query("select * from intention")
    fun loadAll(): List<IntentionDto>

    @Transaction
    @Query("select * from intention")
    fun loadAllObserved(): LiveData<List<IntentionDto>>

    @Query("delete from intention")
    suspend fun deleteAll()
}