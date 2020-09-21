package com.ceaver.assin.alerts

import androidx.lifecycle.*
import com.ceaver.assin.common.SingleLiveEvent
import com.ceaver.assin.markets.Title
import com.ceaver.assin.markets.TitleRepository
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.MathContext

class AlertViewModel(alert: Alert?) : ViewModel() {
    private val _alert = MutableLiveData<Alert>()
    private val _status = SingleLiveEvent<AlertInputStatus>()
    private val _title = MutableLiveData<List<Title>>()
    private val _reference = MutableLiveData<List<Title>>()
    val alert: LiveData<Alert> get() = _alert
    val status: LiveData<AlertInputStatus> get() = _status
    val title: LiveData<List<Title>> get() = _title
    val reference: LiveData<List<Title>> get() = _reference

    init {
        if (alert == null) createAlert() else this._alert.postValue(alert)
        viewModelScope.launch {
            val cryptoTitles = TitleRepository.loadAllCryptoTitles()
            _title.postValue(cryptoTitles)
            val titles = TitleRepository.loadAllTitles()
            _reference.postValue(titles)
        }
    }

    private fun createAlert() {
        viewModelScope.launch {
            _alert.postValue(Alert(title = TitleRepository.loadTitleBySymbol("BTC"), referenceTitle = TitleRepository.loadTitleBySymbol("USD"), alertType = AlertType.RECURRING_STABLE, source = BigDecimal.ZERO, target = BigDecimal.ZERO))
        }
    }

    fun onSaveClick(title: Title, reference: Title, source: BigDecimal, target: BigDecimal) {
        viewModelScope.launch {
            _status.value = AlertInputStatus.START_SAVE
            val alert = _alert.value!!.copy(title = title, referenceTitle = reference, source = source, target = target)
            AlertRepository.saveAlert(alert)
            _status.value = AlertInputStatus.END_SAVE
        }
    }

    suspend fun lookupPrice(title: Title, reference: Title, callback: (Pair<Double, Double>) -> Unit) {
        val it = TitleRepository.lookupPrice(title, reference)
        val result = if (it.isPresent) {
            val last = it.get().toBigDecimal()
            val price = last.round(MathContext(2))
            val target = last.divide(BigDecimal(25), MathContext(1)).toPlainString()
            price.toDouble() to target.toDouble()
        } else 0.0 to 0.0
        callback.invoke(result)

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