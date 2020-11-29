package com.ceaver.assin.backup.delete

import android.content.Context
import com.ceaver.assin.backup.*
import com.ceaver.assin.logging.LogRepository
import java.io.File

class BackupDeleteViewModel : BackupViewModel() {

    suspend fun deleteBackup(backupName: String, context: Context) {
        val directory = BackupUtils.getApplicationDirectory(context)
        deleteBackup(TITLE_FILE_NAME, backupName, directory)
        deleteBackup(ALERT_FILE_NAME, backupName, directory)
        deleteBackup(ACTION_FILE_NAME, backupName, directory)
        deleteBackup(INTENTION_FILE_NAME, backupName, directory)
    }

    private suspend fun deleteBackup(entityName: String, backupName: String, directory: File) {
        val filename = BackupUtils.buildFilename(entityName, backupName)
        val id = LogRepository.insert("Deleting file \"$filename\" in directory \"${directory.path}\"...")
        directory.listFiles()
                .filter { it.name == filename }
                .forEach { it.delete() }
        val log = LogRepository.loadLog(id)
        LogRepository.update(log.copy(message = log.message + "done"))
    }
}