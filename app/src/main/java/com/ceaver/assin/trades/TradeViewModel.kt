package com.ceaver.assin.trades

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.ceaver.assin.common.SingleLiveEvent
import com.ceaver.assin.markets.TitleRepository
import java.time.LocalDate
import java.util.*

class TradeViewModel : ViewModel() {

    val trade = MutableLiveData<Trade>()
    val symbols = MutableLiveData<List<String>>()
    val dataReady = zipLiveData(trade, symbols)
    val status = SingleLiveEvent<TradeInputStatus>()

    private fun lookupTrade(tradeId: Long) {
        TradeRepository.loadTradeAsync(tradeId, false) { trade.postValue(it) }
    }


    fun initTrade(tradeId: Optional<Long>, symbol: Optional<String>, lookupTradeType: TradeType): TradeViewModel {
        TitleRepository.loadAllSymbolsAsync(false) { symbols.postValue(it) }
        when {
            tradeId.isPresent -> lookupTrade(tradeId.get())
            symbol.isPresent -> when (lookupTradeType) {
                TradeType.DEPOSIT -> trade.postValue(Trade(buyTitle = Optional.of(symbol.get())))
                TradeType.WITHDRAW -> trade.postValue(Trade(sellTitle = Optional.of(symbol.get())))
                else -> throw IllegalStateException()
            }
            else -> trade.postValue(Trade())
        }
        return this
    }

    private fun saveTrade(trade: Trade) {
        status.value = TradeInputStatus.START_SAVE
        TradeRepository.saveTradeAsync(trade, true) { status.value = TradeInputStatus.END_SAVE }
    }

    fun onSaveTradeClick(buySymbol: String, buyAmount: Double, sellSymbol: String, sellAmount: Double, tradeDate: LocalDate, comment: String) {
        saveTrade(trade.value!!.copy(buyTitle = Optional.of(buySymbol), buyAmount = Optional.of(buyAmount), sellTitle = Optional.of(sellSymbol), sellAmount = Optional.of(sellAmount), tradeDate = tradeDate, comment = comment))
    }

    fun onDepositClick(buySymbol: String, buyAmount: Double, tradeDate: LocalDate, comment: String) {
        saveTrade(trade.value!!.copy(buyTitle = Optional.of(buySymbol), buyAmount = Optional.of(buyAmount), tradeDate = tradeDate, comment = comment))
    }

    fun onWithdrawClick(sellSymbol: String, sellAmount: Double, tradeDate: LocalDate, comment: String) {
        saveTrade(trade.value!!.copy(sellTitle = Optional.of(sellSymbol), sellAmount = Optional.of(sellAmount), tradeDate = tradeDate, comment = comment))
    }

    enum class TradeInputStatus {
        START_SAVE,
        END_SAVE
    }

    fun <A, B> zipLiveData(a: LiveData<A>, b: LiveData<B>): LiveData<Pair<A, B>> {
        return MediatorLiveData<Pair<A, B>>().apply {
            var lastA: A? = null
            var lastB: B? = null

            fun update() {
                val localLastA = lastA
                val localLastB = lastB
                if (localLastA != null && localLastB != null)
                    this.value = Pair(localLastA, localLastB)
            }

            addSource(a) {
                lastA = it
                update()
            }
            addSource(b) {
                lastB = it
                update()
            }
        }
    }
}
