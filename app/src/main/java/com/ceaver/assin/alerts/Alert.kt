package com.ceaver.assin.alerts

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.ceaver.assin.assets.Symbol

@Entity(tableName = "alert")
data class Alert(//
        @ColumnInfo(name = "id") @PrimaryKey(autoGenerate = true) var id: Long = 0,
        @ColumnInfo(name = "symbol") val symbol: Symbol,
        @ColumnInfo(name = "alertType") val alertType: AlertType,
        @ColumnInfo(name = "source") val source: Double,
        @ColumnInfo(name = "target") val target: Double,
        @ColumnInfo(name = "message") val message: String) 