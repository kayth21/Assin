package com.ceaver.assin.trades

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import java.time.LocalDate
import java.util.*

@Entity(tableName = "trade")
data class Trade(
        @ColumnInfo(name = "id") @PrimaryKey(autoGenerate = true) var id: Long = 0,
        @ColumnInfo(name = "tradeDate") var tradeDate: LocalDate = LocalDate.now(), //
        @ColumnInfo(name = "buySymbol") var buySymbol: Optional<String> = Optional.empty(), //
        @ColumnInfo(name = "buyAmount") var buyAmount: Optional<Double> = Optional.empty(), //
        @ColumnInfo(name = "sellSymbol") var sellSymbol: Optional<String> = Optional.empty(), //
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

    fun isTrade(): Boolean = buySymbol.isPresent && sellSymbol.isPresent
    fun isDeposit(): Boolean = buySymbol.isPresent && !sellSymbol.isPresent
    fun isWithdraw(): Boolean = !buySymbol.isPresent && sellSymbol.isPresent

}


