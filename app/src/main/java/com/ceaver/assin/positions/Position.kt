package com.ceaver.assin.positions

import com.ceaver.assin.markets.Title
import java.math.BigDecimal
import java.math.MathContext
import java.time.LocalDate

data class Position(
        val id: BigDecimal,
        val title: Title,
        val amount: BigDecimal,
        val openDate: LocalDate,
        val openValueBtc: BigDecimal,
        val openVlaueUsd: BigDecimal,
        val closeDate: LocalDate? = null,
        val closeValueBtc: BigDecimal? = null,
        val closeValueUsd: BigDecimal? = null
) {

    fun isActive(): Boolean {
        return closeValueBtc == null && closeValueUsd == null && closeDate == null
    }

    fun currentValueInBtc(): BigDecimal {
        return amount * title.priceBtc!!.toBigDecimal()
    }

    fun currentValueInUsd(): BigDecimal {
        return amount * title.priceUsd!!.toBigDecimal()
    }

    fun profitLossInPercentToBtc(): BigDecimal {
        return ((BigDecimal.valueOf(100)
                .divide(openValueBtc, MathContext.DECIMAL32))
                .times(currentValueInBtc()))
                .subtract(BigDecimal.valueOf(100))
    }

    fun profitLossInPercentToUsd(): BigDecimal {
        return ((BigDecimal.valueOf(100)
                .divide(openVlaueUsd, MathContext.DECIMAL32))
                .times(currentValueInUsd()))
                .subtract(BigDecimal.valueOf(100))
    }
}