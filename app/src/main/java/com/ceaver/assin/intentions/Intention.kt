package com.ceaver.assin.intentions

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.ceaver.assin.markets.Title
import kotlinx.android.parcel.Parcelize
import java.math.BigDecimal
import java.time.LocalDate

@Parcelize
@Entity(tableName = "intention",
        foreignKeys = arrayOf(
                ForeignKey(
                        entity = Title::class,
                        parentColumns = arrayOf("id"),
                        childColumns = arrayOf("title"),
                        onDelete = ForeignKey.CASCADE),
                ForeignKey(
                        entity = Title::class,
                        parentColumns = arrayOf("id"),
                        childColumns = arrayOf("referenceTitle"),
                        onDelete = ForeignKey.CASCADE)))
data class Intention(
        @PrimaryKey(autoGenerate = true)
        var id: Long = 0,
        val type: IntentionType,
        var title: Title,
        var amount: BigDecimal? = null,
        var referenceTitle: Title,
        var referencePrice: BigDecimal,
        var creationDate: LocalDate = LocalDate.now(),
        val status: IntentionStatus = IntentionStatus.WAIT,
        var comment: String? = null)
    : Parcelable {

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