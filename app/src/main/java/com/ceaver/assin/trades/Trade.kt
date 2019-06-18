package com.ceaver.assin.trades

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.ceaver.assin.markets.Title
import java.time.LocalDate

@Entity(tableName = "trade")
data class Trade(
        @ColumnInfo(name = "id") @PrimaryKey(autoGenerate = true) var id: Long = 0,
        @ColumnInfo(name = "tradeDate") var tradeDate: LocalDate = LocalDate.now(), //
        @ColumnInfo(name = "buyTitle") var buyTitle: Title? = null, //
        @ColumnInfo(name = "buyAmount") var buyAmount: Double? = null, //
        @ColumnInfo(name = "sellTitle") var sellTitle: Title? = null, //
        @ColumnInfo(name = "sellAmount") var sellAmount: Double? = null, //
        @ColumnInfo(name = "comment") var comment: String = "") {

    fun getTradeType(): TradeType {
        return when {
            isTrade() -> TradeType.TRADE
            isDeposit() -> TradeType.DEPOSIT
            isWithdraw() -> TradeType.WITHDRAW
            else -> throw IllegalStateException()
        }
    }

    fun isTrade(): Boolean = buyTitle != null && sellTitle != null
    fun isDeposit(): Boolean = buyTitle != null && sellTitle == null
    fun isWithdraw(): Boolean = buyTitle == null && sellTitle != null

    fun getTitles(): Set<Title> {
        return when (getTradeType()) {
            TradeType.TRADE -> setOf(buyTitle!!, sellTitle!!)
            TradeType.DEPOSIT -> setOf(buyTitle!!)
            TradeType.WITHDRAW -> setOf(sellTitle!!)
        }
    }
}