package com.ceaver.assin.alerts

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.ceaver.assin.logging.LogRepository
import com.ceaver.assin.markets.TitleRepository
import java.math.BigDecimal

class AlertWorker(appContext: Context, workerParams: WorkerParameters) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {
        AlertRepository.loadAllAlerts().forEach { checkAlert(it) }
        return Result.success()
    }

    private suspend fun checkAlert(alert: Alert) {
        val price = TitleRepository.lookupPrice(alert.symbol, alert.reference)
        if (price.isPresent) {
            val currentPrice = BigDecimal.valueOf(price.get())
            val result = alert.alertType.check(alert, currentPrice)
            if (result.isPresent.not()) {
                return
            }
            val it = result.get()
            AlertRepository.updateAlert(it)
            checkAlert(it)
            AlertNotification.notify(alert.symbol, alert.reference, targetPrice(alert, currentPrice), currentPrice)
        } else {
            LogRepository.insertLog("Failed to check alert ${alert.symbol.symbol}/${alert.reference.symbol} (no path found).")
        }
    }

    private fun targetPrice(alert: Alert, currentPrice: BigDecimal): BigDecimal {
        return when {
            currentPrice <= (alert.source - alert.target) -> alert.source - alert.target
            currentPrice >= (alert.source + alert.target) -> alert.source + alert.target
            else -> throw IllegalStateException("Current Price: $currentPrice, Source: ${alert.source}, Target: ${alert.target}")
        }
    }
}