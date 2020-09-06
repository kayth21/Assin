package com.ceaver.assin.action

import com.ceaver.assin.R
import com.ceaver.assin.markets.Title
import com.ceaver.assin.markets.TitleRepository
import com.ceaver.assin.positions.Position
import org.apache.commons.csv.CSVRecord
import java.math.BigDecimal
import java.math.MathContext
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
) : Action {

    companion object Factory {
        fun fromAction(actionEntity: ActionEntity): Withdraw {
            require(ActionType.WITHDRAW == actionEntity.actionType)
            return Withdraw(
                    id = actionEntity.id,
                    date = actionEntity.actionDate,
                    title = actionEntity.sellTitle!!,
                    amount = actionEntity.sellAmount!!,
                    valueBtc = actionEntity.valueBtc!!,
                    valueUsd = actionEntity.valueUsd!!,
                    comment = actionEntity.comment,
                    positionId = actionEntity.positionId)
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

        fun fromPosition(position: Position): Withdraw {
            return Withdraw(
                    amount = position.amount,
                    title = position.title,
                    positionId = position.id,
                    valueUsd = position.title.priceUsd!!.toBigDecimal(MathContext.DECIMAL32).times(position.amount),
                    valueBtc = position.title.priceBtc!!.toBigDecimal(MathContext.DECIMAL32).times(position.amount)
            )
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

    override fun getEntityId(): Long = id
    override fun getActionType(): ActionType = ActionType.WITHDRAW
    override fun getLeftImageResource(): Int = title.getIcon()
    override fun getRightImageResource(): Int = R.drawable.withdraw
    override fun getActionDate(): LocalDate = date
    override fun getTitleText(): String = "Withdraw ${title.name}"
    override fun getDetailText(): String = "$amount ${title.symbol}"

    override fun toActionEntity(): ActionEntity {
        return ActionEntity(
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