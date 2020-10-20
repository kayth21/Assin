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
        val quantity: BigDecimal,
        val title: Title,
        val label: String?,
        val valueFiat: BigDecimal,
        val valueCrypto: BigDecimal,
        override val comment: String? = null
) : Action {

    companion object Factory {
        fun fromDto(actionDto: ActionDto): Deposit {
            require(ActionType.DEPOSIT == actionDto.action.actionType)
            return Deposit(
                    id = actionDto.action.id,
                    date = actionDto.action.actionDate,
                    quantity = actionDto.action.quantity!!,
                    title = actionDto.title!!.toTitle(),
                    label = actionDto.action.label,
                    valueFiat = actionDto.action.valueFiat!!,
                    valueCrypto = actionDto.action.valueCrypto!!,
                    comment = actionDto.action.comment)
        }

        suspend fun fromImport(csvRecord: CSVRecord): Deposit {
            require(ActionType.DEPOSIT.name == csvRecord.get(0))
            return Deposit(
                    date = LocalDate.parse(csvRecord.get(1)),
                    quantity = csvRecord.get(2).toBigDecimal(),
                    title = TitleRepository.loadById(csvRecord.get(3)),
                    label = csvRecord.get(4).ifEmpty { null },
                    valueFiat = csvRecord.get(5).toBigDecimal(),
                    valueCrypto = csvRecord.get(6).toBigDecimal(),
                    comment = csvRecord.get(7).ifEmpty { null })
        }
    }

    override fun toExport(): List<String> {
        return listOf(
                ActionType.DEPOSIT.name,
                date.toString(),
                quantity.toPlainString(),
                title.id,
                label.orEmpty(),
                valueFiat.toPlainString(),
                valueCrypto.toPlainString(),
                comment.orEmpty())
    }

    override fun toActionEntity(): ActionEntity {
        return ActionEntity(
                id = id,
                actionType = ActionType.DEPOSIT,
                actionDate = date,
                quantity = quantity,
                titleId = title.id,
                label = label,
                valueFiat = valueFiat,
                valueCrypto = valueCrypto,
                comment = comment
        )
    }

    override fun getActionType(): ActionType = ActionType.DEPOSIT
    override fun getLeftImageResource(): Int = R.drawable.deposit
    override fun getRightImageResource(): Int = title.getIcon()
    override fun getTitleText(): String = "Deposit ${title.name} ${if (label == null) "" else "(${label})"}"
    override fun getDetailText(): String = "$quantity ${title.symbol}"
}