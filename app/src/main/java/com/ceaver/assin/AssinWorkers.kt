package com.ceaver.assin

import androidx.work.*
import com.ceaver.assin.alerts.AlertWorker
import com.ceaver.assin.assets.Category
import com.ceaver.assin.assets.Symbol
import com.ceaver.assin.intensions.IntensionWorker
import com.ceaver.assin.logging.LogRepository
import com.ceaver.assin.markets.MarketWorker
import org.greenrobot.eventbus.EventBus
import java.util.stream.Collectors

object AssinWorkers {

    fun completeUpdate() {
        WorkManager.getInstance()
                .beginWith(logMessage())
                .then(updateAllTitles())
                .then(checkAlerts(), checkIntentions())
                .then(notifyListeners())
                .enqueue()
    }


    private fun logMessage(): OneTimeWorkRequest {
        return OneTimeWorkRequestBuilder<LogWorker>().build()
    }

    private fun notifyListeners(): OneTimeWorkRequest {
        return OneTimeWorkRequestBuilder<NotificationWorker>().build()
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

    class NotificationWorker : Worker() {
        override fun doWork(): Result {
            EventBus.getDefault().post(AssinWorkerEvents.Complete())
            return Result.SUCCESS
        }
    }

    class LogWorker : Worker() {
        override fun doWork(): Result {
            LogRepository.insert("start complete update")
            return Result.SUCCESS
        }
    }

    fun observedUpdate() {
        TODO()
    }

}