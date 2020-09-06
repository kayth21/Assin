package com.ceaver.assin.action

import com.ceaver.assin.R
import com.ceaver.assin.markets.Title
import com.ceaver.assin.markets.TitleRepository
import org.apache.commons.csv.CSVRecord
import java.math.BigDecimal
import java.time.LocalDate

data class Deposit(
        val id: Long = 0,
        val date: LocalDate = LocalDate.now(),
        val title: Title,
        val amount: BigDecimal,
        val valueBtc: BigDecimal,
        val valueUsd: BigDecimal,
        val comment: String?
) : Action {
    companion object Factory {
        fun fromAction(actionEntity: ActionEntity): Deposit {
            require(ActionType.DEPOSIT == actionEntity.actionType)
            return Deposit(
                    id = actionEntity.id,
                    date = actionEntity.actionDate,
                    title = actionEntity.buyTitle!!,
                    amount = actionEntity.buyAmount!!,
                    valueBtc = actionEntity.valueBtc!!,
                    valueUsd = actionEntity.valueUsd!!,
                    comment = actionEntity.comment)
        }

        suspend fun fromImport(csvRecord: CSVRecord): Deposit {
            require(ActionType.DEPOSIT.name == csvRecord.get(0))
            return Deposit(
                    date = LocalDate.parse(csvRecord.get(1)),
                    title = TitleRepository.loadTitleBySymbol(csvRecord.get(2)),
                    amount = csvRecord.get(3).toBigDecimal(),
                    valueBtc = csvRecord.get(4).toBigDecimal(),
                    valueUsd = csvRecord.get(5).toBigDecimal(),
                    comment = csvRecord.get(6).ifEmpty { null })
        }
    }

    override fun toExport(): List<String> {
        return listOf(
                ActionType.DEPOSIT.name,
                date.toString(),
                title.symbol,
                amount.toPlainString(),
                valueBtc.toPlainString(),
                valueUsd.toPlainString(),
                comment.orEmpty())
    }

    override fun getEntityId(): Long = id
    override fun getActionType(): ActionType = ActionType.DEPOSIT
    override fun getLeftImageResource(): Int = R.drawable.deposit
    override fun getRightImageResource(): Int = title.getIcon()
    override fun getActionDate(): LocalDate = date
    override fun getTitleText(): String = "Deposit ${title.name}"
    override fun getDetailText(): String = "$amount ${title.symbol}"

    override fun toActionEntity(): ActionEntity {
        return ActionEntity(
                actionType = ActionType.DEPOSIT,
                id = id,
                actionDate = date,
                buyTitle = title,
                buyAmount = amount,
                valueBtc = valueBtc,
                valueUsd = valueUsd,
                comment = comment
        )
    }
}