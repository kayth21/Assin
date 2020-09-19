package com.ceaver.assin.action

import android.os.Parcelable
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
        @PrimaryKey(autoGenerate = true)
        var id: Long = 0,
        var actionDate: LocalDate = LocalDate.now(),
        var buyTitle: Title? = null,
        var buyAmount: BigDecimal? = null,
        var sellTitle: Title? = null,
        var sellAmount: BigDecimal? = null,
        var comment: String? = null,
        val actionType: ActionType,
        val positionId: BigDecimal? = null,
        val splitAmount: BigDecimal? = null,
        val splitRemaining: BigDecimal? = null,
        val splitTitle: Title? = null,
        val valueCrypto: BigDecimal? = null,
        val valueFiat: BigDecimal? = null
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