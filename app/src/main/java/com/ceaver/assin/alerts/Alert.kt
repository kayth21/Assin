package com.ceaver.assin.alerts

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "alert")
data class Alert(//
        @ColumnInfo(name = "id") @PrimaryKey(autoGenerate = true) val id: Long = 0,
        @ColumnInfo(name = "symbol") val symbol: String,
        @ColumnInfo(name = "reference") val reference: String,
        @ColumnInfo(name = "alertType") val alertType: AlertType,
        @ColumnInfo(name = "source") val source: Double,
        @ColumnInfo(name = "target") val target: Double) {

    fun isNew(): Boolean = this.id == 0L;
}