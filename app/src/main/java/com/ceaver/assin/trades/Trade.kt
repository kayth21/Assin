package com.ceaver.assin.trades

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "trade")
data class Trade(
        @ColumnInfo(name = "id")@PrimaryKey(autoGenerate = true) var id: Long = 0,
        @ColumnInfo(name = "coinmarketcapId") var coinmarketcapId: Int = 0, //
        @ColumnInfo(name = "tradeDate") var tradeDate: LocalDate = LocalDate.now(), //
        @ColumnInfo(name = "comment") var comment: String = "", //
        @ColumnInfo(name = "purchasePrice") var purchasePrice: Double = 0.0, //
        @ColumnInfo(name = "purchaseAmount") var purchaseAmount: Double = 0.0, //
        @ColumnInfo(name = "strategies") var strategies: MutableSet<TradeStrategy> = hashSetOf());