package com.ceaver.assin.action

import com.ceaver.assin.markets.Title
import com.ceaver.assin.markets.TitleRepository
import org.apache.commons.csv.CSVRecord
import java.math.BigDecimal
import java.time.LocalDate

data class Trade(
        val id: Long = 0,
        val date: LocalDate = LocalDate.now(),
        val buyTitle: Title,
        val buyAmount: BigDecimal,
        val sellTitle: Title,
        val sellAmount: BigDecimal,
        val positionId: BigDecimal?,
        val valueBtc: BigDecimal,
        val valueUsd: BigDecimal,
        val comment: String?
) : IAction {

    companion object Factory {
        fun fromAction(action: Action): Trade {
            require(ActionType.TRADE == action.actionType)
            return Trade(
                    id = action.id,
                    date = action.actionDate,
                    buyTitle = action.buyTitle!!,
                    buyAmount = action.buyAmount!!,
                    sellTitle = action.sellTitle!!,
                    sellAmount = action.sellAmount!!,
                    positionId = action.positionId,
                    valueBtc = action.valueBtc!!,
                    valueUsd = action.valueUsd!!,
                    comment = action.comment)
        }

        suspend fun fromImport(csvRecord: CSVRecord): Trade {
            require(ActionType.TRADE.name == csvRecord.get(0))
            return Trade(
                    date = LocalDate.parse(csvRecord.get(1)),
                    buyTitle = TitleRepository.loadTitleBySymbol(csvRecord.get(2)),
                    buyAmount = csvRecord.get(3).toBigDecimal(),
                    sellTitle = TitleRepository.loadTitleBySymbol(csvRecord.get(4)),
                    sellAmount = csvRecord.get(5).toBigDecimal(),
                    positionId = csvRecord.get(6).toBigDecimal(),
                    valueBtc = csvRecord.get(7).toBigDecimal(),
                    valueUsd = csvRecord.get(8).toBigDecimal(),
                    comment = csvRecord.get(9).ifEmpty { null })
        }
    }

    override fun getActionType(): ActionType = ActionType.TRADE
    override fun getLeftImageResource(): Int = sellTitle.getIcon()
    override fun getRightImageResource(): Int = buyTitle.getIcon()
    override fun getActionDate(): LocalDate = date
    override fun getTitleText(): String = "${sellTitle.name} -> ${buyTitle.name}"
    override fun getDetailText(): String = "$sellAmount ${sellTitle.symbol} -> $buyAmount ${buyTitle.symbol}"

    override fun toExport(): List<String> {
        return listOf(
                ActionType.TRADE.name,
                date.toString(),
                buyTitle.symbol,
                buyAmount.toPlainString(),
                sellTitle.symbol,
                sellAmount.toPlainString(),
                positionId!!.toPlainString(),
                valueBtc.toPlainString(),
                valueUsd.toPlainString(),
                comment.orEmpty())
    }

    override fun toAction(): Action {
        return Action(
                actionType = ActionType.TRADE,
                id = id,
                actionDate = date,
                buyTitle = buyTitle,
                buyAmount = buyAmount,
                sellTitle = sellTitle,
                sellAmount = sellAmount,
                positionId = positionId,
                valueBtc = valueBtc,
                valueUsd = valueUsd,
                comment = comment
        )
    }
}