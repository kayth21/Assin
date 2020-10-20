package com.ceaver.assin.positions

import androidx.recyclerview.widget.DiffUtil
import com.ceaver.assin.action.*
import com.ceaver.assin.markets.Title
import java.math.BigDecimal
import java.math.MathContext
import java.time.LocalDate

data class Position(
        val id: Int,
        val quantity: BigDecimal,
        val title: Title,
        val label: String?,
        val open: Quotes,
        val close: Quotes? = null,
        val expired: Boolean = false
) {

    fun isActive(): Boolean {
        return close == null
    }

    fun withdraw(withdraw: Withdraw): Position {
        val closeQuotes = Quotes(date = withdraw.date, valueCrypto = withdraw.valueCrypto, valueFiat = withdraw.valueFiat)
        return copy(close = closeQuotes)
    }

    fun trade(id: Int, trade: Trade): Pair<Position, Position> {
        val quotes = Quotes(date = trade.date, valueCrypto = trade.valueCrypto, valueFiat = trade.valueFiat)
        return copy(close = quotes) to Position(id = id, quantity = trade.buyQuantity, title = trade.buyTitle, label = trade.buyLabel, open = quotes)
    }

    fun split(id: Int, split: Split): Pair<Position, Pair<Position, Position>> {
        fun splitQuote(quote: Quotes, split: BigDecimal, remaining: BigDecimal): Pair<Quotes, Quotes> {
            return Pair(
                    quote.copy(
                            valueCrypto = quote.valueCrypto.divide(split + remaining, MathContext.DECIMAL32).times(split),
                            valueFiat = quote.valueFiat.divide(split + remaining, MathContext.DECIMAL32).times(split)),
                    quote.copy(
                            valueCrypto = quote.valueCrypto.divide(split + remaining, MathContext.DECIMAL32).times(remaining),
                            valueFiat = quote.valueFiat.divide(split + remaining, MathContext.DECIMAL32).times(remaining)))
        }

        val splittedOpenQuotes = splitQuote(open, split.quantity, quantity - split.quantity)
        val splittedCloseQuotes = close?.let { splitQuote(it, split.quantity, quantity - split.quantity) }

        return copy(expired = true) to Pair(
                Position(id = id, quantity = split.quantity, title = title, label = label, open = splittedOpenQuotes.first, close = splittedCloseQuotes?.first),
                Position(id = id.inv(), quantity = quantity - split.quantity, title = title, label = label, open = splittedOpenQuotes.second, close = splittedCloseQuotes?.second)
        )
    }

    fun merge(secondPosition: Position, id: Int, merge: Merge): Pair<Pair<Position, Position>, Position> {
        val closeQuotes1 = Quotes(date = merge.date,
                valueFiat = merge.valueFiat.divide(quantity + secondPosition.quantity, MathContext.DECIMAL32).times(quantity),
                valueCrypto = merge.valueCrypto.divide(quantity + secondPosition.quantity, MathContext.DECIMAL32).times(quantity))
        val closeQuotes2 = Quotes(date = merge.date,
                valueFiat = merge.valueFiat.divide(quantity + secondPosition.quantity, MathContext.DECIMAL32).times(secondPosition.quantity),
                valueCrypto = merge.valueCrypto.divide(quantity + secondPosition.quantity, MathContext.DECIMAL32).times(secondPosition.quantity))

        return Pair(copy(close = closeQuotes1) to secondPosition.copy(close = closeQuotes2),
                Position(id = id, title = title, label = label, quantity = quantity + secondPosition.quantity, open = Quotes(merge.date, merge.valueCrypto, merge.valueFiat))
        )
    }

    fun move(move: Move): Position {
        return copy(label = move.targetLabel)
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
            return (BigDecimal.valueOf(100).divide(open.valueCrypto, MathContext.DECIMAL32))
                    .times(currentValuePrimary)
                    .subtract(BigDecimal.valueOf(100))
        }

    val profitLossInPercentToSecondaryValue: BigDecimal
        get() {
            return (BigDecimal.valueOf(100).divide(open.valueFiat, MathContext.DECIMAL32))
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

    companion object Factory {
        fun fromDeposit(id: Int, deposit: Deposit): Position {
            return Position(
                    id = id,
                    quantity = deposit.quantity,
                    title = deposit.title,
                    label = deposit.label,
                    open = Quotes(
                            date = deposit.date,
                            valueCrypto = deposit.valueCrypto,
                            valueFiat = deposit.valueFiat)
            )
        }
    }
}

