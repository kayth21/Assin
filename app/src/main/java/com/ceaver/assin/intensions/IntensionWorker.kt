package com.ceaver.assin.intensions

import androidx.work.Worker

class IntensionWorker : Worker() {
    override fun doWork(): Result {
        return Result.SUCCESS
    }
}