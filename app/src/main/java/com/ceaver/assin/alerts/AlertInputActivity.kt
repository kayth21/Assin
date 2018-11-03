package com.ceaver.assin.alerts

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.ArrayAdapter
import com.ceaver.assin.IntentKeys
import com.ceaver.assin.R
import com.ceaver.assin.assets.Symbol
import com.ceaver.assin.databinding.ActivityAlertInputBinding
import com.ceaver.assin.extensions.validateFields
import kotlinx.android.synthetic.main.activity_alert_input.*

class AlertInputActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = publishView()
        val alertId = lookupAlertId()
        val viewModel = lookupViewModel().init(alertId)

        bindActions(viewModel, binding)
        bindSymbol(viewModel)
        bindReference(viewModel)
        bindAlert(viewModel, binding)
        observeStatus(viewModel)
    }

    private fun publishView(): ActivityAlertInputBinding = DataBindingUtil.setContentView(this, R.layout.activity_alert_input)

    private fun lookupAlertId() = intent.getLongExtra(IntentKeys.ALERT_ID, 0)

    private fun lookupViewModel(): AlertViewModel = ViewModelProviders.of(this).get(AlertViewModel::class.java)

    private fun bindActions(viewModel: AlertViewModel, binding: ActivityAlertInputBinding) {
        binding.saveClickHandler = viewModel
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

    private fun bindAlert(viewModel: AlertViewModel, binding: ActivityAlertInputBinding) {
        viewModel.alert.observe(this, Observer { onAlertUpdate(binding, it) })
    }

    private fun onAlertUpdate(binding: ActivityAlertInputBinding, alert: Alert?) {
        binding.alert = alert
        validateFields()
        alertSaveButton.isEnabled = true
    }

    private fun observeStatus(viewModel: AlertViewModel) {
        viewModel.status.observe(this, Observer {
            when (it) {
                AlertViewModel.AlertInputStatus.START_SAVE -> onStartSave()
                AlertViewModel.AlertInputStatus.END_SAVE -> onEndSave()
            }
        })
    }

    private fun onStartSave() {
        alertSaveButton.isEnabled = false // TODO Disable inputs fields as well
    }

    private fun onEndSave() {
        exitActivity()
    }

    private fun validateFields() {
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
