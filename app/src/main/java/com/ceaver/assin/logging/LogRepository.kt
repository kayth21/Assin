package com.ceaver.assin.logging

import android.os.Handler
import android.os.Looper
import com.ceaver.assin.database.Database
import com.ceaver.assin.threading.BackgroundThreadExecutor
import org.greenrobot.eventbus.EventBus
import java.time.LocalDateTime

object LogRepository {

    fun insert(message: String) {
        getLogDao().insertLog(Log(0, LocalDateTime.now(), message))
        EventBus.getDefault().post(LogEvents.Insert())
    }

    fun loadAllLogs(): List<Log> {
        return getLogDao().loadAllLogs()
    }

    fun loadAllLogsAsync(callbackInMainThread: Boolean, callback: (List<Log>) -> Unit) {
        BackgroundThreadExecutor.execute {
            val logs = loadAllLogs()
            if (callbackInMainThread)
                Handler(Looper.getMainLooper()).post { callback.invoke(logs) }
            else
                callback.invoke(logs)
        }
    }

    private fun getLogDao(): LogDao {
        return getDatabase().logDao()
    }

    private fun getDatabase(): Database {
        return Database.getInstance()
    }
}