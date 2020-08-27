package com.ceaver.assin.logging

import androidx.room.*
import java.util.*

@Dao
interface LogDao {
    @Query("select * from log")
    suspend fun loadAllLogs(): List<Log>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertLog(log: Log)

    @Update
    suspend fun updateLog(log: Log)

    @Delete
    suspend fun deleteLog(log: Log)

    @Query("delete from log")
    suspend fun deleteAllLog()

    @Query("select * from log where id = :id")
    suspend fun loadLog(id: Long): Log

    @Query("select * from log where uuid = :uuid")
    suspend fun loadLog(uuid: UUID): Log
}