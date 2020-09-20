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
            ActionType.TRADE -> Trade.fromDto(this)
            ActionType.SPLIT -> Split.fromDto(this)
            ActionType.WITHDRAW -> Withdraw.fromDto(this)
            ActionType.DEPOSIT -> Deposit.fromDto(this)
        }
    }
}