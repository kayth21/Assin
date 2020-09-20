package com.ceaver.assin.intentions

import androidx.room.Embedded
import androidx.room.Relation
import com.ceaver.assin.markets.Title

data class IntentionDto (
        @Embedded
        val intention: IntentionEntity,
        @Relation(parentColumn = "titleId", entityColumn = "id")
        val title: Title,
        @Relation(parentColumn = "referenceTitleId", entityColumn = "id")
        val referenceTitle: Title
) {
    fun toIntention(): Intention {
        return Intention.fromDto(this)
    }
}