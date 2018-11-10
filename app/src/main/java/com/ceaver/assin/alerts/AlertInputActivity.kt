package com.ceaver.assin.alerts

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.ArrayAdapter
import com.ceaver.assin.IntentKeys
import com.ceaver.assin.R
import com.ceaver.assin.assets.Symbol
import com.ceaver.assin.common.SpinnerSelectionListener
import com.ceaver.assin.extensions.validateFields
import kotlinx.android.synthetic.main.activity_alert_input.*

class AlertInputActivity : AppCompatActivity() {

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

    private fun lookupAlertId() = intent.getLongExtra(IntentKeys.ALERT_ID, 0)

    private fun lookupViewModel(): AlertViewModel = ViewModelProviders.of(this).get(AlertViewModel::class.java)

    private fun bindActions(viewModel: AlertViewModel) {
        alertSaveButton.setOnClickListener { onSaveClick() }
    }

    private fun bindSymbol(viewModel: AlertViewModel) {
        val adapter = ArrayAdapter<Symbol>(this, android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        alertSymbolText.setAdapter(adapter)
        viewModel.symbol.observe(this, Observer { adapter.addAll(it) })
    }

    private fun bindReference(viewModel: AlertViewModel) {
        val adapter = ArrayAdapter<Symbol>(this, android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        alertReferenceText.setAdapter(adapter)
        viewModel.reference.observe(this, Observer { adapter.addAll(it) })
    }

    private fun bindAlert(viewModel: AlertViewModel) {
        viewModel.alert.observe(this, Observer { bindFields(it); alertSaveButton.isEnabled = true })
    }

    private fun bindViewLogic(viewModel: AlertViewModel) {
        fun updatePrice() {
            if (viewModel.isNew()) {
                val symbol = Symbol.valueOf(alertSymbolText.selectedItem.toString())
                val reference = Symbol.valueOf(alertReferenceText.selectedItem.toString())
                val price: Pair<Double, Double> = lookupViewModel().lookupPrice(symbol, reference);
                alertSourceEditText.setText(price.first.toString()); alertTargetEditText.setText((price.second.toString()))
            }
        }

        fun updateUnit() {
            if (viewModel.isNew()) {
                val symbol = Symbol.valueOf(alertReferenceText.selectedItem.toString())
                startUnitTextView.text = symbol.name
                targetUnitTextView.text = symbol.name
            }
        }
        alertSymbolText.onItemSelectedListener = SpinnerSelectionListener() { updatePrice() }
        alertReferenceText.onItemSelectedListener = SpinnerSelectionListener() { updateUnit(); updatePrice() }
    }

    private fun bindFields(alert: Alert?) {
        if (alert != null) {
            alertSymbolText.setSelection(alert.symbol.ordinal)
            alertReferenceText.setSelection(alert.reference.ordinal)
            alertSourceEditText.setText(alert.source.toString())
            alertTargetEditText.setText(alert.target.toString())

            alertSymbolText.isEnabled = alert.isNew()
            alertReferenceText.isEnabled = alert.isNew()
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
        val symbol = Symbol.valueOf(alertSymbolText.selectedItem.toString())
        val reference = Symbol.valueOf(alertReferenceText.selectedItem.toString())
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
        alertSourceEditText.validateFields({ s -> (s.length >= 1) }, "Please enter amount")
        alertTargetEditText.validateFields({ s -> (s.length >= 1) }, "Please enter amount")
    }

    private fun exitActivity() {
        val intent = Intent(this, AlertListActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }
}
