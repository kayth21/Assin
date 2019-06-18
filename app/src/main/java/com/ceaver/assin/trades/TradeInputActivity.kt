package com.ceaver.assin.trades

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.ArrayAdapter
import com.ceaver.assin.R
import com.ceaver.assin.StartActivity
import com.ceaver.assin.extensions.afterTextChanged
import com.ceaver.assin.extensions.registerInputValidator
import com.ceaver.assin.markets.Title
import com.ceaver.assin.util.CalendarHelper
import com.ceaver.assin.util.DatePickerFragment
import kotlinx.android.synthetic.main.activity_trade_input.*
import java.time.LocalDate
import java.util.*

class TradeInputActivity : AppCompatActivity(), DatePickerFragment.DatePickerFragementCallback {

    companion object {
        val INTENT_EXTRA_TRADE_ID = UUID.randomUUID().toString()
        val INTENT_EXTRA_TRADE_TYPE = UUID.randomUUID().toString()
        val INTENT_EXTRA_TRADE_SYMBOL = UUID.randomUUID().toString()
    }

    private val purchaseDatePickerFragmentTag = UUID.randomUUID().toString()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        publishView()

        val tradeType = lookupTradeType()
        val viewModel = lookupViewModel(tradeType)

        modifyView(tradeType)
        bindActions(viewModel)
        observeSymbols(viewModel)
        observeTrade(viewModel)
        observeStatus(viewModel)
        observeDataReady(viewModel)
    }

    private fun modifyView(tradeType: TradeType) {
        tradeInputBuySymbolSpinner.isEnabled = false // not possible in XML
        tradeInputSellSymbolSpinner.isEnabled = false // not possible in XML
        when (tradeType) {
            TradeType.TRADE -> {
                tradeInputSaveButton.setText("Add Trade")
            }
            TradeType.DEPOSIT -> {
                tradeInputSaveButton.setText("Deposit")
            }
            TradeType.WITHDRAW -> {
                tradeInputSaveButton.setText("Withdraw")
            }
        }
        tradeInputTradeDateEditText.setOnFocusChangeListener { _, hasFocus -> if (hasFocus) DatePickerFragment().show(fragmentManager, purchaseDatePickerFragmentTag) }
        tradeInputTradeDateEditText.keyListener = null // hack to disable user input
    }

    private fun publishView() = setContentView(R.layout.activity_trade_input)

    private fun lookupTradeType() = TradeType.valueOf(intent.getStringExtra(INTENT_EXTRA_TRADE_TYPE))

    private fun lookupSymbol() = Optional.ofNullable(intent.getStringExtra(INTENT_EXTRA_TRADE_SYMBOL))

    private fun lookupTradeId(): Optional<Long> {
        val tradeId = intent.extras.getLong(INTENT_EXTRA_TRADE_ID)
        return if (tradeId == 0L) Optional.empty() else Optional.of(tradeId)
    }

    private fun lookupViewModel(tradeType: TradeType): TradeViewModel {
        return ViewModelProviders.of(this).get(TradeViewModel::class.java).initTrade(lookupTradeId(), lookupSymbol(), lookupTradeType())
    }

    private fun bindActions(viewModel: TradeViewModel) {
        tradeInputSaveButton.setOnClickListener { onSaveClick(viewModel) }
    }

    private fun onSaveClick(viewModel: TradeViewModel) {
        val comment = tradeInputCommentEditText.text.toString()
        val tradeDate = CalendarHelper.convertDate(tradeInputTradeDateEditText.text.toString())
        when (lookupTradeType()) {
            TradeType.TRADE -> {
                val buySymbol = tradeInputBuySymbolSpinner.selectedItem as Title
                val buyAmount = tradeInputBuyAmountEditText.text.toString().toDouble()
                val sellSymbol = tradeInputSellSymbolSpinner.selectedItem as Title
                val sellAmount = tradeInputSellAmountEditText.text.toString().toDouble()
                viewModel.onSaveTradeClick(buySymbol, buyAmount, sellSymbol, sellAmount, tradeDate, comment)
            }
            TradeType.DEPOSIT -> {
                val buySymbol = tradeInputBuySymbolSpinner.selectedItem as Title
                val buyAmount = tradeInputBuyAmountEditText.text.toString().toDouble()
                viewModel.onDepositClick(buySymbol, buyAmount, tradeDate, comment)
            }
            TradeType.WITHDRAW -> {
                val sellSymbol = tradeInputSellSymbolSpinner.selectedItem as Title
                val sellAmount = tradeInputSellAmountEditText.text.toString().toDouble()
                viewModel.onWithdrawClick(sellSymbol, sellAmount, tradeDate, comment)
            }
        }
    }

    private fun observeTrade(viewModel: TradeViewModel) {
        viewModel.trade.observe(this, Observer {
            publishFields(it!!, viewModel);
        })
    }

    private fun observeSymbols(viewModel: TradeViewModel) {
        val adapter = ArrayAdapter<Title>(this, android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        tradeInputBuySymbolSpinner.adapter = adapter
        tradeInputSellSymbolSpinner.adapter = adapter
        viewModel.symbols.observe(this, Observer { adapter.addAll(it) })
    }

    private fun observeDataReady(viewModel: TradeViewModel) {
        viewModel.dataReady.observe(this, Observer {
            updateSpinnerFields(viewModel, it!!.first)
            registerInputValidation()
            enableInput(true)
            viewModel.dataReady.removeObservers(this)
        })
    }

    private fun updateSpinnerFields(viewModel: TradeViewModel, trade: Trade) {
        if (trade.buyTitle != null) {
            tradeInputBuySymbolSpinner.setSelection(viewModel.symbols.value!!.indexOf(trade.buyTitle!!))
        }
        if (trade.sellTitle != null) {
            tradeInputSellSymbolSpinner.setSelection(viewModel.symbols.value!!.indexOf(trade.sellTitle!!))
        }
    }

    private fun publishFields(trade: Trade, viewModel: TradeViewModel) {
        tradeInputTradeDateEditText.setText(CalendarHelper.convertDate(trade.tradeDate))
        tradeInputCommentEditText.setText(trade.comment)
        when (lookupTradeType()) {
            TradeType.TRADE -> {
                tradeInputBuyAmountEditText.setText(if (trade.buyAmount != null) trade.buyAmount.toString() else "")
                tradeInputSellAmountEditText.setText(if (trade.sellAmount != null) trade.sellAmount.toString() else "")
            }
            TradeType.DEPOSIT -> {
                tradeInputBuyAmountEditText.setText(if (trade.buyAmount != null) trade.buyAmount.toString() else "")
            }
            TradeType.WITHDRAW -> {
                tradeInputSellAmountEditText.setText(if (trade.sellAmount != null) trade.sellAmount.toString() else "")
            }
        }
    }

    private fun observeStatus(viewModel: TradeViewModel) {
        viewModel.status.observe(this, Observer {
            when (it) {
                TradeViewModel.TradeInputStatus.START_SAVE -> onStartSave()
                TradeViewModel.TradeInputStatus.END_SAVE -> onEndSave()
            }
        })
    }

    private fun onStartSave() {
        enableInput(false)
    }

    private fun enableInput(enable: Boolean) {
        tradeInputSaveButton.isEnabled = enable && checkSaveButton()
        tradeInputCommentEditText.isEnabled = enable
        tradeInputTradeDateEditText.isEnabled = enable
        when (lookupTradeType()) {
            TradeType.TRADE -> {
                tradeInputBuyAmountEditText.isEnabled = enable
                tradeInputBuySymbolSpinner.isEnabled = enable
                tradeInputSellAmountEditText.isEnabled = enable
                tradeInputSellSymbolSpinner.isEnabled = enable
            }
            TradeType.DEPOSIT -> {
                tradeInputBuyAmountEditText.isEnabled = enable
                tradeInputBuySymbolSpinner.isEnabled = enable
            }
            TradeType.WITHDRAW -> {
                tradeInputSellAmountEditText.isEnabled = enable
                tradeInputSellSymbolSpinner.isEnabled = enable
            }
        }
    }

    private fun onEndSave() {
        exitActivity()
    }

    private fun exitActivity() {
        val intent = Intent(this, StartActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }

    private fun registerInputValidation() {
        tradeInputTradeDateEditText.registerInputValidator({ s -> s.isNotEmpty() }, "Please enter date")
        tradeInputTradeDateEditText.afterTextChanged { tradeInputSaveButton.isEnabled = checkSaveButton() }

        when (lookupTradeType()) {
            TradeType.TRADE -> {
                tradeInputBuyAmountEditText.registerInputValidator({ s -> s.isNotEmpty() }, "Please enter amount")
                tradeInputSellAmountEditText.registerInputValidator({ s -> s.isNotEmpty() }, "Please enter amount")
                tradeInputBuyAmountEditText.afterTextChanged { tradeInputSaveButton.isEnabled = checkSaveButton() }
                tradeInputSellAmountEditText.afterTextChanged { tradeInputSaveButton.isEnabled = checkSaveButton() }
            }
            TradeType.DEPOSIT -> {
                tradeInputBuyAmountEditText.registerInputValidator({ s -> (s.isNotEmpty()) }, "Please enter amount")
                tradeInputBuyAmountEditText.afterTextChanged { tradeInputSaveButton.isEnabled = checkSaveButton() }
            }
            TradeType.WITHDRAW -> {
                tradeInputSellAmountEditText.registerInputValidator({ s -> (s.isNotEmpty()) }, "Please enter amount")
                tradeInputSellAmountEditText.afterTextChanged { tradeInputSaveButton.isEnabled = checkSaveButton() }
            }
        }
    }

    private fun checkSaveButton(): Boolean {
        return tradeInputBuyAmountEditText.error == null && tradeInputSellAmountEditText.error == null
    }

    override fun onDatePickerFragmentDateSelected(tag: String, date: LocalDate) {
        if (purchaseDatePickerFragmentTag.equals(tag)) {
            tradeInputTradeDateEditText.setText(CalendarHelper.convertDate(date))
        }
    }
}