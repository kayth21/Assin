package com.ceaver.assin.action

import androidx.room.*

@Dao
interface ActionDao {

    @Query("select * from action where id = :id")
    fun loadAction(id: Long): Action

    @Query("select * from action")
    fun loadAllActions(): List<Action>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insertAction(action: Action)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insertActions(action: List<Action>)

    @Update
    fun updateAction(action: Action)

    @Delete
    fun deleteAction(action: Action)

    @Query("delete from action")
    fun deleteAllActions()
}