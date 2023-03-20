package com.ceaver.assin.intentions.input

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ceaver.assin.intentions.Intention
import com.ceaver.assin.intentions.IntentionRepository
import com.ceaver.assin.intentions.IntentionType
import com.ceaver.assin.markets.Title
import com.ceaver.assin.markets.TitleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class IntentionViewModel(val intention: Intention) : ViewModel() {

    lateinit var titles: List<Title>

    init {
        viewModelScope.launch {
            titles = TitleRepository.loadAll()
        }
    }

    private val _uiState = MutableStateFlow(
        IntentionUiState(
            active = intention.active,
            type = intention.type,
            quantity = intention.quantity?.toString() ?: "",
            baseString = intention.baseTitle.id,
            baseTitle = intention.baseTitle,
            quoteString = intention.quoteTitle.id,
            quoteTitle = intention.quoteTitle,
            target = intention.target.toString(),
            comment = intention.comment.orEmpty()
        )
    )
    val uiData: StateFlow<IntentionUiState> = _uiState.asStateFlow()

    fun update() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(state = State.BUSY)
            IntentionRepository.update(intention.copy(_uiState.value))
            _uiState.value = _uiState.value.copy(state = State.DONE)
        }
    }

    fun updateType(type: IntentionType) {
        _uiState.value = _uiState.value.copy(
            type = type
        )
    }

    fun updateActive(active: Boolean) {
        _uiState.value = _uiState.value.copy(
            active = active
        )
    }

    fun updateQuantity(quantity: String) {
        val errors = mutableListOf<String>()
        if (quantity.isEmpty() || quantity.matches("^\\d*\\.?\\d*\$".toRegex()).not()) {
            errors.add("Invalid decimal number.")
        }
        _uiState.value = _uiState.value.copy(
            quantity = quantity,
            quantityErrors = errors
        )
    }

    fun updateBaseTitle(baseString: String) {
        val errors = mutableListOf<String>()
        val title: Title? = titles.firstOrNull { it.id == baseString }
        if (title == null) {
            errors.add("Unknown title.")
        }
        _uiState.value = _uiState.value.copy(
            baseString = baseString,
            baseTitle = title,
            baseTitleErrors = errors
        )
    }

    fun updateQuoteTitle(quoteString: String) {
        val errors = mutableListOf<String>()
        val title: Title? = titles.firstOrNull { it.id == quoteString }
        if (title == null) {
            errors.add("Unknown title.")
        }
        _uiState.value = _uiState.value.copy(
            quoteString = quoteString,
            quoteTitle = title,
            quoteTitleErrors = errors
        )
    }

    fun updateTarget(target: String) {
        val errors = mutableListOf<String>()
        if (target.isEmpty() || target.matches("^\\d*\\.?\\d*\$".toRegex()).not()) {
            errors.add("Invalid decimal number.")
        }
        _uiState.value = _uiState.value.copy(
            target = target,
            targetErrors = errors
        )
    }

    fun updateComment(comment: String) {
        _uiState.value = _uiState.value.copy(
            comment = comment
        )
    }

    class Factory(val intention: Intention) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return IntentionViewModel(intention) as T
        }
    }
}