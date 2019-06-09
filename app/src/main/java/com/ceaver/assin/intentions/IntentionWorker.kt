package com.ceaver.assin.intentions

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

class IntentionWorker(appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams) {
    override fun doWork(): Result {
        return Result.success()
    }
}