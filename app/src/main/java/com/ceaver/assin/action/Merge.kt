package com.ceaver.assin.action

import com.ceaver.assin.R
import com.ceaver.assin.positions.Position
import org.apache.commons.csv.CSVRecord
import java.math.BigDecimal
import java.time.LocalDate

data class Merge(
        override val id: Long = 0,
        override val date: LocalDate = LocalDate.now(),
        val sourcePositionIdA: Int,
        val sourcePositionIdB: Int,
        val sourcePositionA: Position? = null,
        val sourcePositionB: Position? = null,
        val valueFiat: BigDecimal,
        val valueCrypto: BigDecimal,
        override val comment: String? = null
) : Action {

    companion object Factory {
        fun fromDto(actionDto: ActionDto): Merge {
            require(ActionType.MERGE == actionDto.action.actionType)
            return Merge(
                    id = actionDto.action.id,
                    date = actionDto.action.actionDate,
                    sourcePositionIdA = actionDto.action.sourcePositionIds!![0],
                    sourcePositionIdB = actionDto.action.sourcePositionIds[1],
                    valueFiat = actionDto.action.valueFiat!!,
                    valueCrypto = actionDto.action.valueCrypto!!,
                    comment = actionDto.action.comment)
        }

        fun fromImport(csvRecord: CSVRecord): Merge {
            require(ActionType.MERGE.name == csvRecord.get(0))
            return Merge(
                    date = LocalDate.parse(csvRecord.get(1)),
                    sourcePositionIdA = csvRecord.get(2).toInt(),
                    sourcePositionIdB = csvRecord.get(3).toInt(),
                    valueFiat = csvRecord.get(4).toBigDecimal(),
                    valueCrypto = csvRecord.get(5).toBigDecimal(),
                    comment = csvRecord.get(6).ifEmpty { null })
        }
    }

    override fun toExport(): List<String> {
        return listOf(
                ActionType.MERGE.name,
                date.toString(),
                sourcePositionIdA.toString(),
                sourcePositionIdB.toString(),
                valueFiat.toPlainString(),
                valueCrypto.toPlainString(),
                comment.orEmpty())
    }

    override fun toActionEntity(): ActionEntity {
        return ActionEntity(
                id = id,
                actionType = ActionType.MERGE,
                actionDate = date,
                sourcePositionIds = listOf(sourcePositionIdA, sourcePositionIdB),
                valueFiat = valueFiat,
                valueCrypto = valueCrypto,
                comment = comment
        )
    }

    override fun getActionType(): ActionType = ActionType.MERGE
    override fun getLeftImageResource(): Int = sourcePositionA!!.title.getIcon()
    override fun getRightImageResource(): Int = R.drawable.mds // TODO
    override fun getTitleText(): String = "Merge ${sourcePositionA!!.title.name} ${if (sourcePositionA.label == null) "" else "(${sourcePositionA.label}) "}Position"
    override fun getDetailText(): String = "${sourcePositionA!!.quantity} ${sourcePositionA.title.symbol} and ${sourcePositionB!!.quantity} ${sourcePositionB.title.symbol} merged into ${sourcePositionA.quantity.add(sourcePositionB.quantity)} ${sourcePositionA.title.symbol}"

}