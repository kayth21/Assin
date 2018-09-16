package com.ceaver.assin

import androidx.work.*
import com.ceaver.assin.alerts.AlertWorker
import com.ceaver.assin.assets.Category
import com.ceaver.assin.assets.Symbol
import com.ceaver.assin.intensions.IntensionWorker
import com.ceaver.assin.logging.LogRepository
import com.ceaver.assin.markets.MarketWorker
import org.greenrobot.eventbus.EventBus
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.*
import java.util.stream.Collectors

object AssinWorkers {

    fun completeUpdate() {
        val identifier = UUID.randomUUID();
        WorkManager.getInstance()
                .beginWith(notifyStart(identifier))
                .then(updateAllTitles())
                .then(checkAlerts(), checkIntentions())
                .then(notifyEnd(identifier))
                .enqueue()
    }


    private fun notifyStart(identifier: UUID): OneTimeWorkRequest {
        val data = Data.Builder().putString(AssinWorkers.toString(), identifier.toString()).build()
        return OneTimeWorkRequestBuilder<StartNotificationWorker>().setInputData(data).build()
    }

    private fun notifyEnd(identifier: UUID): OneTimeWorkRequest {
        val data = Data.Builder().putString(AssinWorkers.toString(), identifier.toString()).build()
        return OneTimeWorkRequestBuilder<EndNotificationWorker>().setInputData(data).build()
    }

    private fun checkIntentions(): OneTimeWorkRequest {
        return OneTimeWorkRequestBuilder<IntensionWorker>().build()
    }

    private fun checkAlerts(): OneTimeWorkRequest {
        return OneTimeWorkRequestBuilder<AlertWorker>().build()
    }

    private fun updateAllTitles() = Symbol.values(Category.CRYPTO).stream().map { requestBuilder(it) }.collect(Collectors.toList())

    private fun requestBuilder(symbol: Symbol): OneTimeWorkRequest {
        val data = Data.Builder().putString(Symbol.toString(), symbol.name).build()
        return OneTimeWorkRequestBuilder<MarketWorker>().setInputData(data).build()
    }

    class StartNotificationWorker : Worker() {
        override fun doWork(): Result {
            val uuid = UUID.fromString(inputData.getString(AssinWorkers.toString()))
            LogRepository.insertLog("Assin workers: starting complete update...", uuid)
            return Result.SUCCESS
        }
    }

    class EndNotificationWorker : Worker() {
        override fun doWork(): Result {
            val uuid = UUID.fromString(inputData.getString(AssinWorkers.toString()))
            val log = LogRepository.loadLog(uuid)
            val duration = log.timestamp.until(LocalDateTime.now(), ChronoUnit.MILLIS)
            LogRepository.updateLog(log.copy(message = log.message + " done. (${duration} ms)"))
            EventBus.getDefault().post(AssinWorkerEvents.Complete())
            return Result.SUCCESS
        }
    }

    fun observedUpdate() {
        TODO()
    }

}