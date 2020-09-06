package com.ceaver.assin.logging

import androidx.lifecycle.LiveData
import com.ceaver.assin.database.Database
import org.greenrobot.eventbus.EventBus
import java.time.LocalDateTime
import java.util.*

object LogRepository {

    suspend fun insertLog(message: String) {
        insertLog(message, UUID.randomUUID())
    }

    suspend fun insertLog(message: String, uuid: UUID) {
        insertLog(Log(0, LocalDateTime.now(), message, uuid))
    }

    suspend fun updateLog(log: Log) {
        getLogDao().updateLog(log)
        EventBus.getDefault().post(LogEvents.Update())
    }

    suspend fun loadLog(identifier: UUID): Log {
        return getLogDao().loadLog(identifier)
    }

    suspend fun insertLog(log: Log) {
        getLogDao().insertLog(log)
        EventBus.getDefault().post(LogEvents.Insert())
    }

    suspend fun loadAllLogs(): List<Log> {
        return getLogDao().loadAllLogs()
    }

    fun loadAllLogsObserved(): LiveData<List<Log>> {
        return getLogDao().loadAllLogsObserved()
    }

    private fun getLogDao(): LogDao {
        return getDatabase().logDao()
    }

    private fun getDatabase(): Database {
        return Database.getInstance()
    }
}