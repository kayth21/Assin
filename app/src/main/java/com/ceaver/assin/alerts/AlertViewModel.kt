package com.ceaver.assin.alerts

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.ceaver.assin.assets.Symbol
import com.ceaver.assin.common.SingleLiveEvent
import com.ceaver.assin.markets.MarketRepository
import java.math.BigDecimal
import java.math.MathContext

class AlertViewModel : ViewModel() {

    val alert = MutableLiveData<Alert>()
    val status = SingleLiveEvent<AlertInputStatus>()
    val symbol = MutableLiveData<List<Symbol>>()
    val reference = MutableLiveData<List<Symbol>>()

    fun init(alertId: Long = 0): AlertViewModel {
        symbol.postValue(Symbol.values().toList())
        reference.postValue(Symbol.values().toList())
        if (alertId > 0) lookupAlert(alertId) else createAlert(); return this
    }

    private fun lookupAlert(alertId: Long) {
        AlertRepository.loadAlertAsync(alertId, false) { alert.postValue(it) }
    }

    private fun createAlert() {
        alert.postValue(Alert(symbol = Symbol.BTC, reference = Symbol.USD, alertType = AlertType.RECURRING_STABLE, source = 0.0, target = 0.0))
    }

    fun onSaveClick(symbol: Symbol, reference: Symbol, source: Double, target: Double) {
        status.value = AlertInputStatus.START_SAVE
        val alert = alert.value!!.copy(symbol = symbol, reference = reference, source = source, target = target)
        AlertRepository.saveAlertAsync(alert, true) { status.value = AlertInputStatus.END_SAVE }
    }

    fun lookupPrice(symbol: Symbol, reference: Symbol): Pair<Double, Double> {
        val title = MarketRepository.load(symbol, reference)
        return if (title.isPresent) {
            val last = title.get().last.toBigDecimal()
            val price = last.round(MathContext(2))
            val target = last.divide(BigDecimal(25), MathContext(1))
            price.toDouble() to target.toDouble()
        } else 0.0 to 0.0
    }

    enum class AlertInputStatus {
        START_SAVE,
        END_SAVE
    }
}