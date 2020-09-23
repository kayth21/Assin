package com.ceaver.assin.markets

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.ceaver.assin.assets.AssetCategory
import com.ceaver.assin.logging.LogRepository

class MarketUpdateWorker(appContext: Context, workerParams: WorkerParameters) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val allRemoteTitles = MarketRepository.loadAllTitles()
        val allRemoteTitleIds = allRemoteTitles.map { it.id }.toSet()
        val allLocalTitles = TitleRepository.loadAll()
        val allLocalTitleIds = allLocalTitles.map { it.id }.toSet()

        if (allLocalTitles.isEmpty()) { // initial load
            TitleRepository.insert(allRemoteTitles.map { it.copy(active = 50) }.toSet())
        } else {
            val newTitlesToInsert = allRemoteTitles.filterNot { it.id in allLocalTitleIds }.toSet()
            val existingTitlesToUpdate = (allRemoteTitles - newTitlesToInsert).map { it.incrementActiveCounter() }
            val removedTitles = allLocalTitles.filterNot { it.id in allRemoteTitleIds }
            val removedTitlesPartitioned = removedTitles.partition { it.inactive() }
            val removedTitlesToDelete = removedTitlesPartitioned.first
            val removedTitlesToUpdate = removedTitlesPartitioned.second
            removedTitlesToUpdate.forEach { it.decreaseActiveCounter() }
            newTitlesToInsert.forEach { println(it.id + " " + it.symbol) }
            println("TTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT")
            allLocalTitles.filter { it.category == AssetCategory.FIAT }.forEach {
                println(it.id + " " + it.symbol)
            }

            TitleRepository.marketUpdate(newTitlesToInsert, (existingTitlesToUpdate + removedTitlesToUpdate).toSet(), removedTitlesToDelete.toSet())

            // TODO Transaction Update
            existingTitlesToUpdate.filter { it.active == 50 }.forEach { LogRepository.insert("Activated  ${it.name} (${it.symbol}).") }
            removedTitlesToDelete.forEach { LogRepository.insert("Removed  ${it.name} (${it.symbol}) from local database due to long time inactivity.") }
        }

        return Result.success()
    }
}