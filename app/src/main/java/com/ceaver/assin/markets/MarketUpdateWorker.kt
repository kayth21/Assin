package com.ceaver.assin.markets

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.ceaver.assin.logging.LogRepository

class MarketUpdateWorker(appContext: Context, workerParams: WorkerParameters) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val allRemoteTitles = MarketRepository.loadAllTitles()
        val allRemoteTitleIds = allRemoteTitles.map { it.id }.toSet()
        val allLocalTitles = TitleRepository.loadAllTitles()
        val allLocalTitleIds = allLocalTitles.map { it.id }.toSet()

        if (allLocalTitles.isEmpty()) { // initial load
            TitleRepository.insertAll(allRemoteTitles.map { it.copy(active = 50) }.toSet())
        } else {
            val newTitlesToInsert = allRemoteTitles.filterNot { it.id in allLocalTitleIds }.toSet()
            val existingTitlesToUpdate = (allRemoteTitles - newTitlesToInsert).map { it.incrementActiveCounter() }
            val removedTitles = allLocalTitles.filterNot { it.id in allRemoteTitleIds }
            val removedTitlesPartitioned = removedTitles.partition { it.inactive() }
            val removedTitlesToDelete = removedTitlesPartitioned.first
            val removedTitlesToUpdate = removedTitlesPartitioned.second
            removedTitlesToUpdate.forEach { it.decreaseActiveCounter() }

            TitleRepository.marketUpdate(newTitlesToInsert, (existingTitlesToUpdate + removedTitlesToUpdate).toSet(), removedTitlesToDelete.toSet())

            // TODO Transaction Update
            existingTitlesToUpdate.filter { it.active == 50 }.forEach { LogRepository.insertLog("Activated  ${it.name} (${it.symbol}).") }
            removedTitlesToDelete.forEach { LogRepository.insertLog("Removed  ${it.name} (${it.symbol}) from local database due to long time inactivity.") }
        }

        return Result.success()
    }
}