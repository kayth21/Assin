package com.ceaver.assin.action.input

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.ceaver.assin.action.Action
import com.ceaver.assin.action.ActionType
import com.ceaver.assin.extensions.afterTextChanged
import com.ceaver.assin.extensions.registerInputValidator
import com.ceaver.assin.markets.Title
import com.ceaver.assin.util.CalendarHelper
import kotlinx.android.synthetic.main.action_input_fragment.*
import java.time.LocalDate

class ActionInputFragment() : DialogFragment() {

    companion object {
        val ACTION_INPUT_FRAGMENT_TAG = "com.ceaver.assin.action.input.ActionInputFragment.Tag"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(com.ceaver.assin.R.layout.action_input_fragment, container, false)
    }

    override fun onStart() {
        super.onStart()

        val actionId = lookupTradeId()
        val symbol = lookupSymbol()
        val actionType = lookupActionType()
        val viewModel = lookupViewModel(actionId, symbol, actionType)

        prepareView(actionType)
        bindActions(viewModel)
        observeSymbols(viewModel)
        observeTrade(viewModel)
        observeStatus(viewModel)
        observeDataReady(viewModel)
    }

    private fun lookupTradeId(): Long? = requireArguments().getLong(Action.ACTION_ID).takeUnless { it == 0L }
    private fun lookupSymbol(): String? = requireArguments().getString(Action.SYMBOL)
    private fun lookupActionType(): ActionType = ActionType.valueOf(requireArguments().getString(Action.ACTION_TYPE)!!)

    private fun lookupViewModel(actionId: Long?, symbol: String?, actionType: ActionType): ActionInputViewModel {
        val viewModel by viewModels<ActionInputViewModel>()
        viewModel.initTrade(actionId, symbol, actionType)
        return viewModel
    }

