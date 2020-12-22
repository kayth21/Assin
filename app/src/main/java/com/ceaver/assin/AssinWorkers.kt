package com.ceaver.assin

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.work.*
import com.ceaver.assin.alerts.AlertWorker
import com.ceaver.assin.intentions.IntentionWorker
import com.ceaver.assin.logging.LogRepository
import com.ceaver.assin.markets.MarketUpdateWorker
import com.ceaver.assin.markets.overview.MarketOverviewUpdateWorker
import com.ceaver.assin.system.SystemRepository
import org.greenrobot.eventbus.EventBus
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit


object AssinWorkers {

    val running = MutableLiveData<Boolean>()

    fun completeUpdate() {
        running.postValue(true)
        WorkManager.getInstance(AssinApplication.appContext!!)
                .beginWith(notifyCompleteStart())
                .then(listOf(updateAllTitles(), updateMarketOverview()))
                .then(listOf(checkAlerts(), checkIntentions()))
                .then(notifyCompleteEnd())
                .enqueue()
    }

    private fun notifyCompleteStart(): OneTimeWorkRequest {
        return OneTimeWorkRequestBuilder<StartCompleteNotificationWorker>().build()
    }

    private fun notifyCompleteEnd(): OneTimeWorkRequest {
        return OneTimeWorkRequestBuilder<EndCompleteNotificationWorker>().build()
    }

    private fun checkIntentions(): OneTimeWorkRequest {
        return OneTimeWorkRequestBuilder<IntentionWorker>().build()
    }

    private fun checkAlerts(): OneTimeWorkRequest {
        return OneTimeWorkRequestBuilder<AlertWorker>().build()
    }

    private fun updateAllTitles(): OneTimeWorkRequest {
        return OneTimeWorkRequestBuilder<MarketUpdateWorker>().build()
    }

    private fun updateMarketOverview(): OneTimeWorkRequest {
        return OneTimeWorkRequestBuilder<MarketOverviewUpdateWorker>().build()
    }

    class StartCompleteNotificationWorker(appContext: Context, workerParams: WorkerParameters) : CoroutineWorker(appContext, workerParams) {
        override suspend fun doWork(): Result {
            running.postValue(true)
            val logId = LogRepository.insert("Assin workers: starting complete update...")
            val outputData: Data = workDataOf(AssinWorkers.toString() to logId)
            return Result.success(outputData)
        }
    }

    class EndCompleteNotificationWorker(appContext: Context, workerParams: WorkerParameters) : CoroutineWorker(appContext, workerParams) {
        override suspend fun doWork(): Result {
            SystemRepository.setInitialized(true)
            val logId = inputData.getLong(AssinWorkers.toString(), -1) // TODO
            val log = LogRepository.loadLog(logId)
            val duration = log.timestamp.until(LocalDateTime.now(), ChronoUnit.MILLIS)
            LogRepository.update(log.copy(message = log.message + " done. (${duration} ms)"))
            EventBus.getDefault().post(AssinWorkerEvents.Complete())
            running.postValue(false)
            return Result.success()
        }
    }
}