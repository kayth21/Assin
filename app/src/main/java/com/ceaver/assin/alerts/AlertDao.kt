package com.ceaver.assin.alerts

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface AlertDao {
    @Query("select * from alert")
    fun loadAllAlerts(): List<Alert>

    @Query("select * from alert")
    fun loadAllAlertsObserved(): LiveData<List<Alert>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertAlert(alert: Alert)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertAlerts(alerts: List<Alert>)

    @Update
    suspend fun updateAlert(alert: Alert)

    @Delete
    suspend fun deleteAlert(alert: Alert)

    @Query("delete from alert")
    suspend fun deleteAllAlerts()

    @Query("select * from alert where id = :id")
    suspend fun loadAlert(id: Long): Alert
}