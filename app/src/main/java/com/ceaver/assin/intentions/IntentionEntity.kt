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
                        childColumns = arrayOf("titleId"),
                        onDelete = ForeignKey.CASCADE),
                ForeignKey(
                        entity = Title::class,
                        parentColumns = arrayOf("id"),
                        childColumns = arrayOf("referenceTitleId"),
                        onDelete = ForeignKey.CASCADE)))
class IntentionEntity(
        @PrimaryKey(autoGenerate = true)
        var id: Long = 0,
        val type: IntentionType,
        var titleId: String,
        var amount: BigDecimal? = null,
        var referenceTitleId: String,
        var referencePrice: BigDecimal,
        var creationDate: LocalDate = LocalDate.now(),
        val status: IntentionStatus = IntentionStatus.WAIT,
        var comment: String? = null)
    : Parcelable