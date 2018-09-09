package com.ceaver.assin.alerts

import androidx.work.Worker

class AlertWorker : Worker() {
    override fun doWork(): Result {
        return Result.SUCCESS
    }
}