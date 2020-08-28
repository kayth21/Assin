package com.ceaver.assin.logging

import com.ceaver.assin.database.Database
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.EventBus
import java.time.LocalDateTime
import java.util.*

object LogRepository {

    suspend fun insertLog(message: String) = withContext(Dispatchers.IO) {
        insertLog(message, UUID.randomUUID())
    }

    suspend fun insertLog(message: String, uuid: UUID) = withContext(Dispatchers.IO) {
        insertLog(Log(0, LocalDateTime.now(), message, uuid))
    }

    suspend fun updateLog(log: Log) = withContext(Dispatchers.IO) {
        getLogDao().updateLog(log)
        EventBus.getDefault().post(LogEvents.Update())
    }

    suspend fun loadLog(identifier: UUID): Log = withContext(Dispatchers.IO) {
        return@withContext getLogDao().loadLog(identifier)
    }

    suspend fun insertLog(log: Log) = withContext(Dispatchers.IO) {
        getLogDao().insertLog(log)
        EventBus.getDefault().post(LogEvents.Insert())
    }

    suspend fun loadAllLogs(): List<Log> = withContext(Dispatchers.IO) {
        return@withContext getLogDao().loadAllLogs()
    }

    private fun getLogDao(): LogDao {
        return getDatabase().logDao()
    }

    private fun getDatabase(): Database {
        return Database.getInstance()
    }
}