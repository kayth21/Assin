package com.ceaver.assin.backup.create

import android.content.Context
import com.ceaver.assin.R
import com.ceaver.assin.action.ActionRepository
import com.ceaver.assin.alerts.AlertRepository
import com.ceaver.assin.backup.*
import com.ceaver.assin.common.Exportable
import com.ceaver.assin.intentions.IntentionRepository
import com.ceaver.assin.logging.LogRepository
import com.ceaver.assin.markets.TitleRepository
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVPrinter
import java.io.File
import java.io.FileWriter

class BackupCreateViewModel : BackupViewModel() {

    init {
        if (existingBackups.isEmpty())
            radioChecked.postValue(R.id.backupCreateFragmenNewBackupRadioButton)
    }

    suspend fun createBackup(backupName: String, context: Context): String {
        val directory = BackupUtils.getApplicationDirectory(context)
        createBackup(TitleRepository.loadAllCustomTitles(), TITLE_FILE_NAME, backupName, directory)
        createBackup(AlertRepository.loadAll(), ALERT_FILE_NAME, backupName, directory)
        createBackup(ActionRepository.loadAll(), ACTION_FILE_NAME, backupName, directory)
        createBackup(IntentionRepository.loadAll(), INTENTION_FILE_NAME, backupName, directory)
        return directory.path
    }

    private suspend fun createBackup(exportables: List<Exportable>, entityName: String, backupName: String, directory: File) {
        val filename = BackupUtils.buildFilename(entityName, backupName)
        val logId = LogRepository.insert("Creating file \"$filename\" in directory \"${directory.path}\"...")
        val file = File(directory, filename)
        val csvPrinter = CSVPrinter(FileWriter(file), CSVFormat.DEFAULT)
        for (exportable in exportables) csvPrinter.printRecord(exportable.toExport())
        csvPrinter.flush()
        val log = LogRepository.loadLog(logId)
        LogRepository.update(log.copy(message = log.message + "done"))
    }
}