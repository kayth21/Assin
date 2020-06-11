package com.ceaver.assin.alerts

import androidx.room.*

@Dao
interface AlertDao {
    @Query("select * from alert")
    fun loadAllAlerts(): List<Alert>

    @Insert(onConflict = OnConflictStrategy.FAIL)
    fun insertAlert(alert: Alert)

    @Insert(onConflict = OnConflictStrategy.FAIL)
    fun insertAlerts(alerts: List<Alert>)

    @Update
    fun updateAlert(alert: Alert)

    @Delete
    fun deleteAlert(alert: Alert)

    @Query("delete from alert")
    fun deleteAllAlerts()

    @Query("select * from alert where id = :id")
    fun loadAlert(id: Long): Alert
}