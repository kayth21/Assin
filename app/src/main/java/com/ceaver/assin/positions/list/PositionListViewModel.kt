package com.ceaver.assin.positions.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ceaver.assin.markets.Title
import com.ceaver.assin.positions.PositionRepository

class PositionListViewModel(val title: Title) : ViewModel() {

    val positions = PositionRepository.loadPositionsObserved(title)

    class Factory(val title: Title) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return PositionListViewModel(title) as T
        }
    }
}