package com.ceaver.assin.action

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.ceaver.assin.markets.Title
import kotlinx.android.parcel.Parcelize
import java.math.BigDecimal
import java.time.LocalDate

@Parcelize
@Entity(tableName = "action",
        foreignKeys = arrayOf(
                ForeignKey(
                        entity = Title::class,
                        parentColumns = arrayOf("id"),
                        childColumns = arrayOf("buyTitle"),
                        onDelete = ForeignKey.RESTRICT), // TODO but what if..?
                ForeignKey(
                        entity = Title::class,
                        parentColumns = arrayOf("id"),
                        childColumns = arrayOf("sellTitle"),
                        onDelete = ForeignKey.RESTRICT), // TODO but what if..?
                ForeignKey(
                        entity = Title::class,
                        parentColumns = arrayOf("id"),
                        childColumns = arrayOf("splitTitle"),
                        onDelete = ForeignKey.RESTRICT))) // TODO but what if..?
data class ActionEntity(
        @ColumnInfo(name = "id") @PrimaryKey(autoGenerate = true) var id: Long = 0,
        @ColumnInfo(name = "actionDate") var actionDate: LocalDate = LocalDate.now(),
        @ColumnInfo(name = "buyTitle") var buyTitle: Title? = null,
        @ColumnInfo(name = "buyAmount") var buyAmount: BigDecimal? = null,
        @ColumnInfo(name = "sellTitle") var sellTitle: Title? = null,
        @ColumnInfo(name = "sellAmount") var sellAmount: BigDecimal? = null,
        @ColumnInfo(name = "comment") var comment: String? = null,
        @ColumnInfo(name = "actionType") val actionType: ActionType,
        @ColumnInfo(name = "positionId") val positionId: BigDecimal? = null,
        @ColumnInfo(name = "splitAmount") val splitAmount: BigDecimal? = null,
        @ColumnInfo(name = "splitRemaining") val splitRemaining: BigDecimal? = null,
        @ColumnInfo(name = "splitTitle") val splitTitle: Title? = null,
        @ColumnInfo(name = "valueBtc") val valueBtc: BigDecimal? = null,
        @ColumnInfo(name = "valueUsd") val valueUsd: BigDecimal? = null
) : Parcelable {

    fun toAction(): Action {
        return when (actionType) {
            ActionType.TRADE -> Trade.fromAction(this)
            ActionType.SPLIT -> Split.fromAction(this)
            ActionType.WITHDRAW -> Withdraw.fromAction(this)
            ActionType.DEPOSIT -> Deposit.fromAction(this)
        }
    }
}