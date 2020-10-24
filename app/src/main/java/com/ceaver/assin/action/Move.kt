package com.ceaver.assin.action

import com.ceaver.assin.R
import com.ceaver.assin.positions.Position
import org.apache.commons.csv.CSVRecord
import java.time.LocalDate

data class Move(
        override val id: Long = 0,
        override val date: LocalDate = LocalDate.now(),
        val sourcePositionId: Int,
        val sourcePosition: Position? = null,
        val targetLabel: String?,
        override val comment: String? = null
) : Action {

    companion object Factory {
        fun fromDto(actionDto: ActionDto): Move {
            require(ActionType.MOVE == actionDto.action.actionType)
            return Move(
                    id = actionDto.action.id,
                    date = actionDto.action.actionDate,
                    sourcePositionId = actionDto.action.sourcePositionIds!![0],
                    targetLabel = actionDto.action.label,
                    comment = actionDto.action.comment)
        }

        fun fromImport(csvRecord: CSVRecord): Move {
            require(ActionType.MOVE.name == csvRecord.get(0))
            return Move(
                    date = LocalDate.parse(csvRecord.get(1)),
                    sourcePositionId = csvRecord.get(2).toInt(),
                    targetLabel = csvRecord.get(3).ifEmpty { null },
                    comment = csvRecord.get(4).ifEmpty { null })
        }
    }

    override fun toExport(): List<String> {
        return listOf(
                ActionType.MOVE.name,
                date.toString(),
                sourcePositionId.toString(),
                targetLabel.orEmpty(),
                comment.orEmpty())
    }

    override fun toActionEntity(): ActionEntity {
        return ActionEntity(
                id = id,
                actionType = ActionType.MOVE,
                actionDate = date,
                sourcePositionIds = listOf(sourcePositionId),
                label = targetLabel,
                comment = comment
        )
    }

    override fun getActionType(): ActionType = ActionType.MOVE
    override fun getLeftImageResource(): Int = sourcePosition!!.title.getIcon()
    override fun getRightImageResource(): Int = R.drawable.mds // TODO
    override fun getTitleText(): String = "Move ${sourcePosition!!.title.name} Position"
    override fun getDetailText(): String = "${sourcePosition!!.quantity} ${sourcePosition.title.symbol} moved from ${if (sourcePosition.label == null) "Default" else "${sourcePosition.label} "} to ${if (targetLabel == null) "Default" else "$targetLabel "}"

}