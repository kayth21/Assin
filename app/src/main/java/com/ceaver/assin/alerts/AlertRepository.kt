package com.ceaver.assin.alerts

import androidx.lifecycle.LiveData
import com.ceaver.assin.database.Database
import org.greenrobot.eventbus.EventBus

object AlertRepository {

    suspend fun loadAlert(id: Long): Alert {
        return getAlertDao().loadAlert(id)
    }

    fun loadAllAlerts(): List<Alert> {
        return getAlertDao().loadAllAlerts()
    }

    fun loadAllAlertsObserved(): LiveData<List<Alert>> {
        return getAlertDao().loadAllAlertsObserved()
    }

    suspend fun saveAlert(alert: Alert) {
        if (alert.id > 0) updateAlert(alert) else insertAlert(alert)
    }

    suspend fun insertAlert(alert: Alert) {
        getAlertDao().insertAlert(alert); EventBus.getDefault().post(AlertEvents.Insert())
    }

    suspend fun insertAlerts(alerts: List<Alert>) {
        getAlertDao().insertAlerts(alerts); EventBus.getDefault().post(AlertEvents.Insert())
    }

    suspend fun updateAlert(alert: Alert) {
        getAlertDao().updateAlert(alert); EventBus.getDefault().post(AlertEvents.Update())
    }

    suspend fun deleteAlert(alert: Alert) {
        getAlertDao().deleteAlert(alert); EventBus.getDefault().post(AlertEvents.Delete())
    }

    suspend fun deleteAllAlerts() {
        getAlertDao().deleteAllAlerts(); EventBus.getDefault().post(AlertEvents.DeleteAll())
    }

    private fun getAlertDao(): AlertDao {
        return getDatabase().alertDao()
    }

    private fun getDatabase(): Database {
        return Database.getInstance()
    }
}