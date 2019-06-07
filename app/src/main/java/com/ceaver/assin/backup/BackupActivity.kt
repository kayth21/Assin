package com.ceaver.assin.backup

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.ceaver.assin.alerts.Alert
import com.ceaver.assin.alerts.AlertRepository
import com.ceaver.assin.alerts.AlertType
import com.ceaver.assin.logging.LogRepository
import com.ceaver.assin.trades.Trade
import com.ceaver.assin.trades.TradeRepository
import kotlinx.android.synthetic.main.activity_backup.*
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.apache.commons.csv.CSVPrinter
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.time.LocalDate
import java.util.*

private const val READ_EXTERNAL_STORAGE = 0
private const val WRITE_EXTERNAL_STORAGE = 1
private const val EXPORT_DIRECTORY_NAME = "assin"
private const val TRADE_FILE_NAME = "trades.csv"
private const val ALERT_FILE_NAME = "alerts.csv"

private fun getOrCreateDirectory(): File {
    val rootDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
    val targetDirectory = File(rootDir.path + "/" + EXPORT_DIRECTORY_NAME)
    if (!targetDirectory.exists()) targetDirectory.mkdir()
    return targetDirectory
}

// TODO try/catch handling on parse or any IO exception
class BackupActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        publishView()
    }

    override fun onStart() {
        super.onStart()
        exportButton.setOnClickListener { onExportClick() }
        importButton.setOnClickListener { onImportClick() }
        EventBus.getDefault().register(this);
    }

    override fun onStop() {
        super.onStop()
        exportButton.setOnClickListener(null)
        importButton.setOnClickListener(null)
        EventBus.getDefault().unregister(this);
    }

    private fun publishView() = setContentView(com.ceaver.assin.R.layout.activity_backup)

    private fun onExportClick() {
        disableButtons()
        if (hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            startExport()
        } else
            requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE)
    }

    private fun onImportClick() {
        disableButtons()
        if (hasPermission(Manifest.permission.READ_EXTERNAL_STORAGE)) {
            startImport()
        } else
            requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE)
    }

    private fun startExport() {
        WorkManager.getInstance()
                .beginWith(listOf(OneTimeWorkRequestBuilder<TradeExportWorker>().build(), OneTimeWorkRequestBuilder<AlertExportWorker>().build()))
                .then(OneTimeWorkRequestBuilder<EnableButtonWorker>().build())
                .enqueue()
    }

    private fun startImport() {
        WorkManager.getInstance()
                .beginWith(listOf(OneTimeWorkRequestBuilder<TradeImportWorker>().build(), OneTimeWorkRequestBuilder<AlertImportWorker>().build()))
                .then(OneTimeWorkRequestBuilder<EnableButtonWorker>().build())
                .enqueue()
    }

    private class TradeExportWorker(appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams) {
        override fun doWork(): Result {
            val trades = TradeRepository.loadAllTrades()
            val targetDirectory = getOrCreateDirectory()
            val filePath = targetDirectory.path + "/" + TRADE_FILE_NAME
            val csvPrinter = CSVPrinter(Files.newBufferedWriter(Paths.get(filePath)), CSVFormat.DEFAULT)
            for (trade in trades) csvPrinter.printRecord(trade.tradeDate, trade.buySymbol.orElse(""), if (trade.buyAmount.isPresent) trade.buyAmount.get() else "", trade.sellSymbol.orElse(""), if (trade.sellAmount.isPresent) trade.sellAmount.get() else "", trade.comment)
            csvPrinter.flush()
            LogRepository.insertLogAsync("Export trades successful to '$filePath'")
            return Result.success()
        }
    }

    private class AlertExportWorker(appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams) {
        override fun doWork(): Result {
            val alerts = AlertRepository.loadAllAlerts()
            val targetDirectory = getOrCreateDirectory()
            val filePath = targetDirectory.path + "/" + ALERT_FILE_NAME
            val csvPrinter = CSVPrinter(Files.newBufferedWriter(Paths.get(filePath)), CSVFormat.DEFAULT)
            for (alert in alerts) csvPrinter.printRecord(alert.symbol, alert.reference, alert.alertType, alert.source, alert.target)
            csvPrinter.flush()
            LogRepository.insertLogAsync("Export alerts successful to '$filePath'")
            return Result.success()
        }
    }

    private class TradeImportWorker(appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams) {
        override fun doWork(): Result {
            val sourceDirectory = getOrCreateDirectory()
            val filePath = sourceDirectory.path + "/" + TRADE_FILE_NAME;
            if (File(filePath).exists()) {
                val reader = Files.newBufferedReader(Paths.get(sourceDirectory.path + "/" + TRADE_FILE_NAME))
                val csvParser = CSVParser(reader, CSVFormat.DEFAULT)
                val trades = csvParser.map { Trade(0, LocalDate.parse(it.get(0)), Optional.ofNullable(it.get(1)), Optional.ofNullable(it.get(2).toDoubleOrNull()), Optional.ofNullable(it.get(3)), Optional.ofNullable(it.get(4).toDoubleOrNull())) }.toList()
                TradeRepository.deleteAllTrades();
                TradeRepository.insertTrades(trades)
                LogRepository.insertLogAsync("Import trades from '$filePath' successful")
                return Result.success()
            } else {
                LogRepository.insertLogAsync("Import trades failed. '$filePath' not found")
                return Result.failure()
            }
        }
    }

    private class AlertImportWorker(appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams) {
        override fun doWork(): Result {
            val sourceDirectory = getOrCreateDirectory()
            val filePath = sourceDirectory.path + "/" + ALERT_FILE_NAME;
            return if (File(filePath).exists()) {
                val reader = Files.newBufferedReader(Paths.get(sourceDirectory.path + "/" + ALERT_FILE_NAME))
                val csvParser = CSVParser(reader, CSVFormat.DEFAULT)
                val alerts = csvParser.map { Alert(0, it.get(0), it.get(1), AlertType.valueOf(it.get(2)), it.get(3).toDouble(), it.get(4).toDouble()) }.toList()
                AlertRepository.deleteAllAlerts()
                AlertRepository.insertAlerts(alerts)
                LogRepository.insertLogAsync("Import alerts from '$filePath' successful")
                Result.success()
            } else {
                LogRepository.insertLogAsync("Import alerts failed. '$filePath' not found")
                Result.failure()
            }
        }
    }

    private class EnableButtonWorker(appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams) {
        override fun doWork(): Result {
            EventBus.getDefault().post(BackupEvents.BackupOperationDone())
            return Result.success()
        }
    }

    private fun disableButtons() {
        enableButtons(false)
    }

    private fun enableButtons() {
        enableButtons(true)
    }

    private fun enableButtons(enable: Boolean) {
        exportButton.isEnabled = enable
        importButton.isEnabled = enable
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: BackupEvents.BackupOperationDone) {
        enableButtons()
        Snackbar.make(backupConstraintLayout, "Successfully done", Snackbar.LENGTH_LONG).show()
    }

    private fun hasPermission(permission: String) = ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED

    private fun requestPermission(permission: String, requestCode: Int) = ActivityCompat.requestPermissions(this, arrayOf(permission), requestCode)

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val permissionGranted = grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
        when (requestCode) {
            WRITE_EXTERNAL_STORAGE ->
                if (permissionGranted) startExport() else enableButtons()
            READ_EXTERNAL_STORAGE ->
                if (permissionGranted) startImport() else enableButtons()
        }
    }
}
