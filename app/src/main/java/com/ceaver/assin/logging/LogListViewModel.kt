package com.ceaver.assin.logging

import androidx.lifecycle.ViewModel

class LogListViewModel : ViewModel() {
    val logs = LogRepository.loadAllLogsObserved()
}