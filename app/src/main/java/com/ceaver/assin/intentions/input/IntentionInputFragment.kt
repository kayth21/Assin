package com.ceaver.assin.intentions.input

import androidx.lifecycle.Observer
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.viewModels
import com.ceaver.assin.assets.overview.AssetOverview
import com.ceaver.assin.assets.overview.AssetOverviewViewModel
import com.ceaver.assin.extensions.afterTextChanged
import com.ceaver.assin.extensions.registerInputValidator
import com.ceaver.assin.intentions.IntentionType
import com.ceaver.assin.markets.Title
import kotlinx.android.synthetic.main.intention_input_fragment.*
import java.math.BigDecimal

class IntentionInputFragment : DialogFragment() {

    companion object {
        val INTENTION_INPUT_FRAGMENT_TAG = "com.ceaver.assin.trades.input.TradeInputFragment.Tag"
        val INTENTION_ID = "com.ceaver.assin.intentions.input.IntentionInputFragment.intentionId"
        val INTENTION_SYMBOL = "com.ceaver.assin.intentions.input.IntentionInputFragment.intentionSymbol"
        val INTENTION_AMOUNT = "com.ceaver.assin.intentions.input.IntentionInputFragment.intentionAmount"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(com.ceaver.assin.R.layout.intention_input_fragment, container, false)
    }

    override fun onStart() {
        super.onStart()

        val intentionId = lookupIntentionId()
        val symbol = lookupSymbol()
        val amount = lookupAmount()
        val viewModel = lookupViewModel(intentionId, symbol, amount)

        prepareView()
        bindActions(viewModel)
        observeStatus(viewModel)
        observeDataReady(viewModel)
    }

    private fun lookupIntentionId(): Long? = requireArguments().getLong(INTENTION_ID).takeUnless { it == 0L }
    private fun lookupSymbol(): String? = requireArguments().getString(INTENTION_SYMBOL)
    private fun lookupAmount(): Double? = requireArguments().getString(INTENTION_AMOUNT)?.toDouble()

    private fun lookupViewModel(intentionId: Long?, symbol: String?, amount: Double?): IntentionInputViewModel {
        val viewModel by viewModels<IntentionInputViewModel>()
        viewModel.init(intentionId, symbol, amount)
        return viewModel
    }

    private fun prepareView() {
        intentionInputFragmentTitleSymbolSpinner.isEnabled = false // not possible in XML
        intentionInputFragmentReferenceSymbolSpinner.isEnabled = false // not possible in XML
    }

    private fun bindActions(viewModel: IntentionInputViewModel) {
        intentionInputFragmentSaveButton.setOnClickListener { onSaveClick(viewModel) }
    }

    private fun onSaveClick(viewModel: IntentionInputViewModel) {
        val type = if (intentionInputFragmentBuyRadio.isChecked) IntentionType.BUY else IntentionType.SELL
        val title = intentionInputFragmentTitleSymbolSpinner.selectedItem as Title
        val amount = intentionInputFragmentTitleAmountTextView.text.toString().toDoubleOrNull()
        val referenceTitle = intentionInputFragmentReferenceSymbolSpinner.selectedItem as Title
        val referencePrice = intentionInputFragmentReferencePriceTextView.text.toString().toDouble()
        val comment = intentionInputFragmentCommentTextView.text.toString()
        viewModel.onSaveClick(type, title, amount, referenceTitle, referencePrice, comment.ifEmpty { null })
    }

    private fun observeDataReady(viewModel: IntentionInputViewModel) {
        viewModel.dataReady.observe(this, Observer {
            val intention = it!!.first
            val titles = it.second

            val titleAdapter = ArrayAdapter<Title>(this.context!!, android.R.layout.simple_spinner_item)
            titleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            intentionInputFragmentTitleSymbolSpinner.adapter = titleAdapter
            titleAdapter.addAll(titles)

            val referenceTitleAdapter = ArrayAdapter<Title>(this.context!!, android.R.layout.simple_spinner_item)
            referenceTitleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            intentionInputFragmentReferenceSymbolSpinner.adapter = referenceTitleAdapter
            referenceTitleAdapter.addAll(titles.filter { setOf("USD", "BTC", "ETH").contains(it.symbol) })

            if (IntentionType.BUY == intention.type) intentionInputFragmentBuyRadio.isChecked = true else intentionInputFragmentSellRadio.isChecked = true
            intentionInputFragmentTitleSymbolSpinner.setSelection(viewModel.symbols.value!!.indexOf(intention.title))
            intentionInputFragmentTitleAmountTextView.setText(intention.amountAsString())
            intentionInputFragmentReferenceSymbolSpinner.setSelection(viewModel.symbols.value!!.indexOf(intention.referenceTitle))
            intentionInputFragmentReferencePriceTextView.setText(BigDecimal.valueOf(intention.referencePrice).toPlainString())
            intentionInputFragmentCommentTextView.setText(intention.comment.orEmpty())

            registerInputValidation()
            enableInput(true)
            viewModel.dataReady.removeObservers(this)
        })
    }

    private fun calculateValue(): String {
        val amount = intentionInputFragmentTitleAmountTextView.text.toString().toDoubleOrNull()
        val referencePrice = intentionInputFragmentReferencePriceTextView.text.toString().toDoubleOrNull()
        if (amount == null || referencePrice == null)
            return ""
        return amount.times(referencePrice).toString()
    }

    private fun observeStatus(viewModel: IntentionInputViewModel) {
        viewModel.status.observe(this, Observer {
            when (it) {
                IntentionInputViewModel.IntentionInputStatus.START_SAVE -> onStartSave()
                IntentionInputViewModel.IntentionInputStatus.END_SAVE -> onEndSave()
            }
        })
    }

    private fun onStartSave() {
        enableInput(false)
    }

    private fun onEndSave() {
        dismiss()
    }

    private fun enableInput(enable: Boolean) {
        intentionInputFragmentSaveButton.isEnabled = enable && checkSaveButton()
        intentionInputFragmentTitleSymbolSpinner.isEnabled = enable
        intentionInputFragmentTitleAmountTextView.isEnabled = enable
        intentionInputFragmentReferenceSymbolSpinner.isEnabled = enable
        intentionInputFragmentReferencePriceTextView.isEnabled = enable
        intentionInputFragmentCommentTextView.isEnabled = enable
    }

    private fun registerInputValidation() {
        intentionInputFragmentReferencePriceTextView.registerInputValidator({ s -> s.isNotEmpty() }, "Please enter amount")
        intentionInputFragmentTitleAmountTextView.afterTextChanged { intentionInputFragmentSaveButton.isEnabled = checkSaveButton() }
        intentionInputFragmentReferencePriceTextView.afterTextChanged { intentionInputFragmentSaveButton.isEnabled = checkSaveButton() }
    }

    private fun checkSaveButton(): Boolean {
        return intentionInputFragmentTitleAmountTextView.error == null && intentionInputFragmentReferencePriceTextView.error == null
    }
}
