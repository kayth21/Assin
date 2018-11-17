package com.ceaver.assin.alerts

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.ceaver.assin.common.SingleLiveEvent
import com.ceaver.assin.markets.MarketRepository
import java.math.BigDecimal
import java.math.MathContext

class AlertViewModel : ViewModel() {

    val alert = MutableLiveData<Alert>()
    val status = SingleLiveEvent<AlertInputStatus>()
    val symbol = MutableLiveData<List<String>>()
    val reference = MutableLiveData<List<String>>()

    fun init(alertId: Long = 0): AlertViewModel {
        symbol.postValue(MarketRepository.loadAllCryptoSymbols().toList())
        reference.postValue(MarketRepository.loadAllSymbols().toList())
        if (alertId > 0) lookupAlert(alertId) else createAlert(); return this
    }

    private fun lookupAlert(alertId: Long) {
        AlertRepository.loadAlertAsync(alertId, false) { alert.postValue(it) }
    }

    private fun createAlert() {
        alert.postValue(Alert(symbol = "BTC", reference = "USD", alertType = AlertType.RECURRING_STABLE, source = 0.0, target = 0.0))
    }

    fun onSaveClick(symbol: String, reference: String, source: Double, target: Double) {
        status.value = AlertInputStatus.START_SAVE
        val alert = alert.value!!.copy(symbol = symbol, reference = reference, source = source, target = target)
        AlertRepository.saveAlertAsync(alert, true) { status.value = AlertInputStatus.END_SAVE }
    }

    fun lookupPrice(symbol: String, reference: String): Pair<Double, Double> {
        val price = MarketRepository.lookupPrice(symbol, reference)
        return if (price.isPresent) {
            val last = price.get().toBigDecimal()
            val price = last.round(MathContext(2))
            val target = last.divide(BigDecimal(25), MathContext(1))
            price.toDouble() to target.toDouble()
        } else 0.0 to 0.0
    }

    fun isNew(): Boolean = alert.value!!.isNew()

    enum class AlertInputStatus {
        START_SAVE,
        END_SAVE
    }
}