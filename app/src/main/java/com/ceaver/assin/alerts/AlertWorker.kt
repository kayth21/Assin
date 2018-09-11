package com.ceaver.assin.alerts

import androidx.work.Worker
import com.ceaver.assin.assets.Symbol
import com.ceaver.assin.markets.MarketValuation

class AlertWorker : Worker() {
    override fun doWork(): Result {
        AlertRepository.loadAllAlerts().forEach { checkAlert(it) }
        return Result.SUCCESS
    }

    private fun checkAlert(alert: Alert) {
        val currentPrice = MarketValuation.load(alert.symbol, Symbol.USD).get().last
        val result = alert.alertType.check(alert, currentPrice)
        result.ifPresent { AlertRepository.updateAlert(it); checkAlert(it) }
    }
}