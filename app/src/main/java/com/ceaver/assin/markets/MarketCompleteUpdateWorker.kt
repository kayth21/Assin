package com.ceaver.assin.markets

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.ceaver.assin.logging.LogRepository
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.*

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
            if (remoteTitles.none { title.symbol == it.symbol }) {
                if (title.inactive.isPresent) {
                    if (title.inactive.get().toLocalDate().equals(LocalDate.now())) {
                        // do nothing
                    } else if (title.inactive.get().hour < 5) {
                        TitleRepository.update(title.copy(inactive = Optional.of(title.inactive.get().plusHours(1))))
                        LogRepository.insertLog("Marked ${title.name} (${title.symbol}) as inactive (${title.inactive.get().hour + 1}/5") // TODO DELETE
                    } else {
                        LogRepository.insertLog("Removed ${title.name} (${title.symbol}) from local database because it was not sent for at least five days.")
                        TitleRepository.deleteTitle(title) // TODO check safe delete, maybe the app asks if it's ok to remove this title. Or at least check if there can be issues later because the title is used in later entities.
                    }
                } else {
                    TitleRepository.update(title.copy(inactive = Optional.of(LocalDateTime.of(LocalDate.now(), LocalTime.of(1, 0)))))
                }
            };
        }
        remoteTitles.forEach { title ->
            if (localTitles.none { title.symbol.equals(it.symbol) }) {
                LogRepository.insertLog("Added  ${title.name} (${title.symbol}) to local database.")
            }
        }
    }

}