package com.ceaver.assin.markets.list

import androidx.lifecycle.ViewModel
import com.ceaver.assin.AssinWorkers
import com.ceaver.assin.markets.TitleRepository

class MarketListViewModel : ViewModel() {

    val titles = TitleRepository.loadActiveCryptoTitlesPagedAndObserved()
    val loading = AssinWorkers.running

    fun refresh() {
        AssinWorkers.completeUpdate()
    }
}