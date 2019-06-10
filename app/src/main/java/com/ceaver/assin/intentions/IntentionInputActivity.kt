package com.ceaver.assin.intentions

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.ArrayAdapter
import com.ceaver.assin.IntentKeys
import com.ceaver.assin.R
import com.ceaver.assin.StartActivity
import com.ceaver.assin.extensions.afterTextChanged
import com.ceaver.assin.extensions.registerInputValidator
import com.ceaver.assin.markets.Title
import kotlinx.android.synthetic.main.activity_intention_input.*
import java.util.*

class IntentionInputActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        publishView()

        val viewModel = lookupViewModel()

        modifyView()
        bindActions(viewModel)
        observeStatus(viewModel)
        observeDataReady(viewModel)
    }

    private fun publishView() = setContentView(R.layout.activity_intention_input)

    private fun lookupViewModel(): IntentionInputViewModel {
        return ViewModelProviders.of(this).get(IntentionInputViewModel::class.java).init(lookupIntentionId())
    }

    private fun lookupIntentionId(): Optional<Long> {
        val intentionId = intent.getLongExtra(IntentKeys.INTENTION_ID, 0L)
        return if (intentionId == 0L) Optional.empty() else Optional.of(intentionId)
    }

    private fun modifyView() {
        intentionInputTitleSpinner.isEnabled = false // not possible in XML
        intentionInputReferenceTitleSpinner.isEnabled = false // not possible in XML
    }

    private fun bindActions(viewModel: IntentionInputViewModel) {
        intentionInputSaveButton.setOnClickListener { onSaveClick(viewModel) }
    }

    private fun onSaveClick(viewModel: IntentionInputViewModel) {
        val type = if (intentionInputBuyRadio.isChecked) IntentionType.BUY else IntentionType.SELL
        val title = intentionInputTitleSpinner.selectedItem as Title
        val amount = intentionInputAmountEditText.text.toString().toDouble()
        val referenceTitle = intentionInputReferenceTitleSpinner.selectedItem as Title
        val referencePrice = intentionInputReferencePriceEditText.text.toString().toDouble()
        val comment = intentionInputCommentEditText.text.toString()
        viewModel.onSaveClick(type, title, amount, referenceTitle, referencePrice, comment)
    }

    private fun observeDataReady(viewModel: IntentionInputViewModel) {
        viewModel.dataReady.observe(this, Observer {
            val intention = it!!.first
            val titles = it.second

            val adapter = ArrayAdapter<Title>(this, android.R.layout.simple_spinner_item)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            intentionInputTitleSpinner.adapter = adapter
            intentionInputReferenceTitleSpinner.adapter = adapter
            adapter.addAll(titles)

            if (IntentionType.BUY == intention.type) intentionInputBuyRadio.isChecked = true else intentionInputSellRadio.isChecked = true
            intentionInputTitleSpinner.setSelection(viewModel.symbols.value!!.indexOf(intention.title))
            intentionInputAmountEditText.setText(intention.amount.toString())
            intentionInputReferenceTitleSpinner.setSelection(viewModel.symbols.value!!.indexOf(intention.referenceTitle))
            intentionInputReferencePriceEditText.setText(intention.referencePrice.toString())
            intentionInputCommentEditText.setText(intention.comment)

            registerInputValidation()
            enableInput(true)
            viewModel.dataReady.removeObservers(this)
        })
    }

    private fun observeStatus(viewModel: IntentionInputViewModel) {
        viewModel.status.observe(this, Observer {
            when (it) {
                IntentionInputViewModel.IntentionInputStatus.START_SAVE -> onStartSave()
                IntentionInputViewModel.IntentionInputStatus.END_SAVE -> onEndSave()
            }
        })
    }

    fun onStartSave() {
        enableInput(false)
    }

    fun onEndSave() {
        exitActivity()
    }

    private fun exitActivity() {
        val intent = Intent(this, StartActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }

    fun enableInput(enable: Boolean) {
        intentionInputSaveButton.isEnabled = enable && checkSaveButton()
        intentionInputTitleSpinner.isEnabled = enable
        intentionInputAmountEditText.isEnabled = enable
        intentionInputReferenceTitleSpinner.isEnabled = enable
        intentionInputReferencePriceEditText.isEnabled = enable
        intentionInputCommentEditText.isEnabled = enable
    }

    private fun registerInputValidation() {
        intentionInputAmountEditText.registerInputValidator({ s -> s.isNotEmpty() }, "Please enter amount")
        intentionInputReferencePriceEditText.registerInputValidator({ s -> s.isNotEmpty() }, "Please enter amount")
        intentionInputAmountEditText.afterTextChanged { intentionInputSaveButton.isEnabled = checkSaveButton() }
        intentionInputReferencePriceEditText.afterTextChanged { intentionInputSaveButton.isEnabled = checkSaveButton() }
    }

    private fun checkSaveButton(): Boolean {
        return intentionInputAmountEditText.error == null && intentionInputReferencePriceEditText.error == null
    }
}
