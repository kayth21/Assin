package com.ceaver.assin.alerts

import android.os.Handler
import android.os.Looper
import com.ceaver.assin.database.Database
import com.ceaver.assin.threading.BackgroundThreadExecutor
import org.greenrobot.eventbus.EventBus

object AlertRepository {

    fun loadAlert(id: Long): Alert {
        return getAlertDao().loadAlert(id)
    }

    fun loadAlertAsync(id: Long, callbackInMainThread: Boolean, callback: (Alert) -> Unit) {
        BackgroundThreadExecutor.execute {
            val alert = getAlertDao().loadAlert(id)
            if (callbackInMainThread)
                Handler(Looper.getMainLooper()).post { callback.invoke(alert) }
            else {
                callback.invoke(alert);
            }
        }
    }

    fun loadAllAlerts(): List<Alert> {
        return getAlertDao().loadAllAlerts()
    }

    fun loadAllAlertsAsync(callbackInMainThread: Boolean, callback: (List<Alert>) -> Unit) {
        BackgroundThreadExecutor.execute {
            val alerts = loadAllAlerts()
            if (callbackInMainThread)
                Handler(Looper.getMainLooper()).post { callback.invoke(alerts) }
            else
                callback.invoke(alerts)
        }
    }

    fun saveAlert(alert: Alert) {
        if (alert.id > 0) updateAlert(alert) else insertAlert(alert)
    }

    fun saveAlertAsync(alert: Alert, callbackInMainThread: Boolean, callback: () -> Unit) {
        if (alert.id > 0) updateAlertAsync(alert, callbackInMainThread, callback) else insertAlertAsync(alert, callbackInMainThread, callback)
    }

    fun insertAlert(alert: Alert) {
        AlertRepository.getAlertDao().insertAlert(alert); EventBus.getDefault().post(AlertEvents.Insert())
    }

    fun insertAlertAsync(alert: Alert, callbackInMainThread: Boolean, callback: () -> Unit) {
        BackgroundThreadExecutor.execute { insertAlert(alert); if (callbackInMainThread) Handler(Looper.getMainLooper()).post(callback) else callback.invoke() }
    }

    fun updateAlert(alert: Alert) {
        AlertRepository.getAlertDao().updateAlert(alert); EventBus.getDefault().post(AlertEvents.Update())
    }

    fun updateAlertAsync(alert: Alert, callbackInMainThread: Boolean, callback: () -> Unit) {
        BackgroundThreadExecutor.execute { updateAlert(alert); if (callbackInMainThread) Handler(Looper.getMainLooper()).post(callback) else callback.invoke() }
    }

    fun deleteAlert(alert: Alert) {
        AlertRepository.getAlertDao().deleteAlert(alert); EventBus.getDefault().post(AlertEvents.Delete())
    }

    fun deleteAlertAsync(alert: Alert) {
        BackgroundThreadExecutor.execute { deleteAlert(alert) }
    }

    fun deleteAllAlerts() {
        AlertRepository.getAlertDao().deleteAllAlerts(); EventBus.getDefault().post(AlertEvents.DeleteAll())
    }

    fun deleteAllAlertsAsync() {
        BackgroundThreadExecutor.execute { deleteAllAlerts() }
    }

    private fun getAlertDao(): AlertDao {
        return getDatabase().alertDao()
    }

    private fun getDatabase(): Database {
        return Database.getInstance()
    }
}