package com.ceaver.assin.intentions

import androidx.room.Embedded
import androidx.room.Relation
import com.ceaver.assin.markets.TitleEntity

data class IntentionDto(
        @Embedded
        val intention: IntentionEntity,
        @Relation(parentColumn = "baseTitleId", entityColumn = "id")
        val baseTitle: TitleEntity,
        @Relation(parentColumn = "quoteTitleId", entityColumn = "id")
        val quoteTitle: TitleEntity
) {
    fun toIntention(): Intention {
        return Intention.fromDto(this)
    }
}