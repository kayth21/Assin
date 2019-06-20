package com.ceaver.assin.markets

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModel
import com.ceaver.assin.common.SingleLiveEvent

class MarketOverviewViewModel : ViewModel() {
    private val marketOverview = SingleLiveEvent<MarketOverview>()

    fun init(marketOverviewFragment: MarketOverviewFragment, marketOverviewObserver: Observer<MarketOverview>): MarketOverviewViewModel {
        marketOverview.observe(marketOverviewFragment, marketOverviewObserver)
        loadMarketOverview()
        return this
    }

    fun loadMarketOverview() {
        MarketOverviewRepository.loadMarketOverviewAsync(false) { marketOverview.postValue(it) }
    }
}