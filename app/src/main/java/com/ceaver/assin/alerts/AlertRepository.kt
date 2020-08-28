package com.ceaver.assin.alerts

import com.ceaver.assin.database.Database
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.EventBus

object AlertRepository {

    suspend fun loadAlert(id: Long): Alert = withContext(Dispatchers.IO) {
        return@withContext getAlertDao().loadAlert(id)
    }

    suspend fun loadAllAlerts(): List<Alert> = withContext(Dispatchers.IO) {
        return@withContext getAlertDao().loadAllAlerts()
    }

    suspend fun saveAlert(alert: Alert) = withContext(Dispatchers.IO) {
        if (alert.id > 0) updateAlert(alert) else insertAlert(alert)
    }

    suspend fun insertAlert(alert: Alert) = withContext(Dispatchers.IO) {
        getAlertDao().insertAlert(alert); EventBus.getDefault().post(AlertEvents.Insert())
    }

    suspend fun insertAlerts(alerts: List<Alert>) = withContext(Dispatchers.IO) {
        getAlertDao().insertAlerts(alerts); EventBus.getDefault().post(AlertEvents.Insert())
    }

    suspend fun updateAlert(alert: Alert) = withContext(Dispatchers.IO) {
        getAlertDao().updateAlert(alert); EventBus.getDefault().post(AlertEvents.Update())
    }

    suspend fun deleteAlert(alert: Alert) = withContext(Dispatchers.IO) {
        getAlertDao().deleteAlert(alert); EventBus.getDefault().post(AlertEvents.Delete())
    }

    suspend fun deleteAllAlerts() = withContext(Dispatchers.IO) {
        getAlertDao().deleteAllAlerts(); EventBus.getDefault().post(AlertEvents.DeleteAll())
    }

    private fun getAlertDao(): AlertDao {
        return getDatabase().alertDao()
    }

    private fun getDatabase(): Database {
        return Database.getInstance()
    }
}