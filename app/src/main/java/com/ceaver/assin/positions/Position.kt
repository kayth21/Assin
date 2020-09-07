package com.ceaver.assin.positions

import androidx.recyclerview.widget.DiffUtil
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
        val openValueUsd: BigDecimal,
        val closeDate: LocalDate? = null,
        val closeValueBtc: BigDecimal? = null,
        val closeValueUsd: BigDecimal? = null
) {

    fun isActive(): Boolean {
        return closeValueBtc == null && closeValueUsd == null && closeDate == null
    }

    val currentValueInBtc: BigDecimal
        get() {
            return amount * title.priceBtc!!.toBigDecimal()
        }

    val currentValueInUsd: BigDecimal
        get() {
            return amount * title.priceUsd!!.toBigDecimal()
        }

    val profitLossInPercentToBtc: BigDecimal
        get() {
            return (BigDecimal.valueOf(100)
                    .divide(openValueBtc, MathContext.DECIMAL32))
                    .times(currentValueInBtc)
                    .subtract(BigDecimal.valueOf(100))
        }

    val profitLossInPercentToUsd: BigDecimal
        get() {
            return (BigDecimal.valueOf(100)
                    .divide(openValueUsd, MathContext.DECIMAL32))
                    .times(currentValueInUsd)
                    .subtract(BigDecimal.valueOf(100))
        }

    object Difference : DiffUtil.ItemCallback<Position>() {
        override fun areItemsTheSame(oldItem: Position, newItem: Position): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Position, newItem: Position): Boolean {
            return oldItem == newItem
        }
    }

}