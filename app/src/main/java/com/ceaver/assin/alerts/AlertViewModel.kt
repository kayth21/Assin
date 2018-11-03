package com.ceaver.assin.alerts

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.ceaver.assin.assets.Symbol
import com.ceaver.assin.common.SaveClickHandler
import com.ceaver.assin.common.SingleLiveEvent

class AlertViewModel : ViewModel(), SaveClickHandler {

    val alert = MutableLiveData<Alert>()
    val status = SingleLiveEvent<AlertInputStatus>()
    val symbol = MutableLiveData<List<Symbol>>()

    fun init(alertId: Long = 0): AlertViewModel {
        symbol.postValue(Symbol.values().toList())
        if (alertId > 0) lookupAlert(alertId) else createAlert(); return this
    }

    private fun lookupAlert(alertId: Long) {
        AlertRepository.loadAlertAsync(alertId, false) { alert.postValue(it) }
    }

    private fun createAlert() {
        alert.postValue(Alert())
    }

    override fun onSaveClick() {
        status.value = AlertInputStatus.START_SAVE
        AlertRepository.saveAlertAsync(alert.value!!, true) { status.value = AlertInputStatus.END_SAVE }    }


    enum class AlertInputStatus {
        START_SAVE,
        END_SAVE
    }
}