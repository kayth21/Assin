package com.ceaver.assin.backup

import android.content.Context
import java.io.File
import java.util.*

object BackupUtils {

    fun buildFilename(entityName: String, customSuffix: String?): String {
        return (entityName + (if (customSuffix.isNullOrBlank()) "" else "-$customSuffix") + ".csv").toLowerCase(Locale.ROOT)
    }

    fun getApplicationDirectory(context: Context): File {
        val file = context.getExternalFilesDir(null)!!
        if (!file.exists()) {
            file.mkdirs()
        }
        return file
    }

}