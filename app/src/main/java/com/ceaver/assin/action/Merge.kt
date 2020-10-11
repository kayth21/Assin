package com.ceaver.assin.action

import com.ceaver.assin.R
import com.ceaver.assin.markets.Title
import com.ceaver.assin.markets.TitleRepository
import com.ceaver.assin.positions.Position
import org.apache.commons.csv.CSVRecord
import java.math.BigDecimal
import java.time.LocalDate

data class Merge(
        override val id: Long = 0,
        override val date: LocalDate = LocalDate.now(),
        val title: Title,
        val label: String?,
        val valueCrypto: BigDecimal,
        val valueFiat: BigDecimal,
        val quantityPartA: BigDecimal,
        val quantityPartB: BigDecimal,
        val sourcePositionA: BigDecimal,
        val sourcePositionB: BigDecimal,
        val comment: String? = null
) : Action {

    companion object Factory {
        fun fromDto(actionDto: ActionDto): Merge {
            require(ActionType.MERGE == actionDto.action.actionType)
            return Merge(
                    id = actionDto.action.id,
                    date = actionDto.action.actionDate,
                    title = actionDto.mergeTitle!!.toTitle(),
                    label = actionDto.action.mergeLabel,
                    valueCrypto = actionDto.action.valueCrypto!!,
                    valueFiat = actionDto.action.valueFiat!!,
                    quantityPartA = actionDto.action.mergeQuantityA!!,
                    quantityPartB = actionDto.action.mergeQuantityB!!,
                    sourcePositionA = actionDto.action.mergeSourcePositionA!!,
                    sourcePositionB = actionDto.action.mergeSourcePositionB!!,
                    comment = actionDto.action.comment)
        }

        suspend fun fromImport(csvRecord: CSVRecord): Merge {
            require(ActionType.MERGE.name == csvRecord.get(0))
            return Merge(
                    date = LocalDate.parse(csvRecord.get(1)),
                    title = TitleRepository.loadById(csvRecord.get(2)),
                    label = csvRecord.get(3).ifEmpty { null },
                    quantityPartA = csvRecord.get(4).toBigDecimal(),
                    quantityPartB = csvRecord.get(5).toBigDecimal(),
                    sourcePositionA = csvRecord.get(6).toBigDecimal(),
                    sourcePositionB = csvRecord.get(7).toBigDecimal(),
                    valueCrypto = csvRecord.get(8).toBigDecimal(),
                    valueFiat = csvRecord.get(9).toBigDecimal(),
                    comment = csvRecord.get(10).ifEmpty { null })
        }

        fun fromPositions(positionA: Position, positionB: Position): Merge {
            require(positionA.title == positionB.title)
            require(positionA.label == positionB.label)
            require(positionA.closedQuotes == null)
            require(positionB.closedQuotes == null)
            return Merge(
                    date = LocalDate.now(),
                    title = positionA.title,
                    label = positionA.label,
                    quantityPartA = positionA.quantity,
                    quantityPartB = positionB.quantity,
                    sourcePositionA = positionA.id,
                    sourcePositionB = positionB.id,
                    valueCrypto = positionA.currentValuePrimary + positionB.currentValuePrimary,
                    valueFiat = positionB.currentValueSecondary + positionB.currentValueSecondary,
                    comment = null) // TODO
        }
    }

    override fun toExport(): List<String> {
        return listOf(
                ActionType.MERGE.name,
                date.toString(),
                title.id,
                label.orEmpty(),
                quantityPartA.toPlainString(),
                quantityPartB.toPlainString(),
                sourcePositionA.toPlainString(),
                sourcePositionB.toPlainString(),
                valueCrypto.toPlainString(),
                valueFiat.toPlainString(),
                comment.orEmpty())
    }

    override fun toActionEntity(): ActionEntity {
        return ActionEntity(
                actionType = ActionType.MERGE,
                id = id,
                actionDate = date,
                mergeTitleId = title.id,
                mergeLabel = label,
                valueFiat = valueFiat,
                valueCrypto = valueCrypto,
                mergeQuantityA = quantityPartA,
                mergeQuantityB = quantityPartB,
                mergeSourcePositionA = sourcePositionA,
                mergeSourcePositionB = sourcePositionB,
                comment = comment
        )
    }

    override fun getActionType(): ActionType = ActionType.MERGE
    override fun getLeftImageResource(): Int = title.getIcon()
    override fun getRightImageResource(): Int = R.drawable.mds // TODO
    override fun getTitleText(): String = "Merge ${title.name} ${if (label == null) "" else "(${label}) "}Position"
    override fun getDetailText(): String = "$quantityPartA ${title.symbol} and $quantityPartB ${title.symbol} merged into ${quantityPartA.add(quantityPartB)} ${title.symbol}"

}