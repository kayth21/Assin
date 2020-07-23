package com.ceaver.assin.positions

import com.ceaver.assin.action.Action
import com.ceaver.assin.markets.Title
import java.math.BigDecimal
import java.math.MathContext

data class Position(
        val title: Title,
        val amount: BigDecimal,
        val openPriceBtc: BigDecimal,
        val openPriceUsd: BigDecimal,
        val closePriceBtc: BigDecimal? = null,
        val closePriceUsd: BigDecimal? = null
) {

    companion object {
        fun create(action: Action): Position {
            return Position(
                    title = action.buyTitle!!,
                    amount = action.buyAmount!!,
                    openPriceBtc = action.buyTitle!!.priceBtc!!.toBigDecimal(),
                    openPriceUsd = action.buyTitle!!.priceUsd!!.toBigDecimal()
            )
        }
    }

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