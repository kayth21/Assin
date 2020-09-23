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
import java.util.*

object AssinWorkers {

    val running = MutableLiveData<Boolean>()

    fun completeUpdate() {
        WorkManager.getInstance(AssinApplication.appContext!!)
        val identifier = UUID.randomUUID();
        WorkManager.getInstance(AssinApplication.appContext!!)
                .beginWith(notifyCompleteStart(identifier))
                .then(listOf(updateAllTitles(), updateMarketOverview()))
                .then(listOf(checkAlerts(), checkIntentions()))
                .then(notifyCompleteEnd(identifier))
                .enqueue()
    }

    private fun notifyCompleteStart(identifier: UUID): OneTimeWorkRequest {
        val data = Data.Builder().putString(AssinWorkers.toString(), identifier.toString()).build()
        return OneTimeWorkRequestBuilder<StartCompleteNotificationWorker>().setInputData(data).build()
    }

    private fun notifyCompleteEnd(identifier: UUID): OneTimeWorkRequest {
        val data = Data.Builder().putString(AssinWorkers.toString(), identifier.toString()).build()
        return OneTimeWorkRequestBuilder<EndCompleteNotificationWorker>().setInputData(data).build()
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
            val uuid = UUID.fromString(inputData.getString(AssinWorkers.toString()))
            LogRepository.insert("Assin workers: starting complete update...", uuid)
            return Result.success()
        }
    }

    class EndCompleteNotificationWorker(appContext: Context, workerParams: WorkerParameters) : CoroutineWorker(appContext, workerParams) {
        override suspend fun doWork(): Result {
            SystemRepository.setInitialized(true)
            val uuid = UUID.fromString(inputData.getString(AssinWorkers.toString()))
            val log = LogRepository.loadLog(uuid)
            val duration = log.timestamp.until(LocalDateTime.now(), ChronoUnit.MILLIS)
            LogRepository.update(log.copy(message = log.message + " done. (${duration} ms)"))
            EventBus.getDefault().post(AssinWorkerEvents.Complete())
            running.postValue(false)
            return Result.success()
        }
    }
}