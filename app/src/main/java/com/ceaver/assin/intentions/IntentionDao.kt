package com.ceaver.assin.intentions

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface IntentionDao {
    @Query("select * from intention")
    fun loadAllIntentions(): List<Intention>

    @Query("select * from intention")
    fun loadAllIntentionsObserved(): LiveData<List<Intention>>

    @Query("select * from intention where id = :id")
    suspend fun loadIntentionById(id: Long): Intention

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertIntention(intention: Intention)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertIntentions(intentions: List<Intention>)

    @Update
    suspend fun updateIntention(intention: Intention)

    @Update
    suspend fun updateIntention(intentions: List<Intention>)

    @Delete
    suspend fun deleteIntention(intention: Intention)

    @Query("delete from intention")
    suspend fun deleteAllIntentions()
}