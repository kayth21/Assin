package com.ceaver.assin.alerts

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.ArrayAdapter
import com.ceaver.assin.R
import com.ceaver.assin.common.SpinnerSelectionListener
import com.ceaver.assin.extensions.afterTextChanged
import com.ceaver.assin.extensions.format
import com.ceaver.assin.extensions.registerInputValidator
import com.ceaver.assin.markets.Title
import kotlinx.android.synthetic.main.activity_alert_input.*
import java.util.*

class AlertInputActivity : AppCompatActivity() {

    companion object {
        val INTENT_EXTRA_ALERT_ID = UUID.randomUUID().toString()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        publishView()

        val alertId = lookupAlertId()
        val viewModel = lookupViewModel().init(alertId)

        bindActions(viewModel)
        bindSymbol(viewModel)
        bindReference(viewModel)
        bindAlert(viewModel)
        observeStatus(viewModel)
        bindViewLogic(viewModel)
        bindFieldValidators()
    }

    private fun publishView() = setContentView(R.layout.activity_alert_input)

    private fun lookupAlertId() = intent.getLongExtra(INTENT_EXTRA_ALERT_ID, 0)

    private fun lookupViewModel(): AlertViewModel = ViewModelProviders.of(this).get(AlertViewModel::class.java)

    private fun bindActions(viewModel: AlertViewModel) {
        alertSaveButton.setOnClickListener { onSaveClick() }
    }

    private fun bindSymbol(viewModel: AlertViewModel) {
        val adapter = ArrayAdapter<Title>(this, android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        alertSymbolText.setAdapter(adapter)
        viewModel.symbol.observe(this, Observer { adapter.addAll(it!!); updateSpinnerFields(viewModel) })
    }

    private fun bindReference(viewModel: AlertViewModel) {
        val adapter = ArrayAdapter<Title>(this, android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        alertReferenceText.setAdapter(adapter)
        viewModel.reference.observe(this, Observer { adapter.addAll(it!!); updateSpinnerFields(viewModel) })
    }

    private fun bindAlert(viewModel: AlertViewModel) {
        viewModel.alert.observe(this, Observer { bindFields(it!!, viewModel); alertSaveButton.isEnabled = true })
    }

    private fun bindViewLogic(viewModel: AlertViewModel) {
        fun updatePrice() {
            if (viewModel.isNew() && alertSymbolText.selectedItem != null && alertReferenceText.selectedItem != null) {
                val symbol = alertSymbolText.selectedItem as Title
                val reference = alertReferenceText.selectedItem as Title
                lookupViewModel().lookupPrice(symbol, reference) {
                    alertSourceEditText.setText(it.first.format(reference.symbol))
                    alertTargetEditText.setText(it.second.format(reference.symbol))
                }
            }
        }

        fun updateUnit() {
            val symbol = alertReferenceText.selectedItem as Title
            startUnitTextView.text = symbol.symbol
            targetUnitTextView.text = symbol.symbol
        }
        alertSymbolText.onItemSelectedListener = SpinnerSelectionListener() { updatePrice(); checkSaveButton() }
        alertReferenceText.onItemSelectedListener = SpinnerSelectionListener() { updateUnit(); updatePrice(); checkSaveButton() }
    }

    private fun bindFields(alert: Alert, viewModel: AlertViewModel) {
        alertSourceEditText.setText(alert.source.format(alert.reference.symbol))
        alertTargetEditText.setText(alert.target.format(alert.reference.symbol))

        updateSpinnerFields(viewModel)

        alertSymbolText.isEnabled = alert.isNew()
        alertReferenceText.isEnabled = alert.isNew()
    }

    private fun updateSpinnerFields(viewModel: AlertViewModel) {
        if (viewModel.alert.value != null && viewModel.symbol.value != null && viewModel.reference.value != null) {
            alertSymbolText.setSelection(viewModel.symbol.value!!.indexOf(viewModel.alert.value!!.symbol))
            alertReferenceText.setSelection(lookupViewModel().reference.value!!.indexOf(viewModel.alert.value!!.reference))
        }
    }

    private fun observeStatus(viewModel: AlertViewModel) {
        viewModel.status.observe(this, Observer {
            when (it) {
                AlertViewModel.AlertInputStatus.START_SAVE -> onStartSave()
                AlertViewModel.AlertInputStatus.END_SAVE -> onEndSave()
            }
        })
    }

    private fun onSaveClick() {
        val symbol = alertSymbolText.selectedItem as Title
        val reference = alertReferenceText.selectedItem as Title
        val startPrice = alertSourceEditText.text.toString().toDouble()
        val targetPrice = alertTargetEditText.text.toString().toDouble()
        lookupViewModel().onSaveClick(symbol, reference, startPrice, targetPrice)
    }

    private fun onStartSave() {
        alertSaveButton.isEnabled = false // TODO Disable inputs fields as well
    }

    private fun onEndSave() {
        exitActivity()
    }

    private fun bindFieldValidators() {
        alertSourceEditText.registerInputValidator({ s -> (s.replace(".", "").length >= 1) }, "Please enter amount")
        alertTargetEditText.registerInputValidator({ s -> ((s.replace(".", "").length >= 1) && (s.toDouble() > 0.0)) }, "Please enter amount")
        alertSourceEditText.afterTextChanged { checkSaveButton() }
        alertTargetEditText.afterTextChanged { checkSaveButton() }
    }

    private fun checkSaveButton() {
        alertSaveButton.isEnabled = alertSourceEditText.error == null && alertTargetEditText.error == null && alertSymbolText.selectedItem != alertReferenceText.selectedItem
    }

    private fun exitActivity() {
        val intent = Intent(this, AlertListActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }
}
