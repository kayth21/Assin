package com.ceaver.assin.action

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.ceaver.assin.markets.TitleEntity
import kotlinx.android.parcel.Parcelize
import java.math.BigDecimal
import java.time.LocalDate

@Parcelize
@Entity(tableName = "action",
        indices = [Index(value = ["titleId"])],
        foreignKeys = [
            ForeignKey(
                    entity = TitleEntity::class,
                    parentColumns = arrayOf("id"),
                    childColumns = arrayOf("titleId"),
                    onDelete = ForeignKey.RESTRICT) // TODO but what if..?
        ])
data class ActionEntity(
        @PrimaryKey(autoGenerate = true)
        var id: Long = 0,
        val actionType: ActionType,
        var actionDate: LocalDate = LocalDate.now(),
        val sourcePositionIds: List<Int>? = null,
        var quantity: BigDecimal? = null,
        var titleId: String? = null,
        var label: String? = null,
        val valueFiat: BigDecimal? = null,
        val valueCrypto: BigDecimal? = null,
        var comment: String? = null
) : Parcelable