package com.ceaver.assin.backup

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.ceaver.assin.action.Action
import com.ceaver.assin.action.ActionRepository
import com.ceaver.assin.action.ActionType
import com.ceaver.assin.alerts.Alert
import com.ceaver.assin.alerts.AlertRepository
import com.ceaver.assin.alerts.AlertType
import com.ceaver.assin.intentions.Intention
import com.ceaver.assin.intentions.IntentionRepository
import com.ceaver.assin.intentions.IntentionStatus
import com.ceaver.assin.intentions.IntentionType
import com.ceaver.assin.logging.LogRepository
import com.ceaver.assin.markets.TitleRepository
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
import java.time.LocalDate

private const val READ_EXTERNAL_STORAGE = 0
private const val WRITE_EXTERNAL_STORAGE = 1
private const val EXPORT_DIRECTORY_NAME = "assin"
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
                        OneTimeWorkRequestBuilder<ActionExportWorker>().build(),
                        OneTimeWorkRequestBuilder<AlertExportWorker>().build(),
                        OneTimeWorkRequestBuilder<IntentionExportWorker>().build()))
                .then(OneTimeWorkRequestBuilder<EnableButtonWorker>().build())
                .enqueue()
    }

    private fun startImport() {
        WorkManager.getInstance(requireContext())
                .beginWith(listOf(
                        OneTimeWorkRequestBuilder<ActionImportWorker>().build(),
                        OneTimeWorkRequestBuilder<AlertImportWorker>().build(),
                        OneTimeWorkRequestBuilder<IntentionImportWorker>().build()))
                .then(OneTimeWorkRequestBuilder<EnableButtonWorker>().build())
                .enqueue()
    }

    class ActionExportWorker(appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams) {
        override fun doWork(): Result {
            val actions = ActionRepository.loadAllActions()
            val targetDirectory = getOrCreateDirectory()
            val filePath = targetDirectory.path + "/" + ACTION_FILE_NAME
            val csvPrinter = CSVPrinter(Files.newBufferedWriter(Paths.get(filePath)), CSVFormat.DEFAULT)
            for (action in actions) csvPrinter.printRecord(action.actionDate, action.buyTitle?.symbol.orEmpty(), if (action.buyAmount != null) action.buyAmount!!.toPlainString() else "", action.sellTitle?.symbol.orEmpty(), if (action.sellAmount != null) action.sellAmount!!.toPlainString() else "", action.comment.orEmpty(), action.actionType.name, if (action.positionId != null) action.positionId!!.toPlainString() else "", if (action.splitAmount != null) action.splitAmount.toPlainString() else "", if (action.splitRemaining != null) action.splitRemaining.toPlainString() else "", action.splitTitle?.symbol.orEmpty(), if (action.valueBtc != null) action.valueBtc.toPlainString() else "", if (action.valueUsd != null) action.valueUsd.toPlainString() else "")
            csvPrinter.flush()
            LogRepository.insertLogAsync("Export actions successful to '$filePath'")
            return Result.success()
        }
    }

    class AlertExportWorker(appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams) {
        override fun doWork(): Result {
            val alerts = AlertRepository.loadAllAlerts()
            val targetDirectory = getOrCreateDirectory()
            val filePath = targetDirectory.path + "/" + ALERT_FILE_NAME
            val csvPrinter = CSVPrinter(Files.newBufferedWriter(Paths.get(filePath)), CSVFormat.DEFAULT)
            for (alert in alerts) csvPrinter.printRecord(alert.symbol.symbol, alert.reference.symbol, alert.alertType, alert.source.toPlainString(), alert.target.toPlainString())
            csvPrinter.flush()
            LogRepository.insertLogAsync("Export alerts successful to '$filePath'")
            return Result.success()
        }
    }

    class IntentionExportWorker(appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams) {
        override fun doWork(): Result {
            val intentions = IntentionRepository.loadAllIntentions()
            val targetDirectory = getOrCreateDirectory()
            val filePath = targetDirectory.path + "/" + INTENTION_FILE_NAME
            val csvPrinter = CSVPrinter(Files.newBufferedWriter(Paths.get(filePath)), CSVFormat.DEFAULT)
            for (intention in intentions) csvPrinter.printRecord(intention.type, intention.title.symbol, intention.amountAsString(), intention.referenceTitle.symbol, intention.referencePrice.toPlainString(), intention.creationDate, intention.status, intention.comment.orEmpty())
            csvPrinter.flush()
            LogRepository.insertLogAsync("Export intentions successful to '$filePath'")
            return Result.success()
        }
    }

    class ActionImportWorker(appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams) {
        override fun doWork(): Result {
            val sourceDirectory = getOrCreateDirectory()
            val filePath = sourceDirectory.path + "/" + ACTION_FILE_NAME;
            if (File(filePath).exists()) {
                val reader = Files.newBufferedReader(Paths.get(sourceDirectory.path + "/" + ACTION_FILE_NAME))
                val csvParser = CSVParser(reader, CSVFormat.DEFAULT)
                val actions = csvParser.map {
                    val actionDate = LocalDate.parse(it.get(0))
                    val buyTitleString = it.get(1)
                    val buyTitle = if (buyTitleString.isEmpty()) null else TitleRepository.loadTitleBySymbol(buyTitleString)
                    val buyAmount = it.get(2).toBigDecimalOrNull()
                    val sellTitleString = it.get(3)
                    val sellTitle = if (sellTitleString.isEmpty()) null else TitleRepository.loadTitleBySymbol(sellTitleString)
                    val sellAmount = it.get(4).toBigDecimalOrNull()
                    val comment = it.get(5).ifEmpty { null }
                    val actionType = ActionType.valueOf(it.get(6))
                    val positionId = it.get(7).toBigDecimalOrNull()
                    val splitAmount = it.get(8).toBigDecimalOrNull()
                    val splitRemaining = it.get(9).toBigDecimalOrNull()
                    val splitTitleString = it.get(10)
                    val splitTitle = if (splitTitleString.isEmpty()) null else TitleRepository.loadTitleBySymbol(splitTitleString)
                    val priceBtc = it.get(11).toBigDecimalOrNull()
                    val priceUsd = it.get(12).toBigDecimalOrNull()
                    // TODO check if any titleString is notNull but corresponding title is null. Happens if no title is not active anymore. Abort import.
                    Action(0, actionDate, buyTitle, buyAmount, sellTitle, sellAmount, comment, actionType, positionId, splitAmount, splitRemaining, splitTitle, priceBtc, priceUsd)
                }.toList()
                ActionRepository.deleteAllActions()
                ActionRepository.insertActions(actions)
                LogRepository.insertLogAsync("Import actions from '$filePath' successful")
            } else {
                LogRepository.insertLogAsync("Import actions failed. '$filePath' not found")
            }
            return Result.success()
        }
    }

    class AlertImportWorker(appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams) {
        override fun doWork(): Result {
            val sourceDirectory = getOrCreateDirectory()
            val filePath = sourceDirectory.path + "/" + ALERT_FILE_NAME;
            if (File(filePath).exists()) {
                val reader = Files.newBufferedReader(Paths.get(sourceDirectory.path + "/" + ALERT_FILE_NAME))
                val csvParser = CSVParser(reader, CSVFormat.DEFAULT)
                val alerts = csvParser.map { Alert(0, TitleRepository.loadTitleBySymbol(it.get(0)), TitleRepository.loadTitleBySymbol(it.get(1)), AlertType.valueOf(it.get(2)), it.get(3).toBigDecimal(), it.get(4).toBigDecimal()) }.toList()
                AlertRepository.deleteAllAlerts()
                AlertRepository.insertAlerts(alerts)
                LogRepository.insertLogAsync("Import alerts from '$filePath' successful")
            } else {
                LogRepository.insertLogAsync("Import alerts failed. '$filePath' not found")
            }
            return Result.success()
        }
    }

    class IntentionImportWorker(appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams) {
        override fun doWork(): Result {
            val sourceDirectory = getOrCreateDirectory()
            val filePath = sourceDirectory.path + "/" + INTENTION_FILE_NAME;
            if (File(filePath).exists()) {
                val reader = Files.newBufferedReader(Paths.get(sourceDirectory.path + "/" + INTENTION_FILE_NAME))
                val csvParser = CSVParser(reader, CSVFormat.DEFAULT)
                val intentions = csvParser.map { Intention(0, IntentionType.valueOf(it.get(0)), TitleRepository.loadTitleBySymbol(it.get(1)), it.get(2).toBigDecimalOrNull(), TitleRepository.loadTitleBySymbol(it.get(3)), it.get(4).toBigDecimal(), LocalDate.parse(it.get(5)), IntentionStatus.valueOf(it.get(6)), it.get(7).ifEmpty { null }) }.toList()
                IntentionRepository.deleteAllIntentions()
                IntentionRepository.insertIntentions(intentions)
                LogRepository.insertLogAsync("Import intentions from '$filePath' successful")
            } else {
                LogRepository.insertLogAsync("Import intentions failed. '$filePath' not found")
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

    private fun requestPermission(permission: String, requestCode: Int) = ActivityCompat.requestPermissions(requireActivity(), arrayOf(permission), requestCode)

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