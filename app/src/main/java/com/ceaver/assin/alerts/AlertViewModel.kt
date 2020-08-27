package com.ceaver.assin.alerts

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ceaver.assin.common.SingleLiveEvent
import com.ceaver.assin.markets.Title
import com.ceaver.assin.markets.TitleRepository
import com.ceaver.assin.threading.BackgroundThreadExecutor
import java.math.BigDecimal
import java.math.MathContext

class AlertViewModel(alert: Alert?) : ViewModel() {
    private val _alert = MutableLiveData<Alert>()
    private val _status = SingleLiveEvent<AlertInputStatus>()
    private val _symbol = MutableLiveData<List<Title>>()
    private val _reference = MutableLiveData<List<Title>>()
    val alert: LiveData<Alert> get() = _alert
    val status: LiveData<AlertInputStatus> get() = _status
    val symbol: LiveData<List<Title>> get() = _symbol
    val reference: LiveData<List<Title>> get() = _reference

    init {
        if (alert == null) createAlert() else this._alert.postValue(alert)
        TitleRepository.loadAllCryptoTitlesAsync(false) { _symbol.postValue(it) }
        TitleRepository.loadAllTitlesAsync(false) { _reference.postValue(it) }
    }

    private fun createAlert() {
        BackgroundThreadExecutor.execute {
            _alert.postValue(Alert(symbol = TitleRepository.loadTitleBySymbol("BTC"), reference = TitleRepository.loadTitleBySymbol("USD"), alertType = AlertType.RECURRING_STABLE, source = BigDecimal.ZERO, target = BigDecimal.ZERO))
        }
    }

    fun onSaveClick(symbol: Title, reference: Title, source: BigDecimal, target: BigDecimal) {
        _status.value = AlertInputStatus.START_SAVE
        val alert = _alert.value!!.copy(symbol = symbol, reference = reference, source = source, target = target)
        AlertRepository.saveAlertAsync(alert, true) { _status.value = AlertInputStatus.END_SAVE }
    }

    fun lookupPrice(symbol: Title, reference: Title, callback: (Pair<Double, Double>) -> Unit) {
        TitleRepository.lookupPriceAsync(symbol, reference, true) {
            val result = if (it.isPresent) {
                val last = it.get().toBigDecimal()
                val price = last.round(MathContext(2))
                val target = last.divide(BigDecimal(25), MathContext(1)).toPlainString()
                price.toDouble() to target.toDouble()
            } else 0.0 to 0.0
            callback.invoke(result)
        }
    }

    fun isNew(): Boolean = _alert.value!!.isNew()

    enum class AlertInputStatus {
        START_SAVE,
        END_SAVE
    }

    class Factory(val alert: Alert?) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return AlertViewModel(alert) as T
        }
    }
}