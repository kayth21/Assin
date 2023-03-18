package com.ceaver.assin.positions.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ceaver.assin.markets.Title
import com.ceaver.assin.positions.PositionRepository

class PositionListViewModel(val title: Title, val label: String?) : ViewModel() {

    val positions = PositionRepository.loadByTitleObserved(title, label)

    class Factory(val title: Title, val label: String?) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return PositionListViewModel(title, label) as T
        }
    }
}