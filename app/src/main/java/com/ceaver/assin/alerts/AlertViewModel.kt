package com.ceaver.assin.alerts

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.ceaver.assin.common.SingleLiveEvent
import com.ceaver.assin.markets.TitleRepository
import java.math.BigDecimal
import java.math.MathContext

class AlertViewModel : ViewModel() {

    val alert = MutableLiveData<Alert>()
    val status = SingleLiveEvent<AlertInputStatus>()
    val symbol = MutableLiveData<List<String>>()
    val reference = MutableLiveData<List<String>>()

    fun init(alertId: Long = 0): AlertViewModel {
        TitleRepository.loadAllCryptoSymbolsAsync(false) { symbol.postValue(it) }
        TitleRepository.loadAllSymbolsAsync(false) { reference.postValue(it) }
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

    fun lookupPrice(symbol: String, reference: String, callback: (Pair<Double, Double>) -> Unit) {
        TitleRepository.lookupPriceAsync(symbol, reference, true) {
            val result = if (it.isPresent) {
                val last = it.get().toBigDecimal()
                val price = last.round(MathContext(2))
                val target = last.divide(BigDecimal(25), MathContext(1))
                price.toDouble() to target.toDouble()
            } else 0.0 to 0.0
            callback.invoke(result)
        }
    }

    fun isNew(): Boolean = alert.value!!.isNew()

    enum class AlertInputStatus {
        START_SAVE,
        END_SAVE
    }
}