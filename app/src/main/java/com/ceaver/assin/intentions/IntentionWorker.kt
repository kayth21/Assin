package com.ceaver.assin.intentions

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class IntentionWorker(appContext: Context, workerParams: WorkerParameters) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {
        IntentionRepository.loadAll().forEach {
            checkIntention(it)
        }
        return Result.success()
    }

    private suspend fun checkIntention(intention: Intention) {
        val result = intention.evaluate() ?: return

        val updatedIntention = result.first
        val notification = result.second

        // TODO instead of updating intentions one by one, update all by one. Btw. Do not update if nothing changed...
        IntentionRepository.update(updatedIntention);
        notification?.push()
    }
}