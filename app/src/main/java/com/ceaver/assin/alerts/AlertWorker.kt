package com.ceaver.assin.alerts

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class AlertWorker(appContext: Context, workerParams: WorkerParameters) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {
        AlertRepository.loadAll().forEach {
            checkAlert(it)
        }
        return Result.success(inputData)
    }

    private suspend fun checkAlert(alert: Alert) {
        val result = alert.evaluate()
        val alert = result.first
        val notification = result.second

        // TODO instead of updating alerts one by one, update all by one. But be aware that an alert can be in that list more than once, store only latest.
        AlertRepository.update(alert)
        if (notification != null) {
            notification.push()
            checkAlert(alert)
        }
    }
}