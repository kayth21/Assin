package com.ceaver.assin.action

import com.ceaver.assin.R
import com.ceaver.assin.markets.Title
import com.ceaver.assin.markets.TitleRepository
import org.apache.commons.csv.CSVRecord
import java.math.BigDecimal
import java.time.LocalDate

data class Split(
        val id: Long = 0,
        val date: LocalDate = LocalDate.now(),
        val title: Title,
        val amount: BigDecimal,
        val remaining: BigDecimal,
        val positionId: BigDecimal,
        val comment: String? = null
) : IAction {

    companion object Factory {
        fun fromAction(action: Action): Split {
            require(ActionType.SPLIT == action.actionType)
            return Split(
                    id = action.id,
                    date = action.actionDate,
                    title = action.splitTitle!!,
                    amount = action.splitAmount!!,
                    remaining = action.splitRemaining!!,
                    positionId = action.positionId!!,
                    comment = action.comment)
        }

        suspend fun fromImport(csvRecord: CSVRecord): Split {
            require(ActionType.SPLIT.name == csvRecord.get(0))
            return Split(
                    date = LocalDate.parse(csvRecord.get(1)),
                    title = TitleRepository.loadTitleBySymbol(csvRecord.get(2)),
                    amount = csvRecord.get(3).toBigDecimal(),
                    remaining = csvRecord.get(4).toBigDecimal(),
                    positionId = csvRecord.get(5).toBigDecimal(),
                    comment = csvRecord.get(6).ifEmpty { null })
        }
    }

    override fun toExport(): List<String> {
        return listOf(
                ActionType.SPLIT.name,
                date.toString(),
                title.symbol,
                amount.toPlainString(),
                remaining.toPlainString(),
                positionId.toPlainString(),
                comment.orEmpty())
    }

    override fun getActionType(): ActionType = ActionType.SPLIT
    override fun getLeftImageResource(): Int = title.getIcon()
    override fun getRightImageResource(): Int = R.drawable.split
    override fun getActionDate(): LocalDate = date
    override fun getTitleText(): String = "Split ${title.name} position"
    override fun getDetailText(): String = "${amount.add(remaining)} ${title.symbol} splitted into $amount ${title.symbol} and $remaining ${title.symbol}"

    override fun toAction(): Action {
        return Action(
                actionType = ActionType.SPLIT,
                id = id,
                actionDate = date,
                splitTitle = title,
                splitAmount = amount,
                splitRemaining = remaining,
                positionId = positionId,
                comment = comment
        )
    }

}