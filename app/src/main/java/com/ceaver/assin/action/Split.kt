package com.ceaver.assin.action

import com.ceaver.assin.R
import com.ceaver.assin.positions.Position
import org.apache.commons.csv.CSVRecord
import java.math.BigDecimal
import java.time.LocalDate

data class Split(
        override val id: Long = 0,
        override val date: LocalDate = LocalDate.now(),
        val sourcePositionId: Int,
        val sourcePosition: Position? = null,
        val quantity: BigDecimal,
        override val comment: String? = null
) : Action {

    companion object Factory {
        fun fromDto(actionDto: ActionDto): Split {
            require(ActionType.SPLIT == actionDto.action.actionType)
            return Split(
                    id = actionDto.action.id,
                    date = actionDto.action.actionDate,
                    sourcePositionId = actionDto.action.sourcePositionIds!![0],
                    quantity = actionDto.action.quantity!!,
                    comment = actionDto.action.comment)
        }

        suspend fun fromImport(csvRecord: CSVRecord): Split {
            require(ActionType.SPLIT.name == csvRecord.get(0))
            return Split(
                    date = LocalDate.parse(csvRecord.get(1)),
                    sourcePositionId = csvRecord.get(2).toInt(),
                    quantity = csvRecord.get(3).toBigDecimal(),
                    comment = csvRecord.get(4).ifEmpty { null })
        }
    }

    override fun toExport(): List<String> {
        return listOf(
                ActionType.SPLIT.name,
                date.toString(),
                sourcePositionId.toString(),
                quantity.toPlainString(),
                comment.orEmpty())
    }

    override fun toActionEntity(): ActionEntity {
        return ActionEntity(
                actionType = ActionType.SPLIT,
                id = id,
                actionDate = date,
                sourcePositionIds = listOf(sourcePositionId),
                quantity = quantity,
                comment = comment
        )
    }

    override fun getActionType(): ActionType = ActionType.SPLIT
    override fun getLeftImageResource(): Int = sourcePosition!!.title.getIcon()
    override fun getRightImageResource(): Int = R.drawable.split
    override fun getTitleText(): String = "Split ${sourcePosition!!.title.name} ${if (sourcePosition.label == null) "" else "(${sourcePosition.label}) "}Position"
    override fun getDetailText(): String = "${quantity.add(sourcePosition!!.quantity - quantity)} ${sourcePosition.title.symbol} splitted into $quantity ${sourcePosition.title.symbol} and ${sourcePosition.quantity - quantity} ${sourcePosition.title.symbol}"
}