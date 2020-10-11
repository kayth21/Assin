package com.ceaver.assin.action

import com.ceaver.assin.R
import com.ceaver.assin.markets.Title
import com.ceaver.assin.markets.TitleRepository
import com.ceaver.assin.positions.Position
import org.apache.commons.csv.CSVRecord
import java.math.BigDecimal
import java.time.LocalDate

data class Move(
        override val id: Long = 0,
        override val date: LocalDate = LocalDate.now(),
        val quantity: BigDecimal,
        val title: Title,
        val sourceLabel: String?,
        val targetLabel: String?,
        val positionId: BigDecimal,
        val comment: String? = null
) : Action {

    companion object Factory {
        fun fromDto(actionDto: ActionDto): Move {
            require(ActionType.MOVE == actionDto.action.actionType)
            return Move(
                    id = actionDto.action.id,
                    date = actionDto.action.actionDate,
                    title = actionDto.moveTitle!!.toTitle(),
                    sourceLabel = actionDto.action.moveSourceLabel,
                    targetLabel = actionDto.action.moveTargetLabel,
                    quantity = actionDto.action.moveQuantity!!,
                    positionId = actionDto.action.positionId!!,
                    comment = actionDto.action.comment)
        }

        suspend fun fromImport(csvRecord: CSVRecord): Move {
            require(ActionType.MOVE.name == csvRecord.get(0))
            return Move(
                    date = LocalDate.parse(csvRecord.get(1)),
                    title = TitleRepository.loadById(csvRecord.get(2)),
                    sourceLabel = csvRecord.get(3).ifEmpty { null },
                    quantity = csvRecord.get(4).toBigDecimal(),
                    positionId = csvRecord.get(5).toBigDecimal(),
                    targetLabel = csvRecord.get(6).ifEmpty { null },
                    comment = csvRecord.get(7).ifEmpty { null })
        }

        fun fromPosition(position: Position, label: String?): Move {
            require(position.label != label)
            return Move(
                    date = LocalDate.now(),
                    title = position.title,
                    sourceLabel = position.label,
                    targetLabel = label,
                    quantity = position.quantity,
                    positionId = position.id,
                    comment = null) // TODO
        }
    }

    override fun toActionEntity(): ActionEntity {
        return ActionEntity(
                actionType = ActionType.MOVE,
                id = id,
                actionDate = date,
                moveTitleId = title.id,
                moveSourceLabel = sourceLabel,
                moveTargetLabel = targetLabel,
                moveQuantity = quantity,
                positionId = positionId,
                comment = comment
        )
    }

    override fun toExport(): List<String> {
        return listOf(
                ActionType.MOVE.name,
                date.toString(),
                title.id,
                sourceLabel.orEmpty(),
                quantity.toPlainString(),
                positionId.toPlainString(),
                targetLabel.orEmpty(),
                comment.orEmpty())
    }

    override fun getActionType(): ActionType = ActionType.MOVE
    override fun getLeftImageResource(): Int = title.getIcon()
    override fun getRightImageResource(): Int = R.drawable.mds // TODO
    override fun getTitleText(): String = "Move ${title.name} Position"
    override fun getDetailText(): String = "$quantity ${title.symbol} moved from ${if (sourceLabel == null) "Default" else "$sourceLabel "} to ${if (targetLabel == null) "Default" else "$targetLabel "}"

}