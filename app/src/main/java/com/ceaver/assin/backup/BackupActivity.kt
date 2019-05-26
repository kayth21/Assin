package com.ceaver.assin.backup

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import com.ceaver.assin.alerts.Alert
import com.ceaver.assin.alerts.AlertRepository
import com.ceaver.assin.alerts.AlertType
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
    private val WRITE_EXTERNAL_STORAGE = 0

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
        val targetDirectory = getTargetDirectory()
        val csvPrinter = CSVPrinter(Files.newBufferedWriter(Paths.get(targetDirectory.path + "/" + EXPORT_FILE_NAME)), CSVFormat.DEFAULT)
        for (alert in alerts) csvPrinter.printRecord(alert.symbol, alert.reference, alert.alertType, alert.source, alert.target)
        csvPrinter.flush()
        enableButtons()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == WRITE_EXTERNAL_STORAGE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                exportAlerts()
            } else {
                enableButtons()
            }
        }
    }

    private fun onImportClick() {
        disableButtons()
        val targetDirectory = getTargetDirectory()
        val reader = Files.newBufferedReader(Paths.get(targetDirectory.path + "/" + EXPORT_FILE_NAME))
        val csvParser = CSVParser(reader, CSVFormat.DEFAULT)
        val alerts = csvParser.map { Alert(0, it.get(0), it.get(1), AlertType.valueOf(it.get(2)), it.get(3).toDouble(), it.get(4).toDouble()) }.toList()
        AlertRepository.deleteAllAlertsAsync(true) { AlertRepository.insertAlertsAsync(alerts, true) { enableButtons() } }
    }

    private fun getTargetDirectory(): File {
        // TODO check if there is an external accessable storage, if not get internal storage.
        val targetDirectory = File(Environment.getExternalStorageDirectory().path + "/" + EXPORT_DIRECTORY_NAME)
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
