package com.ceaver.assin.alerts

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class AlertWorker(appContext: Context, workerParams: WorkerParameters) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {
        AlertRepository.loadAll().forEach {
            checkAlert(it)
        }
        return Result.success()
    }

    private suspend fun checkAlert(alert: Alert) {
        val result = alert.evaluate()
        val alert = result.first
        val notification = result.second

        AlertRepository.update(alert)
        if (notification != null) {
            notification.push()
            checkAlert(alert)
        }
    }
}