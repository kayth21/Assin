package com.ceaver.assin.alerts

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface AlertDao {
    @Query("select * from alert")
    fun loadAllAlerts(): List<AlertDto>

    @Query("select * from alert")
    fun loadAllAlertsObserved(): LiveData<List<AlertDto>>

    @Query("select * from alert where id = :id")
    suspend fun loadAlert(id: Long): AlertDto

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertAlert(alert: AlertEntity)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertAlerts(alerts: List<AlertEntity>)

    @Update
    suspend fun updateAlert(alert: AlertEntity)

    @Delete
    suspend fun deleteAlert(alert: AlertEntity)

    @Query("delete from alert")
    suspend fun deleteAllAlerts()
}