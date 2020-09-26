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
        val quantity: BigDecimal,
        val valueCrypto: BigDecimal,
        val valueFiat: BigDecimal,
        val comment: String? = null,
        val positionId: BigDecimal?
) : Action {

    companion object Factory {
        fun fromDto(actionDto: ActionDto): Withdraw {
            require(ActionType.WITHDRAW == actionDto.action.actionType)
            return Withdraw(
                    id = actionDto.action.id,
                    date = actionDto.action.actionDate,
                    title = actionDto.sellTitle!!.toTitle(),
                    quantity = actionDto.action.sellQuantity!!,
                    valueCrypto = actionDto.action.valueCrypto!!,
                    valueFiat = actionDto.action.valueFiat!!,
                    comment = actionDto.action.comment,
                    positionId = actionDto.action.positionId)
        }

        suspend fun fromImport(csvRecord: CSVRecord): Withdraw {
            require(ActionType.WITHDRAW.name == csvRecord.get(0))
            return Withdraw(
                    date = LocalDate.parse(csvRecord.get(1)),
                    title = TitleRepository.loadBySymbol(csvRecord.get(2)),
                    quantity = csvRecord.get(3).toBigDecimal(),
                    valueCrypto = csvRecord.get(4).toBigDecimal(),
                    valueFiat = csvRecord.get(5).toBigDecimal(),
                    comment = csvRecord.get(6).ifEmpty { null },
                    positionId = csvRecord.get(7).toBigDecimal())
        }

        fun fromPosition(position: Position): Withdraw {
            return Withdraw(
                    quantity = position.quantity,
                    title = position.title,
                    positionId = position.id,
                    valueFiat = position.title.fiatQuotes.price.toBigDecimal(MathContext.DECIMAL32).times(position.quantity),
                    valueCrypto = position.title.cryptoQuotes.price.toBigDecimal(MathContext.DECIMAL32).times(position.quantity)
            )
        }
    }

    override fun toExport(): List<String> {
        return listOf(
                ActionType.WITHDRAW.name,
                date.toString(),
                title.symbol,
                quantity.toPlainString(),
                valueCrypto.toPlainString(),
                valueFiat.toPlainString(),
                comment.orEmpty(),
                positionId!!.toPlainString())
    }

    override fun getEntityId(): Long = id
    override fun getActionType(): ActionType = ActionType.WITHDRAW
    override fun getLeftImageResource(): Int = title.getIcon()
    override fun getRightImageResource(): Int = R.drawable.withdraw
    override fun getActionDate(): LocalDate = date
    override fun getTitleText(): String = "Withdraw ${title.name}"
    override fun getDetailText(): String = "$quantity ${title.symbol}"

    override fun toActionEntity(): ActionEntity {
        return ActionEntity(
                actionType = ActionType.WITHDRAW,
                id = id,
                actionDate = date,
                sellTitleId = title.id,
                sellQuantity = quantity,
                valueCrypto = valueCrypto,
                valueFiat = valueFiat,
                comment = comment,
                positionId = positionId
        )
    }
}