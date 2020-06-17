package com.ceaver.assin.trades.input

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ceaver.assin.common.SingleLiveEvent
import com.ceaver.assin.markets.Title
import com.ceaver.assin.markets.TitleRepository
import com.ceaver.assin.threading.BackgroundThreadExecutor
import com.ceaver.assin.trades.Trade
import com.ceaver.assin.trades.TradeRepository
import com.ceaver.assin.trades.TradeType
import java.math.BigDecimal
import java.time.LocalDate

class TradeInputViewModel : ViewModel() {

    val trade = MutableLiveData<Trade>()
    val symbols = MutableLiveData<List<Title>>()
    val dataReady = zipLiveData(trade, symbols)
    val status = SingleLiveEvent<TradeInputStatus>()

    private fun lookupTrade(tradeId: Long) {
        TradeRepository.loadTradeAsync(tradeId, false) { trade.postValue(it) }
    }

    fun initTrade(tradeId: Long?, symbol: String?, lookupTradeType: TradeType): TradeInputViewModel {
        TitleRepository.loadAllTitlesAsync(false) { symbols.postValue(it) }
        when {
            tradeId != null -> lookupTrade(tradeId)
            symbol != null -> when (lookupTradeType) {
                TradeType.DEPOSIT -> BackgroundThreadExecutor.execute { trade.postValue(Trade(buyTitle = TitleRepository.loadTitleBySymbol(symbol))) }
                TradeType.WITHDRAW -> BackgroundThreadExecutor.execute { trade.postValue(Trade(sellTitle = TitleRepository.loadTitleBySymbol(symbol))) }
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

    fun onSaveTradeClick(buySymbol: Title, buyAmount: BigDecimal, sellSymbol: Title, sellAmount: BigDecimal, tradeDate: LocalDate, comment: String?) {
        saveTrade(trade.value!!.copy(buyTitle = buySymbol, buyAmount = buyAmount, sellTitle = sellSymbol, sellAmount = sellAmount, tradeDate = tradeDate, comment = comment))
    }

    fun onDepositClick(buySymbol: Title, buyAmount: BigDecimal, tradeDate: LocalDate, comment: String?) {
        saveTrade(trade.value!!.copy(buyTitle = buySymbol, buyAmount = buyAmount, tradeDate = tradeDate, comment = comment))
    }

    fun onWithdrawClick(sellSymbol: Title, sellAmount: BigDecimal, tradeDate: LocalDate, comment: String?) {
        saveTrade(trade.value!!.copy(sellTitle = sellSymbol, sellAmount = sellAmount, tradeDate = tradeDate, comment = comment))
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
