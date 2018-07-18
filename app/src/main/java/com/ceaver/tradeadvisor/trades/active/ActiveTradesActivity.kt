package com.ceaver.tradeadvisor.trades.active

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.ceaver.tradeadvisor.trades.Trade

import android.support.v7.widget.DividerItemDecoration
import android.view.MenuItem
import android.view.View
import com.ceaver.tradeadvisor.*
import com.ceaver.tradeadvisor.trades.input.TradeInputActivity
import com.ceaver.tradeadvisor.trades.TradeRepository
import kotlinx.android.synthetic.main.active_trades_list.*
import java.util.*
import javax.inject.Inject

class ActiveTradesActivity : AppCompatActivity() {


    init {
        TradeRepository.deleteAllTrades() // TODO Temporary Helper - Remove!
        TradeRepository.insertTrade(Trade(0,1, Date(), 5.0, 5.0)) // TODO Temporary Helper - Remove!
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.active_trades_activity)

        activeTradesList.adapter = ActiveTradesAdapter(OnListItemClickListener())
        activeTradesList.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL))

        createTradeButton.setOnClickListener(OnCreateTradeClickListener())
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        (activeTradesList.adapter as ActiveTradesAdapter).refresh()
    }

    inner class OnListItemClickListener : OnItemClickListener {
        override fun onItemClick(item: Trade) {
            val intent = Intent(this@ActiveTradesActivity, TradeInputActivity::class.java);
            intent.putExtra(IntentKeys.TRADE_ID, item.id)
            startActivity(intent)
        }
    }

    inner class OnCreateTradeClickListener : View.OnClickListener {
        override fun onClick(v: View?) {
            val intent = Intent(this@ActiveTradesActivity, TradeInputActivity::class.java)
            startActivity(intent)
        }
    }

    public interface OnItemClickListener {
        fun onItemClick(item: Trade)
    }

    override fun onContextItemSelected(item: MenuItem?): Boolean {
        val selectedTrade = (activeTradesList.adapter as ActiveTradesAdapter).currentLongClickTrade!!
        TradeRepository.deleteTrade(selectedTrade)
        (activeTradesList.adapter as ActiveTradesAdapter).refresh()
        return super.onContextItemSelected(item)
    }
}
