package com.ceaver.assin.markets.list

import androidx.databinding.ObservableBoolean
import androidx.lifecycle.ViewModel
import com.ceaver.assin.AssinWorkers
import com.ceaver.assin.markets.TitleRepository

class MarketListViewModel : ViewModel() {

    val titles = TitleRepository.loadActiveCryptoTitles()
    val loading = ObservableBoolean()

    fun refresh() {
        loading.set(true)
        AssinWorkers.completeUpdate()
    }
}