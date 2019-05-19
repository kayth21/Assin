package com.ceaver.assin.markets

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.ceaver.assin.logging.LogRepository

class MarketCompleteUpdateWorker(appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams) {

    override fun doWork(): Result {
        val remoteTitles = MarketRepository.loadAllTitles()
        if (remoteTitles.isNotEmpty()) cleanupLocalTitles(remoteTitles)
        TitleRepository.updateAll(remoteTitles)
        return Result.success()
    }

    private fun cleanupLocalTitles(remoteTitles: Set<Title>) {
        val localTitles = TitleRepository.loadAllTitles()
        localTitles.forEach { title ->
            if (remoteTitles.none { title.symbol.equals(it.symbol) }) {
                LogRepository.insertLog("Removed ${title.name} (${title.symbol}) from local database.")
                TitleRepository.deleteTitle(title) // TODO check safe delete, maybe the app asks if it's ok to remove this title. Or at least check if there can be issues later because the title is used in later entities.
            };
        }
        remoteTitles.forEach { title ->
            if (localTitles.none { title.symbol.equals(it.symbol) }) {
                LogRepository.insertLog("Added  ${title.name} (${title.symbol}) to local database.")
            }
        }
    }

}