package com.ceaver.assin.action

import com.ceaver.assin.R
import com.ceaver.assin.markets.Title
import com.ceaver.assin.markets.TitleRepository
import org.apache.commons.csv.CSVRecord
import java.math.BigDecimal
import java.time.LocalDate

data class Deposit(
        override val id: Long = 0,
        override val date: LocalDate = LocalDate.now(),
        val title: Title,
        val label: String?,
        val quantity: BigDecimal,
        val valueCrypto: BigDecimal,
        val valueFiat: BigDecimal,
        val comment: String?
) : Action {

    companion object Factory {
        fun fromDto(actionDto: ActionDto): Deposit {
            require(ActionType.DEPOSIT == actionDto.action.actionType)
            return Deposit(
                    id = actionDto.action.id,
                    date = actionDto.action.actionDate,
                    title = actionDto.buyTitle!!.toTitle(),
                    label = actionDto.action.buyLabel,
                    quantity = actionDto.action.buyQuantity!!,
                    valueCrypto = actionDto.action.valueCrypto!!,
                    valueFiat = actionDto.action.valueFiat!!,
                    comment = actionDto.action.comment)
        }

        suspend fun fromImport(csvRecord: CSVRecord): Deposit {
            require(ActionType.DEPOSIT.name == csvRecord.get(0))
            return Deposit(
                    date = LocalDate.parse(csvRecord.get(1)),
                    title = TitleRepository.loadById(csvRecord.get(2)),
                    label = csvRecord.get(3).ifEmpty { null },
                    quantity = csvRecord.get(4).toBigDecimal(),
                    valueCrypto = csvRecord.get(5).toBigDecimal(),
                    valueFiat = csvRecord.get(6).toBigDecimal(),
                    comment = csvRecord.get(7).ifEmpty { null })
        }
    }

    override fun toExport(): List<String> {
        return listOf(
                ActionType.DEPOSIT.name,
                date.toString(),
                title.id,
                label.orEmpty(),
                quantity.toPlainString(),
                valueCrypto.toPlainString(),
                valueFiat.toPlainString(),
                comment.orEmpty())
    }

    override fun toActionEntity(): ActionEntity {
        return ActionEntity(
                actionType = ActionType.DEPOSIT,
                id = id,
                actionDate = date,
                buyTitleId = title.id,
                buyLabel = label,
                buyQuantity = quantity,
                valueCrypto = valueCrypto,
                valueFiat = valueFiat,
                comment = comment
        )
    }

    override fun getActionType(): ActionType = ActionType.DEPOSIT
    override fun getLeftImageResource(): Int = R.drawable.deposit
    override fun getRightImageResource(): Int = title.getIcon()
    override fun getTitleText(): String = "Deposit ${title.name} ${if (label == null) "" else "(${label})"}"
    override fun getDetailText(): String = "$quantity ${title.symbol}"
}