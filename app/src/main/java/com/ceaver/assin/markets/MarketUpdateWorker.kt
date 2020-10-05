package com.ceaver.assin.markets

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.ceaver.assin.logging.LogRepository

class MarketUpdateWorker(appContext: Context, workerParams: WorkerParameters) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val allRemoteTitles = MarketRepository.loadAllTitles()
        val allRemoteCryptoTitles = allRemoteTitles.first
        val allRemoteFiatTitles = allRemoteTitles.second
        val allRemoteCustomTitles = allRemoteTitles.third
        val allRemoteCryptoTitleIds = allRemoteCryptoTitles.map { it.id }.toSet()
        val allLocalCryptoTitles = TitleRepository.loadAllCryptoTitles()
        val allLocalCryptoTitleIds = allLocalCryptoTitles.map { it.id }.toSet()

        if (allLocalCryptoTitles.isEmpty()) { // TODO initial load should be done elsewhere and better?
            TitleRepository.insert((allRemoteCryptoTitles.map { it.copy(active = 50) } + allRemoteFiatTitles).toSet())
        } else {
            // TODO needs to be done for Fiat and others as well
            val newCryptoTitlesToInsert = allRemoteCryptoTitles.filterNot { it.id in allLocalCryptoTitleIds }.toSet()
            val existingCryptoTitlesToUpdate = (allRemoteCryptoTitles - newCryptoTitlesToInsert).map { it.incrementActiveCounter() }
            val removedTitles = allLocalCryptoTitles.filterNot { it.id in allLocalCryptoTitleIds }
            val removedTitlesPartitioned = removedTitles.partition { it.inactive() }
            val removedTitlesToDelete = removedTitlesPartitioned.first
            val removedTitlesToUpdate = removedTitlesPartitioned.second
            removedTitlesToUpdate.forEach { it.decreaseActiveCounter() }

            TitleRepository.marketUpdate(newCryptoTitlesToInsert, (existingCryptoTitlesToUpdate + removedTitlesToUpdate + allRemoteCustomTitles).toSet(), removedTitlesToDelete.toSet())

            // TODO Transaction Update
            existingCryptoTitlesToUpdate.filter { it.active == 50 }.forEach { LogRepository.insert("Activated  ${it.name} (${it.symbol}).") }
            removedTitlesToDelete.forEach { LogRepository.insert("Removed  ${it.name} (${it.symbol}) from local database due to long time inactivity.") }
        }

        return Result.success()
    }
}