package com.ceaver.assin.alerts

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.ceaver.assin.assets.Symbol

@Entity(tableName = "alert")
data class Alert(//
        @ColumnInfo(name = "id") @PrimaryKey(autoGenerate = true) var id: Long = 0,
        @ColumnInfo(name = "symbol") var symbol: Symbol = Symbol.BTC,
        @ColumnInfo(name = "reference") var reference: Symbol = Symbol.USD,
        @ColumnInfo(name = "alertType") var alertType: AlertType = AlertType.RECURRING_STABLE,
        @ColumnInfo(name = "source") var source: Double = 0.0,
        @ColumnInfo(name = "target") var target: Double = 0.0,
        @ColumnInfo(name = "message") var message: String = "")