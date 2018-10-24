package com.ceaver.assin.trades

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.ceaver.assin.R
import com.ceaver.assin.StartActivity
import com.ceaver.assin.databinding.ActivityTradeInputBinding
import com.ceaver.assin.extensions.validateFields
import com.ceaver.assin.util.CalendarHelper
import com.ceaver.assin.util.DatePickerFragment
import kotlinx.android.synthetic.main.activity_trade_input.*
import java.time.LocalDate
import java.util.*


class TradeInputActivity : AppCompatActivity(), DatePickerFragment.DatePickerFragementCallback {

    private val purchaseDatePickerFragmentTag = UUID.randomUUID().toString()
    lateinit var binding: ActivityTradeInputBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_trade_input)
        val tradeId = intent.getLongExtra(com.ceaver.assin.IntentKeys.TRADE_ID, 0)

        purchaseDateEditText.setOnFocusChangeListener { _, hasFocus -> if (hasFocus) DatePickerFragment().show(fragmentManager, purchaseDatePickerFragmentTag) }
        purchaseDateEditText.setKeyListener(null) // hack to disable user input

        // TODO Start: Remove
        coinmarketcapIdEditText.setText("Bitcoin (BTC)")
        coinmarketcapIdEditText.setKeyListener(null) // hack to disable user input
        // TODO End

        val viewModel = ViewModelProviders.of(this).get(TradeViewModel::class.java).init(tradeId)

        viewModel.trade.observe(this, Observer { binding.trade = it; validateFields(); saveButton.isEnabled = true })

        saveButton.setOnClickListener {
            // TODO Replace with some generic code / better implementation
            if (coinmarketcapIdEditText.error != null || purchaseDateEditText.error != null || purchasePriceEditText.error != null || purchaseAmountEditText.error != null) {
                return@setOnClickListener
            }
            TradeRepository.saveTradeAsync(viewModel.trade.value!!)
            exitActivity()
        }
    }

    private fun validateFields() {
        coinmarketcapIdEditText.validateFields({ s -> (s.length >= 1) }, "Please select asset")
        purchaseDateEditText.validateFields({ s -> (s.length >= 1) }, "Please enter purchase date")
        purchasePriceEditText.validateFields({ s -> (s.length >= 1) }, "Please enter price")
        purchaseAmountEditText.validateFields({ s -> (s.length >= 1) }, "Please enter amount")
    }

    private fun exitActivity() {
        val intent = Intent(this, StartActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }

    override fun onDatePickerFragmentDateSelected(tag: String, date: LocalDate) {
        if (purchaseDatePickerFragmentTag.equals(tag)) {
            purchaseDateEditText.setText(CalendarHelper.convertDate(date))
        }
    }
}