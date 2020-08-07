package com.ceaver.assin.action

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ceaver.assin.markets.Title
import com.ceaver.assin.positions.Position
import kotlinx.android.parcel.Parcelize
import java.math.BigDecimal
import java.math.MathContext
import java.time.LocalDate

@Parcelize
@Entity(tableName = "action")
data class Action(
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

    companion object {
        val ACTION_ID = "com.ceaver.assin.actions.Action.actionId"
        val ACTION_TYPE = "com.ceaver.assin.actions.Action.actionType"
        val SYMBOL = "com.ceaver.assin.actions.Action.symbol"

        fun withdraw(position: Position) : Action {
            return Action(
                    actionType = ActionType.WITHDRAW,
                    sellAmount = position.amount,
                    sellTitle = position.title,
                    positionId = position.id,
                    valueUsd = position.title.priceUsd!!.toBigDecimal(MathContext.DECIMAL32).times(position.amount),
                    valueBtc = position.title.priceBtc!!.toBigDecimal(MathContext.DECIMAL32).times(position.amount)
            )
        }

        fun split(position: Position, amount: BigDecimal) : Action {
            return Action(
                    actionType = ActionType.SPLIT,
                    splitAmount = amount,
                    splitRemaining = position.amount.minus(amount),
                    splitTitle = position.title,
                    positionId = position.id
            )
        }
    }
}