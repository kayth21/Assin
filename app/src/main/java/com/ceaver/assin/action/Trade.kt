package com.ceaver.assin.action

import com.ceaver.assin.markets.Title
import com.ceaver.assin.markets.TitleRepository
import org.apache.commons.csv.CSVRecord
import java.math.BigDecimal
import java.time.LocalDate

data class Trade(
        override val id: Long = 0,
        override val date: LocalDate = LocalDate.now(),
        val buyTitle: Title,
        val buyLabel: String?,
        val buyQuantity: BigDecimal,
        val sellTitle: Title,
        val sellLabel: String?,
        val sellQuantity: BigDecimal,
        val positionId: Long?,
        val valueCrypto: BigDecimal,
        val valueFiat: BigDecimal,
        val comment: String?
) : Action {

    companion object Factory {
        fun fromDto(actionDto: ActionDto): Trade {
            require(ActionType.TRADE == actionDto.action.actionType)
            return Trade(
                    id = actionDto.action.id,
                    date = actionDto.action.actionDate,
                    buyTitle = actionDto.buyTitle!!.toTitle(),
                    buyLabel = actionDto.action.buyLabel,
                    buyQuantity = actionDto.action.buyQuantity!!,
                    sellTitle = actionDto.sellTitle!!.toTitle(),
                    sellLabel = actionDto.action.sellLabel,
                    sellQuantity = actionDto.action.sellQuantity!!,
                    positionId = actionDto.action.positionId,
                    valueCrypto = actionDto.action.valueCrypto!!,
                    valueFiat = actionDto.action.valueFiat!!,
                    comment = actionDto.action.comment)
        }

        suspend fun fromImport(csvRecord: CSVRecord): Trade {
            require(ActionType.TRADE.name == csvRecord.get(0))
            return Trade(
                    date = LocalDate.parse(csvRecord.get(1)),
                    buyTitle = TitleRepository.loadById(csvRecord.get(2)),
                    buyLabel = csvRecord.get(3).ifEmpty { null },
                    buyQuantity = csvRecord.get(4).toBigDecimal(),
                    sellTitle = TitleRepository.loadById(csvRecord.get(5)),
                    sellLabel = csvRecord.get(6).ifEmpty { null },
                    sellQuantity = csvRecord.get(7).toBigDecimal(),
                    positionId = csvRecord.get(8).toLong(),
                    valueCrypto = csvRecord.get(9).toBigDecimal(),
                    valueFiat = csvRecord.get(10).toBigDecimal(),
                    comment = csvRecord.get(11).ifEmpty { null })
        }
    }

    override fun toExport(): List<String> {
        return listOf(
                ActionType.TRADE.name,
                date.toString(),
                buyTitle.id,
                buyLabel.orEmpty(),
                buyQuantity.toPlainString(),
                sellTitle.id,
                sellLabel.orEmpty(),
                sellQuantity.toPlainString(),
                positionId!!.toString(),
                valueCrypto.toPlainString(),
                valueFiat.toPlainString(),
                comment.orEmpty())
    }

    override fun toActionEntity(): ActionEntity {
        return ActionEntity(
                actionType = ActionType.TRADE,
                id = id,
                actionDate = date,
                buyTitleId = buyTitle.id,
                buyLabel = buyLabel,
                buyQuantity = buyQuantity,
                sellTitleId = sellTitle.id,
                sellLabel = sellLabel,
                sellQuantity = sellQuantity,
                positionId = positionId,
                valueCrypto = valueCrypto,
                valueFiat = valueFiat,
                comment = comment
        )
    }

    override fun getActionType(): ActionType = ActionType.TRADE
    override fun getLeftImageResource(): Int = sellTitle.getIcon()
    override fun getRightImageResource(): Int = buyTitle.getIcon()
    override fun getTitleText(): String = "${sellTitle.name} ${if (sellLabel == null) "" else "(${sellLabel})"} -> ${buyTitle.name} ${if (buyLabel == null) "" else "(${buyLabel})"}"
    override fun getDetailText(): String = "$sellQuantity ${sellTitle.symbol} -> $buyQuantity ${buyTitle.symbol}"
}