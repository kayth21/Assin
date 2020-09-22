package com.ceaver.assin.intentions

import android.os.Parcelable
import com.ceaver.assin.markets.Title
import kotlinx.android.parcel.Parcelize
import java.math.BigDecimal
import java.time.LocalDate

@Parcelize
data class Intention(
        var id: Long = 0,
        val type: IntentionType,
        var title: Title,
        var amount: BigDecimal? = null,
        var referenceTitle: Title,
        var referencePrice: BigDecimal,
        var creationDate: LocalDate = LocalDate.now(),
        val status: IntentionStatus = IntentionStatus.WAIT,
        var comment: String? = null) : Parcelable {

    companion object Factory {
        fun fromDto(dto: IntentionDto): Intention {
            return Intention(
                    id = dto.intention.id,
                    amount = dto.intention.amount,
                    title = dto.title.toTitle(),
                    comment = dto.intention.comment,
                    creationDate = dto.intention.creationDate,
                    referencePrice = dto.intention.referencePrice,
                    referenceTitle = dto.referenceTitle.toTitle(),
                    status = dto.intention.status,
                    type = dto.intention.type)
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
                amount = amount,
                referenceTitleId = referenceTitle.id,
                titleId = title.id
        )
    }

    val percentToReferencePrice: BigDecimal
        get() {
            val currentValue = title.cryptoQuotes.price
            val desiredValue = referenceTitle.cryptoQuotes.price * referencePrice.toDouble()

            return when (type) {
                IntentionType.SELL -> (100.div(desiredValue)).times(currentValue).toBigDecimal()
                IntentionType.BUY -> (100.div(currentValue)).times(desiredValue).toBigDecimal()
            }
        }

    // TODO Use extension function
    fun amountAsString(): String {
        return if (amount == null) "" else amount!!.toPlainString()
    }

    fun calculateState(): IntentionStatus {
        return when (percentToReferencePrice.toDouble()) {
            in 0.0..80.0 -> IntentionStatus.WAIT
            in 80.0..100.0 -> IntentionStatus.NEAR
            else -> IntentionStatus.ACT
        }
    }
}