    private fun prepareView(actionType: ActionType) {
        actionInputFragmentBuySymbolSpinner.isEnabled = false // not possible in XML
        actionInputFragmentSellSymbolSpinner.isEnabled = false // not possible in XML
        when (actionType) {
            ActionType.TRADE -> {
                actionInputFragmentTradeTypeTextView.text = "Trade"
                actionInputFragmentTradeTypeImageView.setImageResource(com.ceaver.assin.R.drawable.trade)
            }
            ActionType.DEPOSIT -> {
                actionInputFragmentTradeTypeTextView.text = "Deposit"
                actionInputFragmentTradeTypeImageView.setImageResource(com.ceaver.assin.R.drawable.deposit)

                actionInputFragment.removeView(actionInputFragmentSellTradeLabel)
                actionInputFragment.removeView(actionInputFragmentSellAmountTextView)
                actionInputFragment.removeView(actionInputFragmentSellSymbolSpinner)
                actionInputFragment.removeView(actionInputFragmentBuyTradeLabel)

                val constraintSet = ConstraintSet()
                constraintSet.clone(actionInputFragment)
                constraintSet.connect(actionInputFragmentCommentLabel.id, ConstraintSet.TOP, actionInputFragmentBuyAmountTextView.id, ConstraintSet.BOTTOM, 20)
                constraintSet.connect(actionInputFragmentBuyAmountTextView.id, ConstraintSet.TOP, actionInputFragmentTradeTypeImageView.id, ConstraintSet.BOTTOM, 20)
                constraintSet.applyTo(actionInputFragment)
            }
            ActionType.WITHDRAW -> {
                actionInputFragmentTradeTypeTextView.text = "Withdraw"
                actionInputFragmentTradeTypeImageView.setImageResource(com.ceaver.assin.R.drawable.withdraw)

                actionInputFragment.removeView(actionInputFragmentBuyTradeLabel)
                actionInputFragment.removeView(actionInputFragmentBuyAmountTextView)
                actionInputFragment.removeView(actionInputFragmentBuySymbolSpinner)
                actionInputFragment.removeView(actionInputFragmentSellTradeLabel)

                val constraintSet = ConstraintSet()
                constraintSet.clone(actionInputFragment)
                constraintSet.connect(actionInputFragmentSellAmountTextView.id, ConstraintSet.TOP, actionInputFragmentTradeTypeImageView.id, ConstraintSet.BOTTOM, 20)
                constraintSet.applyTo(actionInputFragment)
            }
        }
        actionInputFragmentTradeDateTextView.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                val actionDate = CalendarHelper.convertDate(actionInputFragmentTradeDateTextView.text.toString())
                val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth -> actionInputFragmentTradeDateTextView.setText(CalendarHelper.convertDate(LocalDate.of(year, monthOfYear + 1, dayOfMonth))); actionInputFragmentTradeDateTextView.clearFocus() }
                val datePickerDialog = DatePickerDialog(this@ActionInputFragment.requireContext(), dateSetListener, actionDate.year, actionDate.monthValue - 1, actionDate.dayOfMonth)
                datePickerDialog.show()
            }
        }
        actionInputFragmentTradeDateTextView.keyListener = null // hack to disable user input
    }

    private fun bindActions(viewModel: ActionInputViewModel) {
        actionInputFragmentSaveButton.setOnClickListener { onSaveClick(viewModel) }
    }

    private fun onSaveClick(viewModel: ActionInputViewModel) {
        val comment = actionInputFragmentCommentTextView.text.toString().ifEmpty { null }
        val actionDate = CalendarHelper.convertDate(actionInputFragmentTradeDateTextView.text.toString())
        when (lookupActionType()) {
            ActionType.TRADE -> {
                val buySymbol = actionInputFragmentBuySymbolSpinner.selectedItem as Title
                val buyAmount = actionInputFragmentBuyAmountTextView.text.toString().toBigDecimal()
                val sellSymbol = actionInputFragmentSellSymbolSpinner.selectedItem as Title
                val sellAmount = actionInputFragmentSellAmountTextView.text.toString().toBigDecimal()
                viewModel.onSaveTradeClick(buySymbol, buyAmount, sellSymbol, sellAmount, actionDate, comment)
            }
            ActionType.DEPOSIT -> {
                val buySymbol = actionInputFragmentBuySymbolSpinner.selectedItem as Title
                val buyAmount = actionInputFragmentBuyAmountTextView.text.toString().toBigDecimal()
                viewModel.onDepositClick(buySymbol, buyAmount, actionDate, comment)
            }
            ActionType.WITHDRAW -> {
                val sellSymbol = actionInputFragmentSellSymbolSpinner.selectedItem as Title
                val sellAmount = actionInputFragmentSellAmountTextView.text.toString().toBigDecimal()
                viewModel.onWithdrawClick(sellSymbol, sellAmount, actionDate, comment)
            }
        }
    }

    private fun observeSymbols(viewModel: ActionInputViewModel) {
        val adapter = ArrayAdapter<Title>(this.requireContext(), android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        actionInputFragmentBuySymbolSpinner.adapter = adapter
        actionInputFragmentSellSymbolSpinner.adapter = adapter
        viewModel.symbols.observe(this, Observer { adapter.addAll(it!!) })
    }

    private fun observeTrade(viewModel: ActionInputViewModel) {
        viewModel.action.observe(this, Observer {
            publishFields(it!!);
        })
    }

    private fun observeDataReady(viewModel: ActionInputViewModel) {
        viewModel.dataReady.observe(this, Observer {
            updateSpinnerFields(viewModel, it!!.first)
            registerInputValidation()
            enableInput(true)
            viewModel.dataReady.removeObservers(this)
        })
    }

    private fun updateSpinnerFields(viewModel: ActionInputViewModel, action: Action) {
        if (action.buyTitle != null) {
            actionInputFragmentBuySymbolSpinner.setSelection(viewModel.symbols.value!!.indexOf(action.buyTitle!!))
        }
        if (action.sellTitle != null) {
            actionInputFragmentSellSymbolSpinner.setSelection(viewModel.symbols.value!!.indexOf(action.sellTitle!!))
        }
    }

    private fun publishFields(action: Action) {
        actionInputFragmentTradeDateTextView.setText(CalendarHelper.convertDate(action.actionDate))
        actionInputFragmentCommentTextView.setText(action.comment.orEmpty())
        when (lookupActionType()) {
            ActionType.TRADE -> {
                actionInputFragmentBuyAmountTextView.setText(if (action.buyAmount != null) action.buyAmount.toString() else "")
                actionInputFragmentSellAmountTextView.setText(if (action.sellAmount != null) action.sellAmount.toString() else "")
            }
            ActionType.DEPOSIT -> {
                actionInputFragmentBuyAmountTextView.setText(if (action.buyAmount != null) action.buyAmount.toString() else "")
            }
            ActionType.WITHDRAW -> {
                actionInputFragmentSellAmountTextView.setText(if (action.sellAmount != null) action.sellAmount.toString() else "")
            }
        }
    }

    private fun observeStatus(viewModel: ActionInputViewModel) {
        viewModel.status.observe(this, Observer {
            when (it) {
                ActionInputViewModel.ActionInputStatus.START_SAVE -> onStartSave()
                ActionInputViewModel.ActionInputStatus.END_SAVE -> onEndSave()
                null -> throw IllegalStateException()
            }
        })
    }

    private fun onStartSave() {
        enableInput(false)
    }

    private fun enableInput(enable: Boolean) {
        actionInputFragmentSaveButton.isEnabled = enable && checkSaveButton()
        actionInputFragmentCommentTextView.isEnabled = enable
        actionInputFragmentTradeDateTextView.isEnabled = enable
        when (lookupActionType()) {
            ActionType.TRADE -> {
                actionInputFragmentBuyAmountTextView.isEnabled = enable
                actionInputFragmentBuySymbolSpinner.isEnabled = enable
                actionInputFragmentSellAmountTextView.isEnabled = enable
                actionInputFragmentSellSymbolSpinner.isEnabled = enable
            }
            ActionType.DEPOSIT -> {
                actionInputFragmentBuyAmountTextView.isEnabled = enable
                actionInputFragmentBuySymbolSpinner.isEnabled = enable
            }
            ActionType.WITHDRAW -> {
                actionInputFragmentSellAmountTextView.isEnabled = enable
            }
        }
    }

    private fun onEndSave() {
        dismiss()
    }

    private fun registerInputValidation() {
        when (lookupActionType()) {
            ActionType.TRADE -> {
                actionInputFragmentBuyAmountTextView.registerInputValidator({ s -> s.isNotEmpty() }, "Please enter amount")
                actionInputFragmentSellAmountTextView.registerInputValidator({ s -> s.isNotEmpty() }, "Please enter amount")
                actionInputFragmentBuyAmountTextView.afterTextChanged { actionInputFragmentSaveButton.isEnabled = checkSaveButton() }
                actionInputFragmentSellAmountTextView.afterTextChanged { actionInputFragmentSaveButton.isEnabled = checkSaveButton() }
            }
            ActionType.DEPOSIT -> {
                actionInputFragmentBuyAmountTextView.registerInputValidator({ s -> (s.isNotEmpty()) }, "Please enter amount")
                actionInputFragmentBuyAmountTextView.afterTextChanged { actionInputFragmentSaveButton.isEnabled = checkSaveButton() }
            }
            ActionType.WITHDRAW -> {
                actionInputFragmentSellAmountTextView.registerInputValidator({ s -> (s.isNotEmpty()) }, "Please enter amount")
                actionInputFragmentSellAmountTextView.afterTextChanged { actionInputFragmentSaveButton.isEnabled = checkSaveButton() }
            }
        }
    }

    private fun checkSaveButton(): Boolean {
        return actionInputFragmentBuyAmountTextView.error == null && actionInputFragmentSellAmountTextView.error == null
    }
}
