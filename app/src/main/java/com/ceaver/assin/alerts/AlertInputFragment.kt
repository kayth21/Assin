package com.ceaver.assin.alerts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.ceaver.assin.R
import com.ceaver.assin.common.SpinnerSelectionListener
import com.ceaver.assin.extensions.afterTextChanged
import com.ceaver.assin.extensions.format
import com.ceaver.assin.extensions.registerInputValidator
import com.ceaver.assin.markets.Title
import kotlinx.android.synthetic.main.activity_alert_input.*
import kotlinx.coroutines.launch

class AlertInputFragment : Fragment() {

    private lateinit var viewModel: AlertViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val args = AlertInputFragmentArgs.fromBundle(requireArguments())
        viewModel = viewModels<AlertViewModel> { AlertViewModel.Factory(args.alert) }.value
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        bindSymbol()
        bindReference()
        bindAlert()
        return inflater.inflate(R.layout.activity_alert_input, container, false)
    }

    override fun onStart() {
        super.onStart()

        bindActions()
        observeStatus()
        bindViewLogic()
        bindFieldValidators()
    }

    private fun bindActions() {
        alertSaveButton.setOnClickListener { onSaveClick() }
    }

    private fun bindSymbol() {
        viewModel.symbol.observe(viewLifecycleOwner, Observer {
            val adapter = ArrayAdapter<Title>(requireContext(), android.R.layout.simple_spinner_item)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            alertSymbolText.setAdapter(adapter)
            adapter.addAll(it!!); updateSpinnerFields()
        })
    }

    private fun bindReference() {
        viewModel.reference.observe(viewLifecycleOwner, Observer {
            val adapter = ArrayAdapter<Title>(requireContext(), android.R.layout.simple_spinner_item)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            alertReferenceText.setAdapter(adapter)
            adapter.addAll(it!!); updateSpinnerFields()
        })
    }

    private fun bindAlert() {
        viewModel.alert.observe(viewLifecycleOwner, Observer {
            val adapter = ArrayAdapter<Title>(requireContext(), android.R.layout.simple_spinner_item)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            alertReferenceText.setAdapter(adapter)
            bindFields(it!!); alertSaveButton.isEnabled = true
        })
    }

    private fun bindViewLogic() {
        fun updatePrice() {
            lifecycleScope.launch {
                if (viewModel.isNew() && alertSymbolText.selectedItem != null && alertReferenceText.selectedItem != null) {
                    val symbol = alertSymbolText.selectedItem as Title
                    val reference = alertReferenceText.selectedItem as Title
                    viewModel.lookupPrice(symbol, reference) {
                        alertSourceEditText.setText(it.first.format(reference.symbol))
                        alertTargetEditText.setText(it.second.format(reference.symbol))
                    }
                }
            }
        }

        fun updateUnit() {
            val symbol = alertReferenceText.selectedItem as Title
            startUnitTextView.text = symbol.symbol
            targetUnitTextView.text = symbol.symbol
        }
        alertSymbolText.onItemSelectedListener = SpinnerSelectionListener { updatePrice(); checkSaveButton() }
        alertReferenceText.onItemSelectedListener = SpinnerSelectionListener { updateUnit(); updatePrice(); checkSaveButton() }
    }

    private fun bindFields(alert: Alert) {
        alertSourceEditText.setText(alert.source.toPlainString())
        alertTargetEditText.setText(alert.target.toPlainString())

        updateSpinnerFields()

        alertSymbolText.isEnabled = alert.isNew()
        alertReferenceText.isEnabled = alert.isNew()
    }

    private fun updateSpinnerFields() {
        if (viewModel.alert.value != null && viewModel.symbol.value != null && viewModel.reference.value != null) {
            alertSymbolText.setSelection(viewModel.symbol.value!!.indexOf(viewModel.alert.value!!.symbol))
            alertReferenceText.setSelection(viewModel.reference.value!!.indexOf(viewModel.alert.value!!.reference))
        }
    }

    private fun observeStatus() {
        viewModel.status.observe(this, Observer {
            when (it) {
                AlertViewModel.AlertInputStatus.START_SAVE -> onStartSave()
                AlertViewModel.AlertInputStatus.END_SAVE -> onEndSave()
                null -> throw IllegalStateException()
            }
        })
    }

    private fun onSaveClick() {
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
