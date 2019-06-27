package com.ceaver.assin.trades.input

import android.app.DatePickerDialog
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.constraint.ConstraintSet
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.ceaver.assin.extensions.afterTextChanged
import com.ceaver.assin.extensions.registerInputValidator
import com.ceaver.assin.markets.Title
import com.ceaver.assin.trades.Trade
import com.ceaver.assin.trades.TradeType
import com.ceaver.assin.util.CalendarHelper
import kotlinx.android.synthetic.main.trade_input_fragment.*
import java.time.LocalDate

class TradeInputFragment() : DialogFragment() {

    companion object {
        val TRADE_INPUT_FRAGMENT_TAG = "com.ceaver.assin.trades.input.TradeInputFragment.Tag"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(com.ceaver.assin.R.layout.trade_input_fragment, container, false)
    }

    override fun onStart() {
        super.onStart()

        val tradeId = lookupTradeId()
        val symbol = lookupSymbol()
        val tradeType = lookupTradeType()
        val viewModel = lookupViewModel(tradeId, symbol, tradeType)

        prepareView(tradeType)
        bindActions(viewModel)
        observeSymbols(viewModel)
        observeTrade(viewModel)
        observeStatus(viewModel)
        observeDataReady(viewModel)
    }

    private fun lookupTradeId(): Long? = arguments!!.getLong(Trade.TRADE_ID).takeUnless { it == 0L }
    private fun lookupSymbol(): String? = arguments!!.getString(Trade.SYMBOL)
    private fun lookupTradeType(): TradeType = TradeType.valueOf(arguments!!.getString(Trade.TRADE_TYPE)!!)
    private fun lookupViewModel(tradeId: Long?, symbol: String?, tradeType: TradeType): TradeInputViewModel = ViewModelProviders.of(this).get(TradeInputViewModel::class.java).initTrade(tradeId, symbol, tradeType)

