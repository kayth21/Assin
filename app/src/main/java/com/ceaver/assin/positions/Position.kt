package com.ceaver.assin.positions

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ceaver.assin.markets.Title
import java.math.BigDecimal

@Entity(tableName = "position")
class Position(
        @ColumnInfo(name = "id") @PrimaryKey(autoGenerate = true) var id: Long = 0,
        @ColumnInfo(name = "title") var title: Title,
        @ColumnInfo(name = "amount") var amount: BigDecimal,
        @ColumnInfo(name = "openPriceBtc") var openPriceBtc: BigDecimal, //
        @ColumnInfo(name = "openPriceBtc") var openPriceUsd: BigDecimal, //
        @ColumnInfo(name = "closePriceBtc") var closePriceBtc: BigDecimal? = null, //
        @ColumnInfo(name = "closePriceBtc") var closePriceUsd: BigDecimal? = null) {


    fun isActive(): Boolean {
        return closePriceBtc == null && closePriceUsd == null
    }

    fun profitLossInPercentToBtc() {
        throw NotImplementedError()
    }

    fun profitLossInPercentToUsd() {
        throw NotImplementedError()
    }
}