package com.ceaver.assin.alerts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.ceaver.assin.common.SpinnerSelectionListener
import com.ceaver.assin.extensions.afterTextChanged
import com.ceaver.assin.extensions.format
import com.ceaver.assin.extensions.registerInputValidator
import com.ceaver.assin.markets.Title
import kotlinx.android.synthetic.main.activity_alert_input.*

class AlertInputFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(com.ceaver.assin.R.layout.activity_alert_input, container, false)
    }

    override fun onStart() {
        super.onStart()

        val alert = lookupAlert()
        val viewModel = lookupViewModel().init(alert)

        bindActions(viewModel)
        bindSymbol(viewModel)
        bindReference(viewModel)
        bindAlert(viewModel)
        observeStatus(viewModel)
        bindViewLogic(viewModel)
        bindFieldValidators()
    }


    private fun lookupAlert() = AlertInputFragmentArgs.fromBundle(requireArguments()).alert

    private fun lookupViewModel(): AlertViewModel {
        val viewModel by viewModels<AlertViewModel>()
        return viewModel
    }

    private fun bindActions(viewModel: AlertViewModel) {
        alertSaveButton.setOnClickListener { onSaveClick(viewModel) }
    }

    private fun bindSymbol(viewModel: AlertViewModel) {
        val adapter = ArrayAdapter<Title>(requireContext(), android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        alertSymbolText.setAdapter(adapter)
        viewModel.symbol.observe(this, Observer { adapter.addAll(it!!); updateSpinnerFields(viewModel) })
    }

    private fun bindReference(viewModel: AlertViewModel) {
        val adapter = ArrayAdapter<Title>(requireContext(), android.R.layout.simple_spinner_item)
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
        alertSourceEditText.setText(alert.source.toPlainString())
        alertTargetEditText.setText(alert.target.toPlainString())

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
                null -> throw IllegalStateException()
            }
        })
    }

    private fun onSaveClick(viewModel: AlertViewModel) {
        val symbol = alertSymbolText.selectedItem as Title
        val reference = alertReferenceText.selectedItem as Title
        val startPrice = alertSourceEditText.text.toString().toBigDecimal()
        val targetPrice = alertTargetEditText.text.toString().toBigDecimal()
        viewModel.onSaveClick(symbol, reference, startPrice, targetPrice)
    }

    private fun onStartSave() {
        alertSaveButton.isEnabled = false // TODO Disable inputs fields as well
    }

    private fun onEndSave() {
        findNavController().navigateUp()
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
}
