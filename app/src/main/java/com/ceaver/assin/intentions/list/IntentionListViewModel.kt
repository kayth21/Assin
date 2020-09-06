package com.ceaver.assin.intentions.list

import androidx.databinding.ObservableBoolean
import androidx.lifecycle.ViewModel
import com.ceaver.assin.AssinWorkers
import com.ceaver.assin.intentions.IntentionRepository

class IntentionListViewModel : ViewModel() {
    val intentions = IntentionRepository.loadAllIntentionsObserved()
    val loading = ObservableBoolean()

    fun refresh() {
        loading.set(true)
        AssinWorkers.completeUpdate()
    }

}