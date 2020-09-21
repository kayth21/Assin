package com.ceaver.assin.alerts

import androidx.room.Embedded
import androidx.room.Relation
import com.ceaver.assin.markets.Title

data class AlertDto(
    @Embedded
    val alert: AlertEntity,
    @Relation(parentColumn = "titleId", entityColumn = "id")
    val title: Title,
    @Relation(parentColumn = "referenceTitleId", entityColumn = "id")
    val referenceTitle: Title
    ) {
        fun toAlert(): Alert {
            return Alert.fromDto(this)
        }
    }