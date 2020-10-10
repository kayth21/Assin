package com.ceaver.assin.positions

import androidx.recyclerview.widget.DiffUtil
import com.ceaver.assin.markets.Title
import java.math.BigDecimal
import java.math.MathContext
import java.time.LocalDate

data class Position(
        val id: BigDecimal,
        val title: Title,
        val label: String?,
        val quantity: BigDecimal,
        val openQuotes: Quotes,
        val closedQuotes: Quotes? = null
) {

    fun isActive(): Boolean {
        return closedQuotes == null
    }

    val currentValuePrimary: BigDecimal
        get() {
            return quantity * title.cryptoQuotes.price.toBigDecimal()
        }

    val currentValueSecondary: BigDecimal
        get() {
            return quantity * title.fiatQuotes.price.toBigDecimal()
        }

    val profitLossInPercentToPrimaryTitle: BigDecimal
        get() {
            return (BigDecimal.valueOf(100).divide(openQuotes.valueCrypto, MathContext.DECIMAL32))
                    .times(currentValuePrimary)
                    .subtract(BigDecimal.valueOf(100))
        }

    val profitLossInPercentToSecondaryValue: BigDecimal
        get() {
            return (BigDecimal.valueOf(100).divide(openQuotes.valueFiat, MathContext.DECIMAL32))
                    .times(currentValueSecondary)
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
    data class Quotes(
            val date: LocalDate,
            val valueCrypto: BigDecimal,
            val valueFiat: BigDecimal
    )
}

