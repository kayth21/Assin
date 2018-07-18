package com.ceaver.tradeadvisor.trades

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import java.util.*

@Entity(tableName = "trade")
data class Trade(//
        @ColumnInfo(name = "id")@PrimaryKey(autoGenerate = true) var id: Long,
        @ColumnInfo(name = "coinmarketcapId") val coinmarketcapId: Int, //
        @ColumnInfo(name = "tradeDate") val tradeDate: Date, //
        @ColumnInfo(name = "purchasePrice") val purchasePrice: Double, //
        @ColumnInfo(name = "purchaseAmount") val purchaseAmount: Double) {
}