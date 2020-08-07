package com.ceaver.assin

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.work.*
import com.ceaver.assin.logging.LogRepository
import com.ceaver.assin.util.isCharging
import com.ceaver.assin.util.isConnected
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        val navController = findNavController(R.id.main_activity_nav_host_fragment)
        val config = AppBarConfiguration.Builder(R.id.homeFragment).build()
        NavigationUI.setupActionBarWithNavController(this,navController, config)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = this.findNavController(R.id.main_activity_nav_host_fragment)
        return navController.navigateUp()
    }

    override fun onStart() {
        super.onStart()
        val backgroundProcess = PeriodicWorkRequestBuilder<StartWorker>(15, TimeUnit.MINUTES, 5, TimeUnit.MINUTES).build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork("UNIQUE", ExistingPeriodicWorkPolicy.KEEP, backgroundProcess)
    }

    class StartWorker(appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams) {
        override fun doWork(): Result {
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