package com.ceaver.assin.action

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ceaver.assin.markets.Title
import java.math.BigDecimal
import java.time.LocalDate

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
        @ColumnInfo(name = "positionId") val positionId: Long? = null,
        @ColumnInfo(name = "splitAmount") val splitAmount: BigDecimal? = null,
        @ColumnInfo(name = "valueInBtc") val valueInBtc: BigDecimal? = null,
        @ColumnInfo(name = "valueInUsd") val valueInUsd: BigDecimal? = null
) {

    fun getTitles(): Set<Title> {
        return when (actionType) {
            ActionType.TRADE -> setOf(buyTitle!!, sellTitle!!)
            ActionType.DEPOSIT -> setOf(buyTitle!!)
            ActionType.WITHDRAW -> setOf(sellTitle!!)
        }
    }

    companion object {
        val ACTION_ID = "com.ceaver.assin.actions.Action.actionId"
        val ACTION_TYPE = "com.ceaver.assin.actions.Action.actionType"
        val SYMBOL = "com.ceaver.assin.actions.Action.symbol"
    }
}