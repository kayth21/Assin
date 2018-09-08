package com.ceaver.assin.trades

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.ceaver.assin.R
import com.ceaver.assin.StartActivity
import com.ceaver.assin.extensions.validateFields
import com.ceaver.assin.util.CalendarHelper
import com.ceaver.assin.util.DatePickerFragment
import kotlinx.android.synthetic.main.activity_trade_input.*
import java.time.LocalDate
import java.util.*

class TradeInputActivity : AppCompatActivity(), DatePickerFragment.DatePickerFragementCallback {

    private val purchaseDatePickerFragmentTag = UUID.randomUUID().toString()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trade_input)

        purchaseDateEditText.setOnFocusChangeListener { _, hasFocus -> if (hasFocus) DatePickerFragment().show(fragmentManager, purchaseDatePickerFragmentTag) }
        purchaseDateEditText.setKeyListener(null) // hack to disable user input

        // TODO Start: Remove
        coinmarketcapIdEditText.setText("Bitcoin (BTC)")
        coinmarketcapIdEditText.setKeyListener(null) // hack to disable user input
        // TODO End
    }

    override fun onStart() {
        super.onStart()

        val tradeId = intent.getLongExtra(com.ceaver.assin.IntentKeys.TRADE_ID, 0)

        if (tradeId > 0)
            TradeRepository.loadTradeAsync(tradeId, true) { publishFields(it); validateFields() }
        else
            hodlStrategyRadioButton.isChecked = true; validateFields()

        saveButton.setOnClickListener {
            // TODO Replace with some generic code / better implementation
            if (coinmarketcapIdEditText.error != null || purchaseDateEditText.error != null || purchasePriceEditText.error != null || purchaseAmountEditText.error != null) {
                return@setOnClickListener
            }
            val trade = createTrade(tradeId)
            TradeRepository.saveTradeAsync(trade)
            exitActivity()
        }
    }

    private fun validateFields() {
        coinmarketcapIdEditText.validateFields({ s -> (s.length >= 1) }, "Please select asset")
        purchaseDateEditText.validateFields({ s -> (s.length >= 1) }, "Please enter purchase date")
        purchasePriceEditText.validateFields({ s -> (s.length >= 1) }, "Please enter price")
        purchaseAmountEditText.validateFields({ s -> (s.length >= 1) }, "Please enter amount")
    }

    private fun publishFields(trade: Trade) {
        coinmarketcapIdEditText.setText(trade.coinmarketcapId.toString())
        purchaseDateEditText.setText(CalendarHelper.convertDate(trade.tradeDate))
        purchaseAmountEditText.setText(trade.purchaseAmount.toString())
        purchasePriceEditText.setText(trade.purchasePrice.toString())
        trade.strategies.forEach {
            when (it) {
                TradeStrategy.HODL -> hodlStrategyRadioButton.isChecked = true
                TradeStrategy.DOUBLE_OUT -> doubleOutStrategyRadioButton.isChecked = true
                TradeStrategy.ASAP_NO_LOSSES -> asapNoLossesRadioButton.isChecked = true
                TradeStrategy.BAD_TRADE -> badTradeNotificationCheckbox.isChecked = true
                else -> throw NotImplementedError("no radio button for ${it}")
            }
        }
    }

    private fun exitActivity() {
        val intent = Intent(this, StartActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }

    private fun createTrade(tradeId: Long): Trade {
        val purchaseAmount = purchaseAmountEditText.text.toString().toDouble()
        val coinmarketcapId = 1 // TODO
        val tradeDate = CalendarHelper.convertDate(purchaseDateEditText.text.toString())
        val purchasePrice = purchasePriceEditText.text.toString().toDouble()
        val tradeStrategy: TradeStrategy = if (strategyRadioGroup.checkedRadioButtonId == hodlStrategyRadioButton.id) TradeStrategy.HODL else if (strategyRadioGroup.checkedRadioButtonId == doubleOutStrategyRadioButton.id) TradeStrategy.DOUBLE_OUT else TradeStrategy.ASAP_NO_LOSSES // TODO :) Binding?
        val badTradeNotification = badTradeNotificationCheckbox.isChecked

        return Trade(tradeId, coinmarketcapId, tradeDate, purchasePrice, purchaseAmount, if (badTradeNotification) setOf(tradeStrategy, TradeStrategy.BAD_TRADE) else setOf(tradeStrategy))
    }

    override fun onDatePickerFragmentDateSelected(tag: String, date: LocalDate) {
        if (purchaseDatePickerFragmentTag.equals(tag)) {
            purchaseDateEditText.setText(CalendarHelper.convertDate(date))
        }
    }
}