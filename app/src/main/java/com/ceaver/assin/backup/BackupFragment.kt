package com.ceaver.assin.backup

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.work.*
import com.ceaver.assin.action.*
import com.ceaver.assin.alerts.Alert
import com.ceaver.assin.alerts.AlertRepository
import com.ceaver.assin.alerts.AlertType
import com.ceaver.assin.alerts.PriceAlert
import com.ceaver.assin.intentions.Intention
import com.ceaver.assin.intentions.IntentionRepository
import com.ceaver.assin.logging.LogRepository
import com.ceaver.assin.markets.CustomTitle
import com.ceaver.assin.markets.TitleRepository
import com.ceaver.assin.preferences.Preferences
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.backup_fragment.*
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.apache.commons.csv.CSVPrinter
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

private const val READ_EXTERNAL_STORAGE = 0
private const val WRITE_EXTERNAL_STORAGE = 1
private const val EXPORT_DIRECTORY_NAME = "assin"
private const val TITLE_FILE_NAME = "titles.csv"
private const val ACTION_FILE_NAME = "actions.csv"
private const val ALERT_FILE_NAME = "alerts.csv"
private const val INTENTION_FILE_NAME = "intentions.csv"

private fun getOrCreateDirectory(): File {
    val rootDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
    val targetDirectory = File(rootDir.path + "/" + EXPORT_DIRECTORY_NAME)
    if (!targetDirectory.exists()) targetDirectory.mkdir()
    return targetDirectory
}

