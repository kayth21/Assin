package com.ceaver.assin.action

import androidx.room.*

@Dao
interface ActionEntityDao {

    @Query("select * from action where id = :id")
    suspend fun loadActionEntity(id: Long): ActionEntity

    @Query("select * from action")
    suspend fun loadAllActionEntities(): List<ActionEntity>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertActionEntity(actionEntity: ActionEntity)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertActionEntities(actionEntity: List<ActionEntity>)

    @Update
    suspend fun updateActionEntity(actionEntity: ActionEntity)

    @Delete
    suspend fun deleteActionEntity(actionEntity: ActionEntity)

    @Query("delete from action")
    suspend fun deleteAllActionEntities()
}