package com.ceaver.assin.intentions

import android.os.Parcelable
import com.ceaver.assin.common.Exportable
import com.ceaver.assin.markets.Title
import com.ceaver.assin.markets.TitleRepository
import kotlinx.android.parcel.Parcelize
import org.apache.commons.csv.CSVRecord
import java.math.BigDecimal
import java.time.LocalDate

@Parcelize
data class Intention(
        var id: Long = 0,
        val type: IntentionType,
        var title: Title,
        var quantity: BigDecimal? = null,
        var referenceTitle: Title,
        var referencePrice: BigDecimal,
        var creationDate: LocalDate = LocalDate.now(),
        val status: IntentionStatus = IntentionStatus.WAIT,
        var comment: String? = null) : Parcelable, Exportable {

    companion object Factory {
        fun fromDto(dto: IntentionDto): Intention {
            return Intention(
                    id = dto.intention.id,
                    quantity = dto.intention.quantity,
                    title = dto.title.toTitle(),
                    comment = dto.intention.comment,
                    creationDate = dto.intention.creationDate,
                    referencePrice = dto.intention.referencePrice,
                    referenceTitle = dto.referenceTitle.toTitle(),
                    status = dto.intention.status,
                    type = dto.intention.type)
        }

        suspend fun fromImport(record: CSVRecord): Intention {
            return Intention(
                    type = IntentionType.valueOf(record.get(0)),
                    title = TitleRepository.loadBySymbol(record.get(1)),
                    quantity = record.get(2).toBigDecimalOrNull(),
                    referenceTitle = TitleRepository.loadBySymbol(record.get(3)),
                    referencePrice = record.get(4).toBigDecimal(),
                    creationDate = LocalDate.parse(record.get(5)),
                    status = IntentionStatus.valueOf(record.get(6)),
                    comment = record.get(7).ifEmpty { null })
        }
    }

    fun toIntentionEntity(): IntentionEntity {
        return IntentionEntity(
                id = id,
                type = type,
                status = status,
                referencePrice = referencePrice,
                creationDate = creationDate,
                comment = comment,
                quantity = quantity,
                referenceTitleId = referenceTitle.id,
                titleId = title.id
        )
    }

    val percentToReferencePrice: BigDecimal
        get() {
            val currentValue = title.cryptoQuotes.price
            val desiredValue = referencePrice.toDouble()

            return when (type) {
                IntentionType.SELL -> currentValue.div(desiredValue).toBigDecimal()
                IntentionType.BUY -> desiredValue.div(currentValue).toBigDecimal()
            }
        }

    // TODO Use extension function
    fun quantityAsString(): String {
        return if (quantity == null) "" else quantity!!.toPlainString()
    }

    fun calculateState(): IntentionStatus {
        return when (percentToReferencePrice.toDouble()) {
            in 0.0..80.0 -> IntentionStatus.WAIT
            in 80.0..100.0 -> IntentionStatus.NEAR
            else -> IntentionStatus.ACT
        }
    }

    override fun toExport(): List<String> {
        return listOf(type.name, title.symbol, quantityAsString(), referenceTitle.symbol, referencePrice.toPlainString(), creationDate.toString(), status.name, comment.orEmpty())
    }
}