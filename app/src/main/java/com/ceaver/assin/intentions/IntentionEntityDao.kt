package com.ceaver.assin.intentions

import androidx.lifecycle.LiveData
import androidx.room.*
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

    @Update
    suspend fun update(intentions: List<IntentionEntity>)

    @Delete
    suspend fun deleteIntention(intention: IntentionEntity)

    @Query("delete from intention")
    suspend fun deleteAllIntentions()
}