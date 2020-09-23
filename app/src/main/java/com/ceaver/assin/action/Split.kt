package com.ceaver.assin.action

import com.ceaver.assin.R
import com.ceaver.assin.markets.Title
import com.ceaver.assin.markets.TitleRepository
import com.ceaver.assin.positions.Position
import org.apache.commons.csv.CSVRecord
import java.math.BigDecimal
import java.time.LocalDate

data class Split(
        val id: Long = 0,
        val date: LocalDate = LocalDate.now(),
        val title: Title,
        val quantity: BigDecimal,
        val remaining: BigDecimal,
        val positionId: BigDecimal,
        val comment: String? = null
) : Action {

    companion object Factory {
        fun fromDto(actionDto: ActionDto): Split {
            require(ActionType.SPLIT == actionDto.action.actionType)
            return Split(
                    id = actionDto.action.id,
                    date = actionDto.action.actionDate,
                    title = actionDto.splitTitle!!.toTitle(),
                    quantity = actionDto.action.splitQuantity!!,
                    remaining = actionDto.action.splitRemaining!!,
                    positionId = actionDto.action.positionId!!,
                    comment = actionDto.action.comment)
        }

        suspend fun fromImport(csvRecord: CSVRecord): Split {
            require(ActionType.SPLIT.name == csvRecord.get(0))
            return Split(
                    date = LocalDate.parse(csvRecord.get(1)),
                    title = TitleRepository.loadTitleBySymbol(csvRecord.get(2)),
                    quantity = csvRecord.get(3).toBigDecimal(),
                    remaining = csvRecord.get(4).toBigDecimal(),
                    positionId = csvRecord.get(5).toBigDecimal(),
                    comment = csvRecord.get(6).ifEmpty { null })
        }
        fun fromPosition(position: Position, quantity: BigDecimal): Split {
            return Split(
                    quantity = quantity,
                    remaining = position.quantity.minus(quantity),
                    title = position.title,
                    positionId = position.id
            )
        }
    }

    override fun toExport(): List<String> {
        return listOf(
                ActionType.SPLIT.name,
                date.toString(),
                title.symbol,
                quantity.toPlainString(),
                remaining.toPlainString(),
                positionId.toPlainString(),
                comment.orEmpty())
    }

    override fun getEntityId(): Long = id
    override fun getActionType(): ActionType = ActionType.SPLIT
    override fun getLeftImageResource(): Int = title.getIcon()
    override fun getRightImageResource(): Int = R.drawable.split
    override fun getActionDate(): LocalDate = date
    override fun getTitleText(): String = "Split ${title.name} position"
    override fun getDetailText(): String = "${quantity.add(remaining)} ${title.symbol} splitted into $quantity ${title.symbol} and $remaining ${title.symbol}"

    override fun toActionEntity(): ActionEntity {
        return ActionEntity(
                actionType = ActionType.SPLIT,
                id = id,
                actionDate = date,
                splitTitleId = title.id,
                splitQuantity = quantity,
                splitRemaining = remaining,
                positionId = positionId,
                comment = comment
        )
    }

}