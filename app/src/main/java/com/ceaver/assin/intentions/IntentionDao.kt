package com.ceaver.assin.intentions

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface IntentionDao {
    @Transaction
    @Query("select * from intention")
    fun loadAllIntentions(): List<IntentionDto>

    @Transaction
    @Query("select * from intention")
    fun loadAllIntentionsObserved(): LiveData<List<IntentionDto>>

    @Transaction
    @Query("select * from intention where id = :id")
    suspend fun loadIntentionById(id: Long): IntentionDto

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertIntention(intention: IntentionEntity)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertIntentions(intentions: List<IntentionEntity>)

    @Update
    suspend fun updateIntention(intention: IntentionEntity)

    @Update
    suspend fun updateIntention(intentions: List<IntentionEntity>)

    @Delete
    suspend fun deleteIntention(intention: IntentionEntity)

    @Query("delete from intention")
    suspend fun deleteAllIntentions()
}