    private fun prepareView(tradeType: TradeType) {
        tradeInputFragmentBuySymbolSpinner.isEnabled = false // not possible in XML
        tradeInputFragmentSellSymbolSpinner.isEnabled = false // not possible in XML
        when (tradeType) {
            TradeType.TRADE -> {
                tradeInputFragmentTradeTypeTextView.text = "Trade"
                tradeInputFragmentTradeTypeImageView.setImageResource(com.ceaver.assin.R.drawable.trade)
            }
            TradeType.DEPOSIT -> {
                tradeInputFragmentTradeTypeTextView.text = "Deposit"
                tradeInputFragmentTradeTypeImageView.setImageResource(com.ceaver.assin.R.drawable.deposit)

                tradeInputFragment.removeView(tradeInputFragmentSellTradeLabel)
                tradeInputFragment.removeView(tradeInputFragmentSellAmountTextView)
                tradeInputFragment.removeView(tradeInputFragmentSellSymbolSpinner)
                tradeInputFragment.removeView(tradeInputFragmentBuyTradeLabel)

                val constraintSet = ConstraintSet()
                constraintSet.clone(tradeInputFragment)
                constraintSet.connect(tradeInputFragmentCommentLabel.id, ConstraintSet.TOP, tradeInputFragmentBuyAmountTextView.id, ConstraintSet.BOTTOM, 20)
                constraintSet.connect(tradeInputFragmentBuyAmountTextView.id, ConstraintSet.TOP, tradeInputFragmentTradeTypeImageView.id, ConstraintSet.BOTTOM, 20)
                constraintSet.applyTo(tradeInputFragment)
            }
            TradeType.WITHDRAW -> {
                tradeInputFragmentTradeTypeTextView.text = "Withdraw"
                tradeInputFragmentTradeTypeImageView.setImageResource(com.ceaver.assin.R.drawable.withdraw)

                tradeInputFragment.removeView(tradeInputFragmentBuyTradeLabel)
                tradeInputFragment.removeView(tradeInputFragmentBuyAmountTextView)
                tradeInputFragment.removeView(tradeInputFragmentBuySymbolSpinner)
                tradeInputFragment.removeView(tradeInputFragmentSellTradeLabel)

                val constraintSet = ConstraintSet()
                constraintSet.clone(tradeInputFragment)
                constraintSet.connect(tradeInputFragmentSellAmountTextView.id, ConstraintSet.TOP, tradeInputFragmentTradeTypeImageView.id, ConstraintSet.BOTTOM, 20)
                constraintSet.applyTo(tradeInputFragment)
            }
        }
        tradeInputFragmentTradeDateTextView.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                val tradeDate = CalendarHelper.convertDate(tradeInputFragmentTradeDateTextView.text.toString())
                val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth -> tradeInputFragmentTradeDateTextView.setText(CalendarHelper.convertDate(LocalDate.of(year, monthOfYear + 1, dayOfMonth))); tradeInputFragmentTradeDateTextView.clearFocus() }
                val datePickerDialog = DatePickerDialog(this@TradeInputFragment.context!!, dateSetListener, tradeDate.year, tradeDate.monthValue - 1, tradeDate.dayOfMonth)
                datePickerDialog.show()
            }
        }
        tradeInputFragmentTradeDateTextView.keyListener = null // hack to disable user input
    }

    private fun bindActions(viewModel: TradeInputViewModel) {
        tradeInputFragmentSaveButton.setOnClickListener { onSaveClick(viewModel) }
    }

    private fun onSaveClick(viewModel: TradeInputViewModel) {
        val comment = tradeInputFragmentCommentTextView.text.toString().ifEmpty { null }
        val tradeDate = CalendarHelper.convertDate(tradeInputFragmentTradeDateTextView.text.toString())
        when (lookupTradeType()) {
            TradeType.TRADE -> {
                val buySymbol = tradeInputFragmentBuySymbolSpinner.selectedItem as Title
                val buyAmount = tradeInputFragmentBuyAmountTextView.text.toString().toDouble()
                val sellSymbol = tradeInputFragmentSellSymbolSpinner.selectedItem as Title
                val sellAmount = tradeInputFragmentSellAmountTextView.text.toString().toDouble()
                viewModel.onSaveTradeClick(buySymbol, buyAmount, sellSymbol, sellAmount, tradeDate, comment)
            }
            TradeType.DEPOSIT -> {
                val buySymbol = tradeInputFragmentBuySymbolSpinner.selectedItem as Title
                val buyAmount = tradeInputFragmentBuyAmountTextView.text.toString().toDouble()
                viewModel.onDepositClick(buySymbol, buyAmount, tradeDate, comment)
            }
            TradeType.WITHDRAW -> {
                val sellSymbol = tradeInputFragmentSellSymbolSpinner.selectedItem as Title
                val sellAmount = tradeInputFragmentSellAmountTextView.text.toString().toDouble()
                viewModel.onWithdrawClick(sellSymbol, sellAmount, tradeDate, comment)
            }
        }
    }

    private fun observeSymbols(viewModel: TradeInputViewModel) {
        val adapter = ArrayAdapter<Title>(this.context!!, android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        tradeInputFragmentBuySymbolSpinner.adapter = adapter
        tradeInputFragmentSellSymbolSpinner.adapter = adapter
        viewModel.symbols.observe(this, Observer { adapter.addAll(it) })
    }

    private fun observeTrade(viewModel: TradeInputViewModel) {
        viewModel.trade.observe(this, Observer {
            publishFields(it!!, viewModel);
        })
    }

    private fun observeDataReady(viewModel: TradeInputViewModel) {
        viewModel.dataReady.observe(this, Observer {
            updateSpinnerFields(viewModel, it!!.first)
            registerInputValidation()
            enableInput(true)
            viewModel.dataReady.removeObservers(this)
        })
    }

    private fun updateSpinnerFields(viewModel: TradeInputViewModel, trade: Trade) {
        if (trade.buyTitle != null) {
            tradeInputFragmentBuySymbolSpinner.setSelection(viewModel.symbols.value!!.indexOf(trade.buyTitle!!))
        }
        if (trade.sellTitle != null) {
            tradeInputFragmentSellSymbolSpinner.setSelection(viewModel.symbols.value!!.indexOf(trade.sellTitle!!))
        }
    }

    private fun publishFields(trade: Trade, viewModel: TradeInputViewModel) {
        tradeInputFragmentTradeDateTextView.setText(CalendarHelper.convertDate(trade.tradeDate))
        tradeInputFragmentCommentTextView.setText(trade.comment.orEmpty())
        when (lookupTradeType()) {
            TradeType.TRADE -> {
                tradeInputFragmentBuyAmountTextView.setText(if (trade.buyAmount != null) trade.buyAmount.toString() else "")
                tradeInputFragmentSellAmountTextView.setText(if (trade.sellAmount != null) trade.sellAmount.toString() else "")
            }
            TradeType.DEPOSIT -> {
                tradeInputFragmentBuyAmountTextView.setText(if (trade.buyAmount != null) trade.buyAmount.toString() else "")
            }
            TradeType.WITHDRAW -> {
                tradeInputFragmentSellAmountTextView.setText(if (trade.sellAmount != null) trade.sellAmount.toString() else "")
            }
        }
    }

    private fun observeStatus(viewModel: TradeInputViewModel) {
        viewModel.status.observe(this, Observer {
            when (it) {
                TradeInputViewModel.TradeInputStatus.START_SAVE -> onStartSave()
                TradeInputViewModel.TradeInputStatus.END_SAVE -> onEndSave()
            }
        })
    }

    private fun onStartSave() {
        enableInput(false)
    }

    private fun enableInput(enable: Boolean) {
        tradeInputFragmentSaveButton.isEnabled = enable && checkSaveButton()
        tradeInputFragmentCommentTextView.isEnabled = enable
        tradeInputFragmentTradeDateTextView.isEnabled = enable
        when (lookupTradeType()) {
            TradeType.TRADE -> {
                tradeInputFragmentBuyAmountTextView.isEnabled = enable
                tradeInputFragmentBuySymbolSpinner.isEnabled = enable
                tradeInputFragmentSellAmountTextView.isEnabled = enable
                tradeInputFragmentSellSymbolSpinner.isEnabled = enable
            }
            TradeType.DEPOSIT -> {
                tradeInputFragmentBuyAmountTextView.isEnabled = enable
                tradeInputFragmentBuySymbolSpinner.isEnabled = enable
            }
            TradeType.WITHDRAW -> {
                tradeInputFragmentSellAmountTextView.isEnabled = enable
            }
        }
    }

    private fun onEndSave() {
        dismiss()
    }

    private fun registerInputValidation() {
        when (lookupTradeType()) {
            TradeType.TRADE -> {
                tradeInputFragmentBuyAmountTextView.registerInputValidator({ s -> s.isNotEmpty() }, "Please enter amount")
                tradeInputFragmentSellAmountTextView.registerInputValidator({ s -> s.isNotEmpty() }, "Please enter amount")
                tradeInputFragmentBuyAmountTextView.afterTextChanged { tradeInputFragmentSaveButton.isEnabled = checkSaveButton() }
                tradeInputFragmentSellAmountTextView.afterTextChanged { tradeInputFragmentSaveButton.isEnabled = checkSaveButton() }
            }
            TradeType.DEPOSIT -> {
                tradeInputFragmentBuyAmountTextView.registerInputValidator({ s -> (s.isNotEmpty()) }, "Please enter amount")
                tradeInputFragmentBuyAmountTextView.afterTextChanged { tradeInputFragmentSaveButton.isEnabled = checkSaveButton() }
            }
            TradeType.WITHDRAW -> {
                tradeInputFragmentSellAmountTextView.registerInputValidator({ s -> (s.isNotEmpty()) }, "Please enter amount")
                tradeInputFragmentSellAmountTextView.afterTextChanged { tradeInputFragmentSaveButton.isEnabled = checkSaveButton() }
            }
        }
    }

    private fun checkSaveButton(): Boolean {
        return tradeInputFragmentBuyAmountTextView.error == null && tradeInputFragmentSellAmountTextView.error == null
    }
}
