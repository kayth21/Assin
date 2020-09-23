package com.ceaver.assin

import android.app.Application
import android.content.Context
import android.preference.PreferenceManager
import androidx.work.*
import com.ceaver.assin.logging.LogRepository
import com.ceaver.assin.preferences.Preferences
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
        initPreferences()
        delayedInit()
    }

    private fun initPreferences() {
        PreferenceManager.getDefaultSharedPreferences(applicationContext).edit()
                .putString(Preferences.CRYPTO_TITLE_SYMBOL, "BTC")
                .putString(Preferences.FIAT_TITLE_SYMBOL, "USD")
                .apply()
    }

    private fun delayedInit() {
        applicationScope.launch {
            Timber.plant(Timber.DebugTree())
            setupRecurringWork()
        }
    }

    private fun setupRecurringWork() {
        val constraints = Constraints.Builder().setRequiredNetworkType(NetworkType.UNMETERED).setRequiresBatteryNotLow(true).build()
        val backgroundProcess = PeriodicWorkRequestBuilder<StartWorker>(15, TimeUnit.MINUTES, 5, TimeUnit.MINUTES).setConstraints(constraints).build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(ASSIN_WORKER_ID, ExistingPeriodicWorkPolicy.REPLACE, backgroundProcess)
    }

    companion object {
        var appContext: Context? = null
            private set
        const val ASSIN_WORKER_ID = "ASSIN_WORKER_ID"
    }

    class StartWorker(appContext: Context, workerParams: WorkerParameters) : CoroutineWorker(appContext, workerParams) {
        override suspend fun doWork(): Result {
            if (isConnected())
                AssinWorkers.completeUpdate()
            else
                LogRepository.insert("update skipped because of missing connection")
            return Result.success()
        }
    }
}