package com.ceaver.assin

import androidx.work.*
import com.ceaver.assin.alerts.AlertRepository
import com.ceaver.assin.alerts.AlertWorker
import com.ceaver.assin.intensions.IntensionWorker
import com.ceaver.assin.logging.LogRepository
import com.ceaver.assin.markets.MarketCompleteUpdateWorker
import com.ceaver.assin.markets.MarketPartialUpdateWorker
import org.greenrobot.eventbus.EventBus
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.*
import java.util.stream.Collectors

object AssinWorkers {

    fun completeUpdate() {
        val identifier = UUID.randomUUID();
        WorkManager.getInstance()
                .beginWith(notifyCompleteStart(identifier))
                .then(updateAllTitles())
                .then(checkAlerts(), checkIntentions())
                .then(notifyCompleteEnd(identifier))
                .enqueue()
    }


    fun observedUpdate() {
        val identifier = UUID.randomUUID();
        WorkManager.getInstance()
                .beginWith(notifyObservedStart(identifier))
                .then(updateObservedTitles())
                .then(checkAlerts(), checkIntentions())
                .then(notifyObservedEnd(identifier))
                .enqueue()
    }


    private fun notifyCompleteStart(identifier: UUID): OneTimeWorkRequest {
        val data = Data.Builder().putString(AssinWorkers.toString(), identifier.toString()).build()
        return OneTimeWorkRequestBuilder<StartCompleteNotificationWorker>().setInputData(data).build()
    }

    private fun notifyObservedStart(identifier: UUID): OneTimeWorkRequest {
        val data = Data.Builder().putString(AssinWorkers.toString(), identifier.toString()).build()
        return OneTimeWorkRequestBuilder<StartObservedNotificationWorker>().setInputData(data).build()
    }

    private fun notifyCompleteEnd(identifier: UUID): OneTimeWorkRequest {
        val data = Data.Builder().putString(AssinWorkers.toString(), identifier.toString()).build()
        return OneTimeWorkRequestBuilder<EndCompleteNotificationWorker>().setInputData(data).build()
    }

    private fun notifyObservedEnd(identifier: UUID): OneTimeWorkRequest {
        val data = Data.Builder().putString(AssinWorkers.toString(), identifier.toString()).build()
        return OneTimeWorkRequestBuilder<EndObservedNotificationWorker>().setInputData(data).build()
    }

    private fun checkIntentions(): OneTimeWorkRequest {
        return OneTimeWorkRequestBuilder<IntensionWorker>().build()
    }

    private fun checkAlerts(): OneTimeWorkRequest {
        return OneTimeWorkRequestBuilder<AlertWorker>().build()
    }

    private fun updateAllTitles(): OneTimeWorkRequest {
        return OneTimeWorkRequestBuilder<MarketCompleteUpdateWorker>().build()
    }

    private fun updateObservedTitles(): MutableList<OneTimeWorkRequest> {
        return AlertRepository.loadAllAlerts().stream().flatMap { setOf(it.symbol, it.reference).stream() }.filter { it != "USD" }.map { marketPartialUpdateRequestBuilder(it) }.collect(Collectors.toList())
    }

    private fun marketPartialUpdateRequestBuilder(symbol: String): OneTimeWorkRequest {
        val data = Data.Builder().putString("Symbol", symbol).build() // TODO use better identifier
        return OneTimeWorkRequestBuilder<MarketPartialUpdateWorker>().setInputData(data).build()
    }

    class StartCompleteNotificationWorker : Worker() {
        override fun doWork(): Result {
            val uuid = UUID.fromString(inputData.getString(AssinWorkers.toString()))
            LogRepository.insertLog("Assin workers: starting complete update...", uuid)
            return Result.SUCCESS
        }
    }

    class StartObservedNotificationWorker : Worker() {
        override fun doWork(): Result {
            val uuid = UUID.fromString(inputData.getString(AssinWorkers.toString()))
            LogRepository.insertLog("Assin workers: starting observed update...", uuid)
            return Result.SUCCESS
        }
    }

    class EndCompleteNotificationWorker : Worker() {
        override fun doWork(): Result {
            val uuid = UUID.fromString(inputData.getString(AssinWorkers.toString()))
            val log = LogRepository.loadLog(uuid)
            val duration = log.timestamp.until(LocalDateTime.now(), ChronoUnit.MILLIS)
            LogRepository.updateLog(log.copy(message = log.message + " done. (${duration} ms)"))
            EventBus.getDefault().post(AssinWorkerEvents.Complete())
            return Result.SUCCESS
        }
    }

    class EndObservedNotificationWorker : Worker() {
        override fun doWork(): Result {
            val uuid = UUID.fromString(inputData.getString(AssinWorkers.toString()))
            val log = LogRepository.loadLog(uuid)
            val duration = log.timestamp.until(LocalDateTime.now(), ChronoUnit.MILLIS)
            LogRepository.updateLog(log.copy(message = log.message + " done. (${duration} ms)"))
            EventBus.getDefault().post(AssinWorkerEvents.Observed())
            return Result.SUCCESS
        }
    }
}