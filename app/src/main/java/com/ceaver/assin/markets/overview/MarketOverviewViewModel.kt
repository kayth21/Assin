package com.ceaver.assin.markets.overview

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ceaver.assin.common.SingleLiveEvent

class MarketOverviewViewModel(lifecycleOwner: LifecycleOwner, observer: Observer<MarketOverview>) : ViewModel() {
    private val marketOverview = SingleLiveEvent<MarketOverview>()

    init{
        marketOverview.observe(lifecycleOwner, observer)
        loadMarketOverview()
    }

    fun loadMarketOverview() {
        MarketOverviewRepository.loadMarketOverviewAsync(false) { marketOverview.postValue(it) }
    }

    class Factory(val lifecycleOwner: LifecycleOwner, val observer: Observer<MarketOverview>) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return MarketOverviewViewModel(lifecycleOwner, observer) as T
        }
    }
}