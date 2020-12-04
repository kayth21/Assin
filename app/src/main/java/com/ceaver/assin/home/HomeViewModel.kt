package com.ceaver.assin.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.ceaver.assin.AssinWorkers
import com.ceaver.assin.common.SingleMediatorLiveData

class HomeViewModel : ViewModel() {
    val assinWorkerRunning: LiveData<Boolean> = SingleMediatorLiveData<Boolean>()
            .apply {

                fun update() {
                    val running = AssinWorkers.running.value ?: return
                    value = running
                }
                addSource(AssinWorkers.running) { update() }
                update()
            }
}
