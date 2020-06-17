package com.ceaver.assin.trades

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ceaver.assin.markets.Title
import java.math.BigDecimal
import java.time.LocalDate

@Entity(tableName = "trade")
data class Trade(
        @ColumnInfo(name = "id") @PrimaryKey(autoGenerate = true) var id: Long = 0,
        @ColumnInfo(name = "tradeDate") var tradeDate: LocalDate = LocalDate.now(), //
        @ColumnInfo(name = "buyTitle") var buyTitle: Title? = null, //
        @ColumnInfo(name = "buyAmount") var buyAmount: BigDecimal? = null, //
        @ColumnInfo(name = "sellTitle") var sellTitle: Title? = null, //
        @ColumnInfo(name = "sellAmount") var sellAmount: BigDecimal? = null, //
        @ColumnInfo(name = "comment") var comment: String? = null) {

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

    companion object {
        val TRADE_ID = "com.ceaver.assin.trades.Trades.tradeId"
        val TRADE_TYPE = "com.ceaver.assin.trades.Trades.tradeType"
        val SYMBOL = "com.ceaver.assin.trades.Trades.symbol"
    }
}