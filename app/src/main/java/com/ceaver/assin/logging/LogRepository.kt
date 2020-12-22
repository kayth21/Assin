package com.ceaver.assin.logging

import androidx.lifecycle.LiveData
import com.ceaver.assin.database.Database
import java.time.LocalDateTime

object LogRepository {

    suspend fun loadLog(id: Long): LogEntity =
            dao.loadById(id)

    suspend fun loadAllLogs(): List<LogEntity> =
            dao.loadAll()

    fun loadAllLogsObserved(): LiveData<List<LogEntity>> =
            dao.loadAllObserved()

    suspend fun insert(message: String) =
            insert(LogEntity(0, LocalDateTime.now(), message))

    suspend fun insert(log: LogEntity) =
            dao.insert(log)

    suspend fun update(log: LogEntity) =
            dao.update(log)

    private val dao: LogEntityDao
        get() {
            return database.logDao()
        }

    private val database: Database
        get() {
            return Database.getInstance()
        }
}