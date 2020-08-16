package com.ceaver.assin.markets.overview

import androidx.lifecycle.ViewModel
import com.ceaver.assin.common.SingleLiveEvent

class MarketOverviewViewModel() : ViewModel() {
     val marketOverview = SingleLiveEvent<MarketOverview>()

    init{
        loadMarketOverview()
    }

    fun loadMarketOverview() {
        MarketOverviewRepository.loadMarketOverviewAsync(false) { marketOverview.postValue(it) }
    }

}