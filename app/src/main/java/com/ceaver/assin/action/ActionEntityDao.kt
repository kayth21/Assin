package com.ceaver.assin.action

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ActionEntityDao {

    @Transaction
    @Query("SELECT * FROM action")
    fun getActionDtosObserved(): LiveData<List<ActionDto>>

    @Transaction
    @Query("SELECT * FROM action")
    suspend fun getActionDtos(): List<ActionDto>

    @Transaction
    @Query("select * from action where id = :id")
    suspend fun loadActionDto(id: Long): ActionDto

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