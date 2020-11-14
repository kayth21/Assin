package com.ceaver.assin.alerts

import androidx.room.Embedded
import androidx.room.Relation
import com.ceaver.assin.markets.TitleEntity

data class AlertDto(
        @Embedded
        val alert: AlertEntity,
        @Relation(parentColumn = "baseTitleId", entityColumn = "id")
        val baseTitle: TitleEntity?,
        @Relation(parentColumn = "quoteTitleId", entityColumn = "id")
        val quoteTitle: TitleEntity?
) {
    fun toAlert(): Alert {
        return when (alert.type) {
            AlertType.PRICE -> PriceAlert.fromDto(this)
            AlertType.PORTFOLIO -> TODO()
            AlertType.MARKETCAP -> TODO()
            AlertType.DOMINANCE -> TODO()
            AlertType.RANKING -> TODO()
        }
    }
}