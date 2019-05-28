package com.ceaver.assin.markets

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.ceaver.assin.logging.LogRepository

class MarketCompleteUpdateWorker(appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams) {

    override fun doWork(): Result {
        val allRemoteTitles = MarketRepository.loadAllTitles()
        val allLocalTitles = TitleRepository.loadAllTitles()

        val newTitlesToInsert = allRemoteTitles - allLocalTitles
        val existingTitlesToUpdate = (allRemoteTitles - newTitlesToInsert).map { if (it.active.toInt() > 99) it else it.copy(active = Integer(it.active.toInt() + 1)) }
        val removedTitles = allLocalTitles - allRemoteTitles
        val removedTitlesToDelete = removedTitles.filter { it.active < -99 }
        val removedTitlesToUpdate = (removedTitles - removedTitlesToDelete).map { it.copy(active = Integer(it.active.toInt() - 1)) }

        TitleRepository.updateAll(newTitlesToInsert + existingTitlesToUpdate + removedTitlesToUpdate)
        TitleRepository.deleteTitles(removedTitlesToDelete.toSet())

        newTitlesToInsert.forEach { LogRepository.insertLog("Added  ${it.name} (${it.symbol}) to local database.") }
        removedTitlesToUpdate.forEach { LogRepository.insertLog("Decreased active rating of ${it.name} (${it.symbol}) (${it.active})") }
        removedTitlesToDelete.forEach { LogRepository.insertLog("Removed  ${it.name} (${it.symbol}) from local database.") }

        return Result.success()
    }
}