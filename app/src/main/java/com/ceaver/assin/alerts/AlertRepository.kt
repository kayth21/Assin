package com.ceaver.assin.alerts

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.ceaver.assin.database.Database

object AlertRepository {

    suspend fun loadAlert(id: Long): Alert {
        return getAlertDao().loadAlert(id).toAlert()
    }

    fun loadAllAlerts(): List<Alert> {
        return getAlertDao().loadAllAlerts().map { it.toAlert() }
    }

    fun loadAllAlertsObserved(): LiveData<List<Alert>> {
        return Transformations.map(getAlertDao().loadAllAlertsObserved()) { it.map { it.toAlert() } }
    }

    suspend fun saveAlert(alert: Alert) {
        if (alert.id > 0) updateAlert(alert) else insertAlert(alert)
    }

    suspend fun insertAlert(alert: Alert) {
        getAlertDao().insertAlert(alert.toEntity())
    }

    suspend fun insertAlerts(alerts: List<Alert>) {
        getAlertDao().insertAlerts(alerts.map { it.toEntity() })
    }

    suspend fun updateAlert(alert: Alert) {
        getAlertDao().updateAlert(alert.toEntity())
    }

    suspend fun deleteAlert(alert: Alert) {
        getAlertDao().deleteAlert(alert.toEntity())
    }

    suspend fun deleteAllAlerts() {
        getAlertDao().deleteAllAlerts()
    }

    private fun getAlertDao(): AlertDao {
        return getDatabase().alertDao()
    }

    private fun getDatabase(): Database {
        return Database.getInstance()
    }
}