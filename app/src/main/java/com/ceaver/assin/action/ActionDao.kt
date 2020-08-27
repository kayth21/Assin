package com.ceaver.assin.action

import androidx.room.*

@Dao
interface ActionDao {

    @Query("select * from action where id = :id")
    suspend fun loadAction(id: Long): Action

    @Query("select * from action")
    suspend fun loadAllActions(): List<Action>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertAction(action: Action)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertActions(action: List<Action>)

    @Update
    suspend fun updateAction(action: Action)

    @Delete
    suspend fun deleteAction(action: Action)

    @Query("delete from action")
    suspend fun deleteAllActions()
}