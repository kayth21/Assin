package com.ceaver.assin.intentions

import androidx.room.Embedded
import androidx.room.Relation
import com.ceaver.assin.markets.TitleEntity

data class IntentionDto(
        @Embedded
        val intention: IntentionEntity,
        @Relation(parentColumn = "titleId", entityColumn = "id")
        val title: TitleEntity,
        @Relation(parentColumn = "referenceTitleId", entityColumn = "id")
        val referenceTitle: TitleEntity
) {
    fun toIntention(): Intention {
        return Intention.fromDto(this)
    }
}