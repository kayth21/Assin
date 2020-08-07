package com.ceaver.assin.intentions.input

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.ceaver.assin.extensions.afterTextChanged
import com.ceaver.assin.extensions.registerInputValidator
import com.ceaver.assin.intentions.Intention
import com.ceaver.assin.intentions.IntentionType
import com.ceaver.assin.markets.Title
import kotlinx.android.synthetic.main.intention_input_fragment.*
import java.math.BigDecimal

class IntentionInputFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(com.ceaver.assin.R.layout.intention_input_fragment, container, false)
    }

    override fun onStart() {
        super.onStart()

        val intention = lookupIntention()
        val title = lookupTitle()
        val amount = lookupAmount()
        val viewModel = lookupViewModel(intention, title, amount)

        prepareView()
        bindActions(viewModel)
        observeStatus(viewModel)
        observeDataReady(viewModel)
    }

    private fun lookupIntention(): Intention? = IntentionInputFragmentArgs.fromBundle(requireArguments()).intention
    private fun lookupTitle(): Title? = IntentionInputFragmentArgs.fromBundle(requireArguments()).title
    private fun lookupAmount(): BigDecimal? = IntentionInputFragmentArgs.fromBundle(requireArguments()).amount

    private fun lookupViewModel(intention: Intention?, title: Title?, amount: BigDecimal?): IntentionInputViewModel {
        val viewModel by viewModels<IntentionInputViewModel>()
        viewModel.init(intention, title, amount)
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
        val amount = intentionInputFragmentTitleAmountTextView.text.toString().toBigDecimalOrNull()
        val referenceTitle = intentionInputFragmentReferenceSymbolSpinner.selectedItem as Title
        val referencePrice = intentionInputFragmentReferencePriceTextView.text.toString().toBigDecimal()
        val comment = intentionInputFragmentCommentTextView.text.toString()
        viewModel.onSaveClick(type, title, amount, referenceTitle, referencePrice, comment.ifEmpty { null })
    }

    private fun observeDataReady(viewModel: IntentionInputViewModel) {
        viewModel.dataReady.observe(this, Observer {
            val intention = it!!.first
            val titles = it.second

            val titleAdapter = ArrayAdapter<Title>(this.requireContext(), android.R.layout.simple_spinner_item)
            titleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            intentionInputFragmentTitleSymbolSpinner.adapter = titleAdapter
            titleAdapter.addAll(titles)

            val referenceTitleAdapter = ArrayAdapter<Title>(this.requireContext(), android.R.layout.simple_spinner_item)
            referenceTitleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            intentionInputFragmentReferenceSymbolSpinner.adapter = referenceTitleAdapter
            referenceTitleAdapter.addAll(titles.filter { setOf("USD", "BTC", "ETH").contains(it.symbol) })

            if (IntentionType.BUY == intention.type) intentionInputFragmentBuyRadio.isChecked = true else intentionInputFragmentSellRadio.isChecked = true
            intentionInputFragmentTitleSymbolSpinner.setSelection(viewModel.symbols.value!!.indexOf(intention.title))
            intentionInputFragmentTitleAmountTextView.setText(intention.amountAsString())
            intentionInputFragmentReferenceSymbolSpinner.setSelection(viewModel.symbols.value!!.indexOf(intention.referenceTitle))
            intentionInputFragmentReferencePriceTextView.setText(intention.referencePrice.toPlainString())
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
                null -> throw IllegalStateException()
            }
        })
    }

    private fun onStartSave() {
        enableInput(false)
    }

    private fun onEndSave() {
        findNavController().navigateUp()
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
