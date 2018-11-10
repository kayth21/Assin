package com.ceaver.assin.trades

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.ceaver.assin.IntentKeys
import com.ceaver.assin.R
import com.ceaver.assin.StartActivity
import com.ceaver.assin.databinding.ActivityTradeInputBinding
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

        val binding = publishView()
        val tradeId = lookupTradeId()
        val viewModel = lookupViewModel(tradeId)

        bindActions(viewModel, binding)
        bindTrade(viewModel, binding)
        observeStatus(viewModel)

        modifyPurchaseDateField()
        modifyCoinMarketCapField()
    }

    private fun publishView(): ActivityTradeInputBinding = DataBindingUtil.setContentView(this, R.layout.activity_trade_input)

    private fun lookupTradeId() = intent.getLongExtra(IntentKeys.TRADE_ID, 0)

    private fun lookupViewModel(tradeId: Long): TradeViewModel = ViewModelProviders.of(this).get(TradeViewModel::class.java).init(tradeId)

    private fun bindActions(viewModel: TradeViewModel, binding: ActivityTradeInputBinding) {
        binding.saveClickHandler = viewModel
    }

    private fun bindTrade(viewModel: TradeViewModel, binding: ActivityTradeInputBinding) {
        viewModel.trade.observe(this, Observer { onTradeUpdate(binding, it) })
    }

    private fun onTradeUpdate(binding: ActivityTradeInputBinding, trade: Trade?) {
        binding.trade = trade
        validateFields()
        saveButton.isEnabled = true
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
        saveButton.isEnabled = false // TODO Disable inputs fields as well
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

    private fun modifyCoinMarketCapField() {
        // TODO Start: Remove
        coinmarketcapIdEditText.setText("Bitcoin (BTC)")
        coinmarketcapIdEditText.setKeyListener(null) // hack to disable user input
        // TODO End
    }

    private fun validateFields() {
        coinmarketcapIdEditText.registerInputValidator({ s -> (s.length >= 1) }, "Please select asset")
        purchaseDateEditText.registerInputValidator({ s -> (s.length >= 1) }, "Please enter purchase date")
        purchasePriceEditText.registerInputValidator({ s -> (s.length >= 1) }, "Please enter price")
        purchaseAmountEditText.registerInputValidator({ s -> (s.length >= 1) }, "Please enter amount")
    }

    private fun TradeInputActivity.modifyPurchaseDateField() {
        purchaseDateEditText.setOnFocusChangeListener { _, hasFocus -> if (hasFocus) DatePickerFragment().show(fragmentManager, purchaseDatePickerFragmentTag) }
        purchaseDateEditText.setKeyListener(null) // hack to disable user input
    }

    override fun onDatePickerFragmentDateSelected(tag: String, date: LocalDate) {
        if (purchaseDatePickerFragmentTag.equals(tag)) {
            purchaseDateEditText.setText(CalendarHelper.convertDate(date))
        }
    }
}