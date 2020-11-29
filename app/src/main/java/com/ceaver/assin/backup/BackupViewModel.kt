package com.ceaver.assin.backup

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ceaver.assin.AssinApplication
import java.util.*

abstract class BackupViewModel : ViewModel() {

    var radioChecked = MutableLiveData<Int>()
    var existingBackups: List<String>

    init {
        val directory = BackupUtils.getApplicationDirectory(AssinApplication.appContext!!)
        existingBackups = directory.listFiles()
                .filter { it.extension == "csv" }
                .filter { it.name.contains("-") }
                .filter { it.name.startsWith(TITLE_FILE_NAME) || it.name.startsWith(ALERT_FILE_NAME) || it.name.startsWith(ACTION_FILE_NAME) || it.name.startsWith(INTENTION_FILE_NAME) }
                .sortedBy { it.lastModified() }
                .map { it.name }
                .map { it.removeSuffix(".csv") }
                .map { it.removePrefix(TITLE_FILE_NAME) }
                .map { it.removePrefix(ALERT_FILE_NAME) }
                .map { it.removePrefix(ACTION_FILE_NAME) }
                .map { it.removePrefix(INTENTION_FILE_NAME) }
                .map { it.removePrefix("-") }
                .map { it.toUpperCase(Locale.ROOT) }
                .distinct()
    }
}