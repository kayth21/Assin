package com.ceaver.assin.logging

import android.arch.persistence.room.*

@Dao
interface LogDao {
    @Query("select * from log")
    fun loadAllLogs(): List<Log>

    @Insert(onConflict = OnConflictStrategy.FAIL)
    fun insertLog(log: Log)

    @Update
    fun updateLog(log: Log)

    @Delete
    fun deleteLog(log: Log)

    @Query("delete from log")
    fun deleteAllLog()

    @Query("select * from log where id = :id")
    fun loadLog(id: Long): Log
}