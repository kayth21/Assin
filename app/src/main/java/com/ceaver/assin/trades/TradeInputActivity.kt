package com.ceaver.assin.trades

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
import com.ceaver.assin.util.CalendarHelper
import com.ceaver.assin.util.DatePickerFragment
import kotlinx.android.synthetic.main.activity_trade_input.*
import java.time.LocalDate
import java.util.*

class TradeInputActivity : AppCompatActivity(), DatePickerFragment.DatePickerFragementCallback {

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
        tradeInputTradeDateEditText.setKeyListener(null) // hack to disable user input
    }

    private fun publishView() = setContentView(R.layout.activity_trade_input)

    private fun lookupTradeType() = TradeType.valueOf(intent.getStringExtra(IntentKeys.TRADE_TYPE))

    private fun lookupSymbol() = intent.getStringExtra(IntentKeys.SYMBOL)

    private fun lookupTradeId() = intent.getLongExtra(IntentKeys.TRADE_ID, 0)

    private fun lookupViewModel(tradeType: TradeType): TradeViewModel {
        val viewModel = ViewModelProviders.of(this).get(TradeViewModel::class.java)
        return when (tradeType) {
            TradeType.TRADE -> viewModel.initTrade(lookupTradeId())
            TradeType.DEPOSIT -> viewModel.initDeposit(Optional.ofNullable(lookupSymbol()))
            TradeType.WITHDRAW -> viewModel.initWithdraw(lookupSymbol())
        }
    }

    private fun bindActions(viewModel: TradeViewModel) {
        tradeInputSaveButton.setOnClickListener { onSaveClick(viewModel) }
    }

    private fun onSaveClick(viewModel: TradeViewModel) {
        val comment = tradeInputCommentEditText.text.toString()
        val tradeDate = CalendarHelper.convertDate(tradeInputTradeDateEditText.text.toString())
        when (lookupTradeType()) {
            TradeType.TRADE -> {
                val buySymbol = tradeInputBuySymbolSpinner.selectedItem as String
                val buyAmount = tradeInputBuyAmountEditText.text.toString().toDouble()
                val sellSymbol = tradeInputSellSymbolSpinner.selectedItem as String
                val sellAmount = tradeInputSellAmountEditText.text.toString().toDouble()
                viewModel.onSaveTradeClick(buySymbol, buyAmount, sellSymbol, sellAmount, tradeDate, comment)
            }
            TradeType.DEPOSIT -> {
                val buySymbol = tradeInputBuySymbolSpinner.selectedItem as String
                val buyAmount = tradeInputBuyAmountEditText.text.toString().toDouble()
                viewModel.onDepositClick(buySymbol, buyAmount, tradeDate, comment)
            }
            TradeType.WITHDRAW -> {
                val sellSymbol = tradeInputSellSymbolSpinner.selectedItem as String
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
        val adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        tradeInputBuySymbolSpinner.adapter = adapter
        tradeInputSellSymbolSpinner.adapter = adapter
        viewModel.symbols.observe(this, Observer { adapter.addAll(it) })
    }

    private fun observeDataReady(viewModel: TradeViewModel) {
        viewModel.dataReady.observe(this, Observer {
            registerInputValidation()
            enableInput(true)
            viewModel.dataReady.removeObservers(this)
        })
    }

    private fun publishFields(trade: Trade, viewModel: TradeViewModel) {
        tradeInputTradeDateEditText.setText(CalendarHelper.convertDate(trade.tradeDate))
        when (lookupTradeType()) {
            TradeType.TRADE -> {
                tradeInputBuyAmountEditText.setText(if (trade.buyAmount.isPresent) trade.buyAmount.get().toString() else "")
                tradeInputBuyAmountEditText.setText(if (trade.sellAmount.isPresent) trade.sellAmount.get().toString() else "")
            }
            TradeType.DEPOSIT -> {
                tradeInputBuyAmountEditText.setText(if (trade.buyAmount.isPresent) trade.buyAmount.get().toString() else "")
            }
            TradeType.WITHDRAW -> {
                tradeInputBuyAmountEditText.setText(if (trade.sellAmount.isPresent) trade.sellAmount.get().toString() else "")
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
                tradeInputBuyAmountEditText.afterTextChanged { tradeInputSaveButton.isEnabled = checkSaveButton() }
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