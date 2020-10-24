package com.ceaver.assin.action

import com.ceaver.assin.R
import com.ceaver.assin.positions.Position
import org.apache.commons.csv.CSVRecord
import java.math.BigDecimal
import java.math.MathContext
import java.time.LocalDate

data class Withdraw(
        override val id: Long = 0,
        override val date: LocalDate = LocalDate.now(),
        val sourcePositionId: Int,
        val sourcePosition: Position? = null,
        val valueCrypto: BigDecimal,
        val valueFiat: BigDecimal,
        override val comment: String? = null
) : Action {

    companion object Factory {
        fun fromDto(actionDto: ActionDto): Withdraw {
            require(ActionType.WITHDRAW == actionDto.action.actionType)
            return Withdraw(
                    id = actionDto.action.id,
                    date = actionDto.action.actionDate,
                    sourcePositionId = actionDto.action.sourcePositionIds!![0],
                    valueFiat = actionDto.action.valueFiat!!,
                    valueCrypto = actionDto.action.valueCrypto!!,
                    comment = actionDto.action.comment
            )
        }

        fun fromPosition(position: Position): Withdraw {
            return Withdraw(
                    sourcePositionId = position.id,
                    valueFiat = position.title.fiatQuotes.price.toBigDecimal(MathContext.DECIMAL32).times(position.quantity),
                    valueCrypto = position.title.cryptoQuotes.price.toBigDecimal(MathContext.DECIMAL32).times(position.quantity)
            )
        }

        fun fromImport(csvRecord: CSVRecord): Withdraw {
            require(ActionType.WITHDRAW.name == csvRecord.get(0))
            return Withdraw(
                    date = LocalDate.parse(csvRecord.get(1)),
                    sourcePositionId = csvRecord.get(2).toInt(),
                    valueFiat = csvRecord.get(3).toBigDecimal(),
                    valueCrypto = csvRecord.get(4).toBigDecimal(),
                    comment = csvRecord.get(5).ifEmpty { null }
            )
        }
    }

    override fun toExport(): List<String> {
        return listOf(
                ActionType.WITHDRAW.name,
                date.toString(),
                sourcePositionId.toString(),
                valueFiat.toPlainString(),
                valueCrypto.toPlainString(),
                comment.orEmpty()
        )
    }

    override fun toActionEntity(): ActionEntity {
        return ActionEntity(
                id = id,
                actionType = ActionType.WITHDRAW,
                actionDate = date,
                sourcePositionIds = listOf(sourcePositionId),
                valueFiat = valueFiat,
                valueCrypto = valueCrypto,
                comment = comment
        )
    }

    override fun getActionType(): ActionType = ActionType.WITHDRAW
    override fun getLeftImageResource(): Int = sourcePosition!!.title.getIcon()
    override fun getRightImageResource(): Int = R.drawable.withdraw
    override fun getTitleText(): String = "Withdraw ${sourcePosition!!.title.name} ${if (sourcePosition.label == null) "" else "(${sourcePosition.label})"}"
    override fun getDetailText(): String = "${sourcePosition!!.quantity} ${sourcePosition.title.symbol}"
}