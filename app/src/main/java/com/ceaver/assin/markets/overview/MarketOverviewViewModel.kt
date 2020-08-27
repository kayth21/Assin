package com.ceaver.assin.markets.overview

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ceaver.assin.common.SingleLiveEvent
import kotlinx.coroutines.launch

class MarketOverviewViewModel() : ViewModel() {
    private val _marketOverview = SingleLiveEvent<MarketOverview>()
    val marketOverview: LiveData<MarketOverview> get() = _marketOverview

    init {
        loadMarketOverview()
    }

    fun loadMarketOverview() {
        viewModelScope.launch {
            val result = MarketOverviewRepository.loadMarketOverview()
            _marketOverview.postValue(result)
        }
    }

}