package com.ceaver.tradeadvisor.trades.input

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.ceaver.tradeadvisor.IntentKeys
import com.ceaver.tradeadvisor.MainActivity
import com.ceaver.tradeadvisor.R
import com.ceaver.tradeadvisor.trades.Trade
import com.ceaver.tradeadvisor.trades.TradeRepository
import com.ceaver.tradeadvisor.util.CalendarHelper
import com.ceaver.tradeadvisor.util.DatePickerFragment
import kotlinx.android.synthetic.main.activity_trade_input.*
import java.util.*
import com.ceaver.tradeadvisor.extensions.*

class TradeInputActivity : AppCompatActivity(), DatePickerFragment.DatePickerFragementCallback {

    private val purchaseDatePickerFragmentTag = UUID.randomUUID().toString()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trade_input)

        val tradeId = intent.getLongExtra(IntentKeys.TRADE_ID, 0)

        if (tradeId > 0)
            publishFields(TradeRepository.loadTrade(tradeId))

        saveButton.setOnClickListener {
            // TODO Replace with some generic code / better implementation
            if (coinmarketcapIdEditText.error != null || purchaseDateEditText.error != null || purchasePriceEditText.error != null || purchaseAmountEditText.error != null) {
                return@setOnClickListener
            }
            val trade = createTrade(tradeId)
            TradeRepository.saveTrade(trade)
            exitActivity()
        }

        purchaseDateEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus)
                DatePickerFragment().show(fragmentManager, purchaseDatePickerFragmentTag)
        }
        purchaseDateEditText.setKeyListener(null) // hack to disable user input

        coinmarketcapIdEditText.validate({ s -> (s.length >= 1) }, "Please select asset")
        purchaseDateEditText.validate({ s -> (s.length >= 1) }, "Please enter purchase date")
        purchasePriceEditText.validate({ s -> (s.length >= 1) }, "Please enter price")
        purchaseAmountEditText.validate({ s -> (s.length >= 1) }, "Please enter amount")

        // TODO Start: Remove
        coinmarketcapIdEditText.setText("Bitcoin (BTC)")
        coinmarketcapIdEditText.setKeyListener(null) // hack to disable user input
        // TODO End
    }

    private fun publishFields(trade: Trade) {
        coinmarketcapIdEditText.setText(trade.coinmarketcapId.toString())
        purchaseDateEditText.setText(CalendarHelper.convertDate(trade.tradeDate))
        purchaseAmountEditText.setText(trade.purchaseAmount.toString())
        purchasePriceEditText.setText(trade.purchasePrice.toString())
    }

    private fun exitActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }

    private fun createTrade(tradeId: Long): Trade {
        val purchaseAmount = purchaseAmountEditText.text.toString().toDouble()
        val coinmarketcapId = 1 // TODO
        val tradeDate = CalendarHelper.convertDate(purchaseDateEditText.text.toString())
        val purchasePrice = purchasePriceEditText.text.toString().toDouble()

        return Trade(tradeId, coinmarketcapId, tradeDate, purchasePrice, purchaseAmount)
    }

    override fun onDatePickerFragmentDateSelected(tag: String, date: Date) {
        if (purchaseDatePickerFragmentTag.equals(tag)) {
            purchaseDateEditText.setText(CalendarHelper.convertDate(date))
        }
    }
}