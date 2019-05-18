package com.ceaver.assin.intensions

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

class IntensionWorker(appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams) {
    override fun doWork(): Result {
        return Result.success()
    }
}