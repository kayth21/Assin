package com.ceaver.assin.alerts

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.ceaver.assin.database.Database

object AlertRepository {

    suspend fun loadById(id: Long): Alert =
            dao.loadById(id).toAlert()

    fun loadAll(): List<Alert> =
            dao.loadAll().map { it.toAlert() }

    fun loadAllObserved(): LiveData<List<Alert>> =
            Transformations.map(dao.loadAllObserved()) { it.map { it.toAlert() } }

    suspend fun insert(alert: Alert) =
            alert.toEntity().let { dao.insert(it) }

    suspend fun insert(alerts: List<Alert>) =
            alerts.map { it.toEntity() }.let { dao.insert(it) }

    suspend fun update(alert: Alert) =
            alert.toEntity().let { dao.update(it) }

    suspend fun delete(alert: Alert) =
            alert.toEntity().let { dao.delete(it) }

    suspend fun deleteAll() =
            dao.deleteAll()

    suspend fun save(alert: Alert) =
            if (alert.id > 0) update(alert) else insert(alert)

    private val dao: AlertEntityDao
        get() = database.alertDao()

    private val database: Database
        get() =
            Database.getInstance()
}