package com.ceaver.assin.trades

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.ceaver.assin.common.SaveClickHandler
import com.ceaver.assin.common.SingleLiveEvent

class TradeViewModel : ViewModel(), SaveClickHandler {

    val trade = MutableLiveData<Trade>()
    val status = SingleLiveEvent<TradeInputStatus>()

    fun init(tradeId: Long): TradeViewModel {
        if (tradeId > 0) lookupTrade(tradeId) else createTrade(); return this
    }

    private fun lookupTrade(tradeId: Long) {
        TradeRepository.loadTradeAsync(tradeId, false) { trade.postValue(it) }
    }

    private fun createTrade() {
        trade.postValue(Trade())
    }

    override fun onSaveClick() {
        status.value = TradeInputStatus.START_SAVE
        TradeRepository.saveTradeAsync(trade.value!!, true) { status.value = TradeInputStatus.END_SAVE }
    }

    enum class TradeInputStatus {
        START_SAVE,
        END_SAVE
    }

}