// TODO try/catch handling on parse or any IO exception
class BackupFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(com.ceaver.assin.R.layout.backup_fragment, container, false)
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
        WorkManager.getInstance(requireContext())
                .beginWith(listOf(
                        OneTimeWorkRequestBuilder<TitleExportWorker>().build(),
                        OneTimeWorkRequestBuilder<ActionExportWorker>().build(),
                        OneTimeWorkRequestBuilder<AlertExportWorker>().build(),
                        OneTimeWorkRequestBuilder<IntentionExportWorker>().build()))
                .then(OneTimeWorkRequestBuilder<EnableButtonWorker>().build())
                .enqueue()
    }

    private fun startImport() {
        WorkManager.getInstance(requireContext())
                .beginWith(listOf(
                        OneTimeWorkRequestBuilder<TitleImportWorker>().build(),
                        OneTimeWorkRequestBuilder<ActionImportWorker>().build(),
                        OneTimeWorkRequestBuilder<AlertImportWorker>().build(),
                        OneTimeWorkRequestBuilder<IntentionImportWorker>().build()))
                .then(OneTimeWorkRequestBuilder<EnableButtonWorker>().build())
                .enqueue()
    }

    class TitleExportWorker(appContext: Context, workerParams: WorkerParameters) : CoroutineWorker(appContext, workerParams) {
        override suspend fun doWork(): Result {
            val titles = TitleRepository.loadAllCustomTitles()
            val targetDirectory = getOrCreateDirectory()
            val filePath = targetDirectory.path + "/" + TITLE_FILE_NAME
            val csvPrinter = CSVPrinter(Files.newBufferedWriter(Paths.get(filePath)), CSVFormat.DEFAULT)
            for (title in titles) csvPrinter.printRecord(title.toExport())
            csvPrinter.flush()
            LogRepository.insert("Export titles successful to '$filePath'")
            return Result.success()
        }
    }

    class ActionExportWorker(appContext: Context, workerParams: WorkerParameters) : CoroutineWorker(appContext, workerParams) {
        override suspend fun doWork(): Result {
            val actions = ActionRepository.loadAll()
            val targetDirectory = getOrCreateDirectory()
            val filePath = targetDirectory.path + "/" + ACTION_FILE_NAME
            val csvPrinter = CSVPrinter(Files.newBufferedWriter(Paths.get(filePath)), CSVFormat.DEFAULT)
            for (action in actions) csvPrinter.printRecord(action.toExport())
            csvPrinter.flush()
            LogRepository.insert("Export actions successful to '$filePath'")
            return Result.success()
        }
    }

    class AlertExportWorker(appContext: Context, workerParams: WorkerParameters) : CoroutineWorker(appContext, workerParams) {
        override suspend fun doWork(): Result {
            val alerts = AlertRepository.loadAll()
            val targetDirectory = getOrCreateDirectory()
            val filePath = targetDirectory.path + "/" + ALERT_FILE_NAME
            val csvPrinter = CSVPrinter(Files.newBufferedWriter(Paths.get(filePath)), CSVFormat.DEFAULT)
            for (alert in alerts) csvPrinter.printRecord(alert.toExport())
            csvPrinter.flush()
            LogRepository.insert("Export alerts successful to '$filePath'")
            return Result.success()
        }
    }

    class IntentionExportWorker(appContext: Context, workerParams: WorkerParameters) : CoroutineWorker(appContext, workerParams) {
        override suspend fun doWork(): Result {
            val intentions = IntentionRepository.loadAll()
            val targetDirectory = getOrCreateDirectory()
            val filePath = targetDirectory.path + "/" + INTENTION_FILE_NAME
            val csvPrinter = CSVPrinter(Files.newBufferedWriter(Paths.get(filePath)), CSVFormat.DEFAULT)
            for (intention in intentions) csvPrinter.printRecord(intention.toExport())
            csvPrinter.flush()
            LogRepository.insert("Export intentions successful to '$filePath'")
            return Result.success()
        }
    }

    class TitleImportWorker(appContext: Context, workerParams: WorkerParameters) : CoroutineWorker(appContext, workerParams) {
        override suspend fun doWork(): Result {
            val cryptoTitle = Preferences.getCryptoTitle()
            val sourceDirectory = getOrCreateDirectory()
            val filePath = sourceDirectory.path + "/" + TITLE_FILE_NAME;
            if (File(filePath).exists()) {
                val reader = Files.newBufferedReader(Paths.get(sourceDirectory.path + "/" + TITLE_FILE_NAME))
                val csvParser = CSVParser(reader, CSVFormat.DEFAULT)
                val csvTitles = csvParser.map { CustomTitle.fromImport(it, cryptoTitle) }.toList()
                val localTitles = TitleRepository.loadAllCustomTitles()
                // TODO minus doesn't work. check alternative

                val deletedTitles = localTitles.filterNot { csvTitles.map { it.id }.contains(it.id) }
                val newTitles = csvTitles.filterNot { localTitles.map { it.id }.contains(it.id) }
                val updateTitles = localTitles.filter { csvTitles.map { it.id }.contains(it.id) }.toSet()
                // TODO one tx
                TitleRepository.delete(deletedTitles)
                TitleRepository.update(updateTitles)
                TitleRepository.insert(newTitles.toSet())
                LogRepository.insert("Import titles from '$filePath' successful")
            } else {
                LogRepository.insert("Import titles failed. '$filePath' not found")
            }
            return Result.success()
        }
    }

    class ActionImportWorker(appContext: Context, workerParams: WorkerParameters) : CoroutineWorker(appContext, workerParams) {
        override suspend fun doWork(): Result {
            val sourceDirectory = getOrCreateDirectory()
            val filePath = sourceDirectory.path + "/" + ACTION_FILE_NAME;
            if (File(filePath).exists()) {
                val reader = Files.newBufferedReader(Paths.get(sourceDirectory.path + "/" + ACTION_FILE_NAME))
                val csvParser = CSVParser(reader, CSVFormat.DEFAULT)
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
                LogRepository.insert("Import actions from '$filePath' successful")
            } else {
                LogRepository.insert("Import actions failed. '$filePath' not found")
            }
            return Result.success()
        }
    }

    class AlertImportWorker(appContext: Context, workerParams: WorkerParameters) : CoroutineWorker(appContext, workerParams) {
        override suspend fun doWork(): Result {
            val sourceDirectory = getOrCreateDirectory()
            val filePath = sourceDirectory.path + "/" + ALERT_FILE_NAME;
            if (File(filePath).exists()) {
                val reader = Files.newBufferedReader(Paths.get(sourceDirectory.path + "/" + ALERT_FILE_NAME))
                val csvParser = CSVParser(reader, CSVFormat.DEFAULT)
                val alerts: List<Alert> = csvParser.map {
                    when (AlertType.valueOf(it.get(0))) {
                        AlertType.PRICE -> PriceAlert.fromImport(it)
                        AlertType.MARKETCAP -> TODO()
                        AlertType.DOMINANCE -> TODO()
                        AlertType.PORTFOLIO -> TODO()
                        AlertType.RANKING -> TODO()
                    }
                }.toList()
                AlertRepository.deleteAll()
                AlertRepository.insert(alerts)
                LogRepository.insert("Import alerts from '$filePath' successful")
            } else {
                LogRepository.insert("Import alerts failed. '$filePath' not found")
            }
            return Result.success()
        }
    }

    class IntentionImportWorker(appContext: Context, workerParams: WorkerParameters) : CoroutineWorker(appContext, workerParams) {
        override suspend fun doWork(): Result {
            val sourceDirectory = getOrCreateDirectory()
            val filePath = sourceDirectory.path + "/" + INTENTION_FILE_NAME;
            if (File(filePath).exists()) {
                val reader = Files.newBufferedReader(Paths.get(sourceDirectory.path + "/" + INTENTION_FILE_NAME))
                val csvParser = CSVParser(reader, CSVFormat.DEFAULT)
                val intentions = csvParser.map { Intention.fromImport(it) }.toList()
                IntentionRepository.deleteAll()
                IntentionRepository.insert(intentions)
                LogRepository.insert("Import intentions from '$filePath' successful")
            } else {
                LogRepository.insert("Import intentions failed. '$filePath' not found")
            }
            return Result.success()
        }
    }

    class EnableButtonWorker(appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams) {
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

    @Suppress("UNUSED_PARAMETER")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: BackupEvents.BackupOperationDone) {
        enableButtons()
        Snackbar.make(backupConstraintLayout, "Successfully done", Snackbar.LENGTH_LONG).show()
    }

    private fun hasPermission(permission: String) = ContextCompat.checkSelfPermission(requireContext(), permission) == PackageManager.PERMISSION_GRANTED

    private fun requestPermission(permission: String, requestCode: Int) = requestPermissions(arrayOf(permission), requestCode)

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
