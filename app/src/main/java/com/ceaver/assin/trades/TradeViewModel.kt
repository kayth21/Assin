package com.ceaver.assin.trades

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel

class TradeViewModel : ViewModel() {

    var trade = MutableLiveData<Trade>()

    fun init(tradeId: Long): TradeViewModel {
        if(tradeId > 0) {
            TradeRepository.loadTradeAsync(tradeId, false) { trade.postValue(it) }
        } else {
            trade.postValue(Trade(strategies = setOf(TradeStrategy.HODL, TradeStrategy.BAD_TRADE)))
        }
        return this;
    }
}
