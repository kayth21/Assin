package com.ceaver.assin.alerts

import androidx.room.Embedded
import androidx.room.Relation
import com.ceaver.assin.markets.TitleEntity

data class AlertDto(
    @Embedded
    val alert: AlertEntity,
    @Relation(parentColumn = "titleId", entityColumn = "id")
    val title: TitleEntity,
    @Relation(parentColumn = "referenceTitleId", entityColumn = "id")
    val referenceTitle: TitleEntity
    ) {
        fun toAlert(): Alert {
            return Alert.fromDto(this)
        }
    }