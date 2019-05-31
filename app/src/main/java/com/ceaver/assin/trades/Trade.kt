package com.ceaver.assin.trades

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "trade")
data class Trade(
        @ColumnInfo(name = "id") @PrimaryKey(autoGenerate = true) var id: Long = 0,
        @ColumnInfo(name = "tradeDate") var tradeDate: LocalDate = LocalDate.now(), //
        @ColumnInfo(name = "comment") var comment: String = "", //
        @ColumnInfo(name = "buySymbol") var buySymbol: String = "", //
        @ColumnInfo(name = "buyAmount") var buyAmount: Double = 0.0, //
        @ColumnInfo(name = "sellSymbol") var sellSymbol: String = "", //
        @ColumnInfo(name = "sellAmount") var sellAmount: Double = 0.0)