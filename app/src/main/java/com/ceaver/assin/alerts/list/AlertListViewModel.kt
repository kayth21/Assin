package com.ceaver.assin.alerts.list

import androidx.lifecycle.ViewModel
import com.ceaver.assin.alerts.AlertRepository

class AlertListViewModel : ViewModel() {

    val alerts = AlertRepository.loadAllAlertsObserved()

}