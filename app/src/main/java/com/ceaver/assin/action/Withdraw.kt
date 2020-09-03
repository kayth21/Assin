package com.ceaver.assin.action

import com.ceaver.assin.R
import com.ceaver.assin.markets.Title
import com.ceaver.assin.markets.TitleRepository
import org.apache.commons.csv.CSVRecord
import java.math.BigDecimal
import java.time.LocalDate

data class Withdraw(
        val id: Long = 0,
        val date: LocalDate = LocalDate.now(),
        val title: Title,
        val amount: BigDecimal,
        val valueBtc: BigDecimal,
        val valueUsd: BigDecimal,
        val comment: String? = null,
        val positionId: BigDecimal?
) : IAction {

    companion object Factory {
        fun fromAction(action: Action): Withdraw {
            require(ActionType.WITHDRAW == action.actionType)
            return Withdraw(
                    id = action.id,
                    date = action.actionDate,
                    title = action.sellTitle!!,
                    amount = action.sellAmount!!,
                    valueBtc = action.valueBtc!!,
                    valueUsd = action.valueUsd!!,
                    comment = action.comment,
                    positionId = action.positionId)
        }

        suspend fun fromImport(csvRecord: CSVRecord): Withdraw {
            require(ActionType.WITHDRAW.name == csvRecord.get(0))
            return Withdraw(
                    date = LocalDate.parse(csvRecord.get(1)),
                    title = TitleRepository.loadTitleBySymbol(csvRecord.get(2)),
                    amount = csvRecord.get(3).toBigDecimal(),
                    valueBtc = csvRecord.get(4).toBigDecimal(),
                    valueUsd = csvRecord.get(5).toBigDecimal(),
                    comment = csvRecord.get(6).ifEmpty { null },
                    positionId = csvRecord.get(7).toBigDecimal())
        }
    }


    override fun toExport(): List<String> {
        return listOf(
                ActionType.WITHDRAW.name,
                date.toString(),
                title.symbol,
                amount.toPlainString(),
                valueBtc.toPlainString(),
                valueUsd.toPlainString(),
                comment.orEmpty(),
                positionId!!.toPlainString())
    }

    override fun getActionType(): ActionType = ActionType.WITHDRAW
    override fun getLeftImageResource(): Int = title.getIcon()
    override fun getRightImageResource(): Int = R.drawable.withdraw
    override fun getActionDate(): LocalDate = date
    override fun getTitleText(): String = "Withdraw ${title.name}"
    override fun getDetailText(): String = "$amount ${title.symbol}"

    override fun toAction(): Action {
        return Action(
                actionType = ActionType.WITHDRAW,
                id = id,
                actionDate = date,
                sellTitle = title,
                sellAmount = amount,
                valueBtc = valueBtc,
                valueUsd = valueUsd,
                comment = comment,
                positionId = positionId
        )
    }
}