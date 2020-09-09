package com.ceaver.assin

import android.app.Application
import android.content.Context
import androidx.work.*
import com.ceaver.assin.logging.LogRepository
import com.ceaver.assin.util.isCharging
import com.ceaver.assin.util.isConnected
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.concurrent.TimeUnit


class AssinApplication : Application() {

    private val applicationScope = CoroutineScope(Dispatchers.Default)

    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
        delayedInit()
    }

    private fun delayedInit() {
        applicationScope.launch {
            Timber.plant(Timber.DebugTree())
            setupRecurringWork()
        }
    }

    private fun setupRecurringWork() {
        val backgroundProcess = PeriodicWorkRequestBuilder<StartWorker>(15, TimeUnit.MINUTES, 5, TimeUnit.MINUTES).build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(ASSIN_WORKER_ID, ExistingPeriodicWorkPolicy.KEEP, backgroundProcess)
    }

    companion object {
        var appContext: Context? = null
            private set
        const val ASSIN_WORKER_ID = "ASSIN_WORKER_ID"
    }

    class StartWorker(appContext: Context, workerParams: WorkerParameters) : CoroutineWorker(appContext, workerParams) {
        override suspend fun doWork(): Result {
            if (isConnected())
                if (isCharging())
                    AssinWorkers.completeUpdate()
                else
                    AssinWorkers.observedUpdate()
            else
                LogRepository.insertLog("update skipped because of missing connection")
            return Result.success()
        }
    }
}