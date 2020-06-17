package com.ceaver.assin.alerts

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.ceaver.assin.logging.LogRepository
import com.ceaver.assin.markets.TitleRepository
import java.math.BigDecimal

class AlertWorker(appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams) {
    override fun doWork(): Result {
        AlertRepository.loadAllAlerts().forEach { checkAlert(it) }
        return Result.success()
    }

    private fun checkAlert(alert: Alert) {
        val price = TitleRepository.lookupPrice(alert.symbol, alert.reference)
        if(price.isPresent) {
            val currentPrice = price.get()
            val result = alert.alertType.check(alert, currentPrice)
            result.ifPresent { AlertRepository.updateAlert(it); checkAlert(it); AlertNotification.notify(alert.symbol, alert.reference, targetPrice(alert, currentPrice), currentPrice) }
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