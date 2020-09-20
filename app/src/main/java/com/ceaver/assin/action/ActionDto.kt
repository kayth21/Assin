package com.ceaver.assin.action

import androidx.room.Embedded
import androidx.room.Relation
import com.ceaver.assin.markets.Title

data class ActionDto (
        @Embedded
        val action: ActionEntity,
        @Relation(parentColumn = "buyTitleId", entityColumn = "id")
        val buyTitle: Title?,
        @Relation(parentColumn = "sellTitleId", entityColumn = "id")
        val sellTitle: Title?,
        @Relation(parentColumn = "splitTitleId", entityColumn = "id")
        val splitTitle: Title?
) {
    fun toAction(): Action {
        return when (action.actionType) {
            ActionType.TRADE -> Trade.fromAction(this)
            ActionType.SPLIT -> Split.fromAction(this)
            ActionType.WITHDRAW -> Withdraw.fromAction(this)
            ActionType.DEPOSIT -> Deposit.fromAction(this)
        }
    }
}