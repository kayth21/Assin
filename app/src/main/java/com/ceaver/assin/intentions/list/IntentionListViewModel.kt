package com.ceaver.assin.intentions.list

import androidx.lifecycle.ViewModel
import com.ceaver.assin.AssinWorkers
import com.ceaver.assin.intentions.IntentionRepository

class IntentionListViewModel : ViewModel() {
    val intentions = IntentionRepository.loadAllIntentionsObserved()
    val loading = AssinWorkers.running

    fun refresh() {
        AssinWorkers.completeUpdate()
    }

}