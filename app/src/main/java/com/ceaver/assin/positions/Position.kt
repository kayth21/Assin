package com.ceaver.assin.positions

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ceaver.assin.action.Action
import com.ceaver.assin.markets.Title
import java.math.BigDecimal
import java.math.MathContext

@Entity(tableName = "position")
class Position(
        @ColumnInfo(name = "id") @PrimaryKey(autoGenerate = true) var id: Long = 0,
        @ColumnInfo(name = "title") var title: Title,
        @ColumnInfo(name = "amount") var amount: BigDecimal,
        @ColumnInfo(name = "openPriceBtc") var openPriceBtc: BigDecimal, //
        @ColumnInfo(name = "openPriceUsd") var openPriceUsd: BigDecimal, //
        @ColumnInfo(name = "closePriceBtc") var closePriceBtc: BigDecimal? = null, //
        @ColumnInfo(name = "closePriceUsd") var closePriceUsd: BigDecimal? = null) {
    constructor(action: Action) : this(
            id = 0,
            title = action.buyTitle!!,
            amount = action.buyAmount!!,
            openPriceBtc = action.buyTitle!!.priceBtc!!.toBigDecimal(),
            openPriceUsd = action.buyTitle!!.priceUsd!!.toBigDecimal()
    )

    fun isActive(): Boolean {
        return closePriceBtc == null && closePriceUsd == null
    }

    fun currentValueInBtc(): BigDecimal {
        return amount * title.priceBtc!!.toBigDecimal()
    }

    fun currentValueInUsd(): BigDecimal {
        return amount * title.priceUsd!!.toBigDecimal()
    }

    fun profitLossInPercentToBtc(): BigDecimal {
        return ((BigDecimal.valueOf(100)
                .divide((openPriceBtc.times(amount)), MathContext.DECIMAL32))
                .times(currentValueInBtc()))
                .subtract(BigDecimal.valueOf(100))
    }

    fun profitLossInPercentToUsd(): BigDecimal {
        return ((BigDecimal.valueOf(100)
                .divide((openPriceUsd.times(amount)), MathContext.DECIMAL32))
                .times(currentValueInUsd()))
                .subtract(BigDecimal.valueOf(100))
    }
}