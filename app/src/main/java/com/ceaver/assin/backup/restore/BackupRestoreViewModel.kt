package com.ceaver.assin.backup.restore

import android.content.Context
import com.ceaver.assin.action.*
import com.ceaver.assin.alerts.*
import com.ceaver.assin.backup.*
import com.ceaver.assin.intentions.Intention
import com.ceaver.assin.intentions.IntentionRepository
import com.ceaver.assin.logging.LogRepository
import com.ceaver.assin.markets.CustomTitle
import com.ceaver.assin.markets.TitleRepository
import com.ceaver.assin.preferences.Preferences
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import java.io.File
import java.io.InputStreamReader

class BackupRestoreViewModel : BackupViewModel() {

    suspend fun restoreBackup(backupName: String, context: Context) {
        val directory = BackupUtils.getApplicationDirectory(context)
        restoreTitleBackup(backupName, directory)
        restoreActionBackup(backupName, directory)
        restoreAlertBackup(backupName, directory)
        restoreIntentionBackup(backupName, directory)
    }

    private suspend fun restoreTitleBackup(backupName: String, directory: File) {
        val filename = BackupUtils.buildFilename(TITLE_FILE_NAME, backupName)
        val id = LogRepository.insert("Restoring file \"$filename\" in directory \"${directory.path}\"...")
        val file = File(directory, filename)
        val inputStreamReader = InputStreamReader(file.inputStream())
        val csvParser = CSVParser(inputStreamReader, CSVFormat.DEFAULT)
        val cryptoTitle = Preferences.getCryptoTitle()

        val csvTitles = csvParser.map { CustomTitle.fromImport(it, cryptoTitle) }.toList()
        val localTitles = TitleRepository.loadAllCustomTitles()

        val deletedTitles = localTitles.filterNot { csvTitles.map { it.id }.contains(it.id) }
        val newTitles = csvTitles.filterNot { localTitles.map { it.id }.contains(it.id) }
        val updateTitles = localTitles.filter { csvTitles.map { it.id }.contains(it.id) }.toSet()
        // TODO one tx
        TitleRepository.delete(deletedTitles)
        TitleRepository.update(updateTitles)
        TitleRepository.insert(newTitles.toSet())

        val log = LogRepository.loadLog(id)
        LogRepository.update(log.copy(message = log.message + "done"))
    }

    private suspend fun restoreActionBackup(backupName: String, directory: File) {
        val filename = BackupUtils.buildFilename(ACTION_FILE_NAME, backupName)
        val id = LogRepository.insert("Restoring file \"$filename\" in directory \"${directory.path}\"...")
        val file = File(directory, filename)
        val inputStreamReader = InputStreamReader(file.inputStream())
        val csvParser = CSVParser(inputStreamReader, CSVFormat.DEFAULT)
        val actions: List<Action> = csvParser.map {
            when (ActionType.valueOf(it.get(0))) {
                ActionType.TRADE -> Trade.fromImport(it)
                ActionType.SPLIT -> Split.fromImport(it)
                ActionType.WITHDRAW -> Withdraw.fromImport(it)
                ActionType.DEPOSIT -> Deposit.fromImport(it)
                ActionType.MERGE -> Merge.fromImport(it)
                ActionType.MOVE -> Move.fromImport(it)
            }
        }.toList()
        ActionRepository.deleteAll()
        ActionRepository.insertAll(actions)

        val log = LogRepository.loadLog(id)
        LogRepository.update(log.copy(message = log.message + "done"))
    }

    private suspend fun restoreAlertBackup(backupName: String, directory: File) {
        val filename = BackupUtils.buildFilename(ALERT_FILE_NAME, backupName)
        val id = LogRepository.insert("Restoring file \"$filename\" in directory \"${directory.path}\"...")
        val file = File(directory, filename)
        val inputStreamReader = InputStreamReader(file.inputStream())
        val csvParser = CSVParser(inputStreamReader, CSVFormat.DEFAULT)
        val alerts: List<Alert> = csvParser.map {
            when (AlertType.valueOf(it.get(0))) {
                AlertType.PRICE -> PriceAlert.fromImport(it)
                AlertType.MARKETCAP -> MarketcapAlert.fromImport(it)
                AlertType.PORTFOLIO -> PortfolioAlert.fromImport(it)
            }
        }.toList()
        AlertRepository.deleteAll()
        AlertRepository.insert(alerts)

        val log = LogRepository.loadLog(id)
        LogRepository.update(log.copy(message = log.message + "done"))
    }

    private suspend fun restoreIntentionBackup(backupName: String, directory: File) {
        val filename = BackupUtils.buildFilename(INTENTION_FILE_NAME, backupName)
        val id = LogRepository.insert("Restoring file \"$filename\" in directory \"${directory.path}\"...")
        val file = File(directory, filename)
        val inputStreamReader = InputStreamReader(file.inputStream())
        val csvParser = CSVParser(inputStreamReader, CSVFormat.DEFAULT)
        val intentions = csvParser.map { Intention.fromImport(it) }.toList()
        IntentionRepository.deleteAll()
        IntentionRepository.insert(intentions)

        val log = LogRepository.loadLog(id)
        LogRepository.update(log.copy(message = log.message + "done"))
    }
}