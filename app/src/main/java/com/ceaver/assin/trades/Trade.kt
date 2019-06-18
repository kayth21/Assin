package com.ceaver.assin.trades

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.ceaver.assin.markets.Title
import java.time.LocalDate
import java.util.*

@Entity(tableName = "trade")
data class Trade(
        @ColumnInfo(name = "id") @PrimaryKey(autoGenerate = true) var id: Long = 0,
        @ColumnInfo(name = "tradeDate") var tradeDate: LocalDate = LocalDate.now(), //
        @ColumnInfo(name = "buyTitle") var buyTitle: Title?, //
        @ColumnInfo(name = "buyAmount") var buyAmount: Optional<Double> = Optional.empty(), //
        @ColumnInfo(name = "sellTitle") var sellTitle: Title?, //
        @ColumnInfo(name = "sellAmount") var sellAmount: Optional<Double> = Optional.empty(), //
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
            TradeType.TRADE -> setOf(buyTitle, sellTitle) as Set<Title>
            TradeType.DEPOSIT -> setOf(buyTitle) as Set<Title>
            TradeType.WITHDRAW -> setOf(sellTitle) as Set<Title>
        }
    }
}