package com.ceaver.assin.action

import androidx.room.Embedded
import androidx.room.Relation
import com.ceaver.assin.markets.TitleEntity

data class ActionDto (
        @Embedded
        val action: ActionEntity,
        @Relation(parentColumn = "buyTitleId", entityColumn = "id")
        val buyTitle: TitleEntity?,
        @Relation(parentColumn = "sellTitleId", entityColumn = "id")
        val sellTitle: TitleEntity?,
        @Relation(parentColumn = "splitTitleId", entityColumn = "id")
        val splitTitle: TitleEntity?
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