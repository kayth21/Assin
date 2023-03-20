package com.ceaver.assin.intentions.input

import com.ceaver.assin.intentions.IntentionType
import com.ceaver.assin.markets.Title

data class IntentionUiState(
    val state: State = State.READY,
    val active: Boolean,
    val type: IntentionType,
    val quantity: String,
    val quantityErrors: MutableList<String> = mutableListOf(),
    val baseString: String,
    val baseTitle: Title?,
    val baseTitleErrors: MutableList<String> = mutableListOf(),
    val quoteString: String,
    val quoteTitle: Title?,
    val quoteTitleErrors: MutableList<String> = mutableListOf(),
    val target: String,
    val targetErrors: MutableList<String> = mutableListOf(),
    val comment: String
) {
    fun hasErrors(): Boolean {
        return quantityErrors.isNotEmpty() || baseTitleErrors.isNotEmpty() || quoteTitleErrors.isNotEmpty() || targetErrors.isNotEmpty()
    }
}

enum class State {
    BUSY, READY, DONE
}