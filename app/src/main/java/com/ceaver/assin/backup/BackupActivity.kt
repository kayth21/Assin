package com.ceaver.assin.backup

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import com.ceaver.assin.MyApplication
import com.ceaver.assin.alerts.Alert
import com.ceaver.assin.alerts.AlertRepository
import com.ceaver.assin.alerts.AlertType
import com.ceaver.assin.logging.LogRepository
import kotlinx.android.synthetic.main.activity_backup.*
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.apache.commons.csv.CSVPrinter
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths


class BackupActivity : AppCompatActivity() {

    private val EXPORT_DIRECTORY_NAME = "assin"
    private val EXPORT_FILE_NAME = "export.csv"
    private val READ_EXTERNAL_STORAGE = 0
    private val WRITE_EXTERNAL_STORAGE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        publishView()
    }

    private fun publishView() = setContentView(com.ceaver.assin.R.layout.activity_backup)

    override fun onStart() {
        super.onStart()
        exportButton.setOnClickListener { onExportClick() }
        importButton.setOnClickListener { onImportClick() }
    }

    override fun onStop() {
        super.onStop()
        exportButton.setOnClickListener(null)
        importButton.setOnClickListener(null)
    }

    private fun onExportClick() {
        disableButtons()
        if (hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) exportAlerts()
        else ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), WRITE_EXTERNAL_STORAGE)
    }

    private fun exportAlerts() {
        AlertRepository.loadAllAlertsAsync(true) { exportAlerts(it) }
    }

    private fun exportAlerts(alerts: List<Alert>) {
        val targetDirectory = getOrCreateDirectory()
        val filePath = targetDirectory.path + "/" + EXPORT_FILE_NAME
        val csvPrinter = CSVPrinter(Files.newBufferedWriter(Paths.get(filePath)), CSVFormat.DEFAULT)
        for (alert in alerts) csvPrinter.printRecord(alert.symbol, alert.reference, alert.alertType, alert.source, alert.target)
        csvPrinter.flush()
        val message = "Export successful to '$filePath'"
        LogRepository.insertLogAsync(message)
        Snackbar.make(backupConstraintLayout, message, Snackbar.LENGTH_SHORT).show()
        enableButtons()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val permissionGranted = grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
        when (requestCode) {
            WRITE_EXTERNAL_STORAGE ->
                if (permissionGranted) exportAlerts() else enableButtons()
            READ_EXTERNAL_STORAGE ->
                if (permissionGranted) importAlerts() else enableButtons()
        }
    }

    private fun onImportClick() {
        disableButtons()
        if (hasPermission(Manifest.permission.READ_EXTERNAL_STORAGE)) importAlerts()
        else ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), READ_EXTERNAL_STORAGE)
    }

    private fun importAlerts() {
        val sourceDirectory = getOrCreateDirectory()
        val filePath = sourceDirectory.path + "/" + EXPORT_FILE_NAME;
        if (File(filePath).exists()) {
            val reader = Files.newBufferedReader(Paths.get(sourceDirectory.path + "/" + EXPORT_FILE_NAME))
            val csvParser = CSVParser(reader, CSVFormat.DEFAULT)
            val alerts = csvParser.map { Alert(0, it.get(0), it.get(1), AlertType.valueOf(it.get(2)), it.get(3).toDouble(), it.get(4).toDouble()) }.toList()
            AlertRepository.deleteAllAlertsAsync(true) {
                AlertRepository.insertAlertsAsync(alerts, true) {
                    enableButtons()
                    val message = "Import '$filePath' successful"
                    LogRepository.insertLogAsync(message)
                    Snackbar.make(backupConstraintLayout, message, Snackbar.LENGTH_SHORT).show()
                }
            }
        } else {
            val message = "Import failed. '$filePath' not found"
            LogRepository.insertLogAsync(message)
            Snackbar.make(backupConstraintLayout, message, Snackbar.LENGTH_LONG).show()
            enableButtons()
        }
    }

    private fun getOrCreateDirectory(): File {
        val mediaMounted = Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
        val rootDir = if (mediaMounted) Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) else MyApplication.appContext!!.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
        val targetDirectory = File(rootDir.path + "/" + EXPORT_DIRECTORY_NAME)
        if (!targetDirectory.exists()) targetDirectory.mkdir()
        return targetDirectory
    }

    private fun disableButtons() {
        exportButton.isEnabled = false
        importButton.isEnabled = false
    }

    private fun enableButtons() {
        exportButton.isEnabled = true
        importButton.isEnabled = true
    }

    private fun hasPermission(permission: String) = ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
}
