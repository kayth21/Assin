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
        indices = [Index(value = ["buyTitleId", "sellTitleId", "splitTitleId", "mergeTitleId"])],
        foreignKeys = [
            ForeignKey(
                    entity = TitleEntity::class,
                    parentColumns = arrayOf("id"),
                    childColumns = arrayOf("buyTitleId"),
                    onDelete = ForeignKey.RESTRICT), // TODO but what if..?
            ForeignKey(
                    entity = TitleEntity::class,
                    parentColumns = arrayOf("id"),
                    childColumns = arrayOf("sellTitleId"),
                    onDelete = ForeignKey.RESTRICT), // TODO but what if..?
            ForeignKey(
                    entity = TitleEntity::class,
                    parentColumns = arrayOf("id"),
                    childColumns = arrayOf("splitTitleId"),
                    onDelete = ForeignKey.RESTRICT), // TODO but what if..?
            ForeignKey(
                    entity = TitleEntity::class,
                    parentColumns = arrayOf("id"),
                    childColumns = arrayOf("mergeTitleId"),
                    onDelete = ForeignKey.RESTRICT) // TODO but what if..?
        ])
data class ActionEntity(
        @PrimaryKey(autoGenerate = true)
        var id: Long = 0,
        var actionDate: LocalDate = LocalDate.now(),
        var buyTitleId: String? = null,
        var buyLabel: String? = null,
        var buyQuantity: BigDecimal? = null,
        var sellTitleId: String? = null,
        var sellLabel: String? = null,
        var sellQuantity: BigDecimal? = null,
        var comment: String? = null,
        val actionType: ActionType,
        val positionId: BigDecimal? = null,
        val splitQuantity: BigDecimal? = null,
        val splitRemaining: BigDecimal? = null,
        val splitTitleId: String? = null,
        val splitLabel: String? = null,
        val mergeTitleId: String? = null,
        val mergeLabel: String? = null,
        val mergeQuantityA: BigDecimal? = null,
        val mergeQuantityB: BigDecimal? = null,
        val mergeSourcePositionA: BigDecimal? = null,
        val mergeSourcePositionB: BigDecimal? = null,
        val valueCrypto: BigDecimal? = null,
        val valueFiat: BigDecimal? = null
) : Parcelable