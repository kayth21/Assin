package com.ceaver.assin.action

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.ceaver.assin.database.BaseEntityDao

@Dao
interface ActionEntityDao : BaseEntityDao<ActionEntity> {

    @Transaction
    @Query("select * from 'action' where id = :id")
    suspend fun loadById(id: Long): ActionDto

    @Transaction
    @Query("SELECT * FROM 'action'")
    suspend fun loadAll(): List<ActionDto>

    @Transaction
    @Query("select * from 'action'")
    fun loadAllObserved(): LiveData<List<ActionDto>>

    @Transaction
    @Query("select * from 'action' where buyTitleId = :titleId or sellTitleId = :titleId or splitTitleId = :titleId")
    fun loadAllOfTitle(titleId: String):List<ActionDto>

    @Transaction
    @Query("select * from 'action' where buyTitleId = :titleId or sellTitleId = :titleId or splitTitleId = :titleId")
    fun loadAllOfTitleObserved(titleId: String): LiveData<List<ActionDto>>

    @Query("delete from 'action'")
    suspend fun deleteAll()
}