package com.ceaver.assin.alerts

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ceaver.assin.common.SingleLiveEvent
import com.ceaver.assin.markets.Title
import com.ceaver.assin.markets.TitleRepository
import com.ceaver.assin.threading.BackgroundThreadExecutor
import java.math.BigDecimal
import java.math.MathContext

class AlertViewModel : ViewModel() {

    val alert = MutableLiveData<Alert>()
    val status = SingleLiveEvent<AlertInputStatus>()
    val symbol = MutableLiveData<List<Title>>()
    val reference = MutableLiveData<List<Title>>()

    fun init(alertId: Long = 0): AlertViewModel {
        TitleRepository.loadAllCryptoTitlesAsync(false) { symbol.postValue(it) }
        TitleRepository.loadAllTitlesAsync(false) { reference.postValue(it) }
        if (alertId > 0) lookupAlert(alertId) else createAlert(); return this
    }

    private fun lookupAlert(alertId: Long) {
        AlertRepository.loadAlertAsync(alertId, false) { alert.postValue(it) }
    }

    private fun createAlert() {
        BackgroundThreadExecutor.execute {
            alert.postValue(Alert(symbol = TitleRepository.loadTitleBySymbol("BTC"), reference = TitleRepository.loadTitleBySymbol("USD"), alertType = AlertType.RECURRING_STABLE, source = 0.0, target = 0.0))
        }
    }

    fun onSaveClick(symbol: Title, reference: Title, source: Double, target: Double) {
        status.value = AlertInputStatus.START_SAVE
        val alert = alert.value!!.copy(symbol = symbol, reference = reference, source = source, target = target)
        AlertRepository.saveAlertAsync(alert, true) { status.value = AlertInputStatus.END_SAVE }
    }

    fun lookupPrice(symbol: Title, reference: Title, callback: (Pair<Double, Double>) -> Unit) {
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