package com.ceaver.assin.markets.overview

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.ceaver.assin.common.SingleLiveEvent

class MarketOverviewViewModel() : ViewModel() {
    private val _marketOverview = SingleLiveEvent<MarketOverview>()
    val marketOverview: LiveData<MarketOverview>
        get() = _marketOverview

    init {
        loadMarketOverview()
    }

    fun loadMarketOverview() {
        MarketOverviewRepository.loadMarketOverviewAsync(false) { _marketOverview.postValue(it) }
    }

}