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
        val buyQuantity: BigDecimal,
        val sellTitle: Title,
        val sellQuantity: BigDecimal,
        val positionId: BigDecimal?,
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
                    buyQuantity = actionDto.action.buyQuantity!!,
                    sellTitle = actionDto.sellTitle!!.toTitle(),
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
                    buyQuantity = csvRecord.get(3).toBigDecimal(),
                    sellTitle = TitleRepository.loadById(csvRecord.get(4)),
                    sellQuantity = csvRecord.get(5).toBigDecimal(),
                    positionId = csvRecord.get(6).toBigDecimal(),
                    valueCrypto = csvRecord.get(7).toBigDecimal(),
                    valueFiat = csvRecord.get(8).toBigDecimal(),
                    comment = csvRecord.get(9).ifEmpty { null })
        }
    }

    override fun getEntityId(): Long = id
    override fun getActionType(): ActionType = ActionType.TRADE
    override fun getLeftImageResource(): Int = sellTitle.getIcon()
    override fun getRightImageResource(): Int = buyTitle.getIcon()
    override fun getActionDate(): LocalDate = date
    override fun getTitleText(): String = "${sellTitle.name} -> ${buyTitle.name}"
    override fun getDetailText(): String = "$sellQuantity ${sellTitle.symbol} -> $buyQuantity ${buyTitle.symbol}"

    override fun toExport(): List<String> {
        return listOf(
                ActionType.TRADE.name,
                date.toString(),
                buyTitle.id,
                buyQuantity.toPlainString(),
                sellTitle.id,
                sellQuantity.toPlainString(),
                positionId!!.toPlainString(),
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
                buyQuantity = buyQuantity,
                sellTitleId = sellTitle.id,
                sellQuantity = sellQuantity,
                positionId = positionId,
                valueCrypto = valueCrypto,
                valueFiat = valueFiat,
                comment = comment
        )
    }
}