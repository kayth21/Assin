package com.ceaver.assin.action

import com.ceaver.assin.markets.Title
import com.ceaver.assin.markets.TitleRepository
import com.ceaver.assin.positions.Position
import org.apache.commons.csv.CSVRecord
import java.math.BigDecimal
import java.time.LocalDate

data class Trade(
        override val id: Long = 0,
        override val date: LocalDate = LocalDate.now(),
        val buyTitle: Title,
        val buyLabel: String?,
        val buyQuantity: BigDecimal,
        val sellPositionId: Int,
        val sellPosition: Position? = null,
        val valueCrypto: BigDecimal,
        val valueFiat: BigDecimal,
        override val comment: String? = null
) : Action {

    companion object Factory {
        fun fromDto(actionDto: ActionDto): Trade {
            require(ActionType.TRADE == actionDto.action.actionType)
            return Trade(
                    id = actionDto.action.id,
                    date = actionDto.action.actionDate,
                    sellPositionId = actionDto.action.sourcePositionIds!![0],
                    buyTitle = actionDto.title!!.toTitle(),
                    buyLabel = actionDto.action.label,
                    buyQuantity = actionDto.action.quantity!!,
                    valueFiat = actionDto.action.valueFiat!!,
                    valueCrypto = actionDto.action.valueCrypto!!,
                    comment = actionDto.action.comment)
        }

        suspend fun fromImport(csvRecord: CSVRecord): Trade {
            require(ActionType.TRADE.name == csvRecord.get(0))
            return Trade(
                    date = LocalDate.parse(csvRecord.get(1)),
                    sellPositionId = csvRecord.get(2).toInt(),
                    buyQuantity = csvRecord.get(3).toBigDecimal(),
                    buyTitle = TitleRepository.loadById(csvRecord.get(4)),
                    buyLabel = csvRecord.get(5).ifEmpty { null },
                    valueFiat = csvRecord.get(6).toBigDecimal(),
                    valueCrypto = csvRecord.get(7).toBigDecimal(),
                    comment = csvRecord.get(8).ifEmpty { null })
        }
    }

    override fun toExport(): List<String> {
        return listOf(
                ActionType.TRADE.name,
                date.toString(),
                sellPositionId!!.toString(),
                buyTitle.id,
                buyLabel.orEmpty(),
                buyQuantity.toPlainString(),
                valueCrypto.toPlainString(),
                valueFiat.toPlainString(),
                comment.orEmpty())
    }

    override fun toActionEntity(): ActionEntity {
        return ActionEntity(
                id = id,
                actionType = ActionType.TRADE,
                actionDate = date,
                sourcePositionIds = listOf(sellPositionId),
                quantity = buyQuantity,
                titleId = buyTitle.id,
                label = buyLabel,
                valueFiat = valueFiat,
                valueCrypto = valueCrypto,
                comment = comment
        )
    }

    override fun getActionType(): ActionType = ActionType.TRADE
    override fun getLeftImageResource(): Int = sellPosition!!.title.getIcon()
    override fun getRightImageResource(): Int = buyTitle.getIcon()
    override fun getTitleText(): String = "${sellPosition!!.title.name} ${if (sellPosition.label == null) "" else "(${sellPosition.label})"} -> ${buyTitle.name} ${if (buyLabel == null) "" else "(${buyLabel})"}"
    override fun getDetailText(): String = "${sellPosition!!.quantity} ${sellPosition.title.symbol} -> $buyQuantity ${buyTitle.symbol}"
}