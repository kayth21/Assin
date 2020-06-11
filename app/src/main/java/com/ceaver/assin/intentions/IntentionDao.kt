package com.ceaver.assin.intentions

import androidx.room.*
@Dao
interface IntentionDao {
    @Query("select * from intention")
    fun loadAllIntentions(): List<Intention>

    @Query("select * from intention where id = :id")
    fun loadIntentionById(id: Long): Intention

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insertIntention(intention: Intention)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insertIntentions(intentions: List<Intention>)

    @Update
    fun updateIntention(intention: Intention)

    @Update
    fun updateIntention(intentions: List<Intention>)

    @Delete
    fun deleteIntention(intention: Intention)

    @Query("delete from intention")
    fun deleteAllIntentions()
}