package com.ceaver.assin.positions

import com.ceaver.assin.markets.Title
import java.math.BigDecimal
import java.math.MathContext
import kotlin.random.Random

data class Position(
        val id: Long = Random.nextLong(),
        val title: Title,
        val amount: BigDecimal,
        val openPriceBtc: BigDecimal,
        val openPriceUsd: BigDecimal,
        val closePriceBtc: BigDecimal? = null,
        val closePriceUsd: BigDecimal? = null
) {

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