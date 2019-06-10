package com.ceaver.assin.intentions

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

class IntentionWorker(appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams) {
    override fun doWork(): Result {
        IntentionRepository.loadAllIntentions().forEach { checkIntention(it) }
        return Result.success()
    }

    private fun checkIntention(intention: Intention) {
        val calculatedStatus = intention.calculateState()
        if (calculatedStatus > intention.status) {
            val newIntention = intention.copy(status = calculatedStatus)
            IntentionRepository.updateIntention(newIntention);
            IntentionNotification.notify(newIntention)
        }
    }
}