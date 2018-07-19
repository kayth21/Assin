package com.ceaver.tradeadvisor.trades.list

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import com.ceaver.tradeadvisor.IntentKeys

import com.ceaver.tradeadvisor.R
import com.ceaver.tradeadvisor.trades.Trade
import com.ceaver.tradeadvisor.trades.TradeRepository
import com.ceaver.tradeadvisor.trades.input.TradeInputActivity
import kotlinx.android.synthetic.main.fragment_trade_list.*

class TradeListFragment : Fragment() {

    val tradeListAdapter = TradeListAdapter(OnListItemClickListener())

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_trade_list, container, false)
    }

    override fun onStart() {
        super.onStart()
        tradeListAdapter.reload()
        tradeList.adapter = tradeListAdapter
        tradeList.addItemDecoration(DividerItemDecoration(activity.application, LinearLayoutManager.VERTICAL)) // TODO Seriously?
        createTradeButton.setOnClickListener(OnCreateTradeClickListener())
    }

    override fun onStop() {
        super.onStop()
        tradeList.adapter = null
        createTradeButton.setOnClickListener(null)
    }

    interface OnItemClickListener {
        fun onItemClick(item: Trade)
    }

    private inner class OnListItemClickListener : OnItemClickListener {
        override fun onItemClick(item: Trade) {
            val intent = Intent(activity.application, TradeInputActivity::class.java);
            intent.putExtra(IntentKeys.TRADE_ID, item.id)
            startActivity(intent)
        }
    }

    private inner class OnCreateTradeClickListener : View.OnClickListener {
        override fun onClick(v: View?) {
            val intent = Intent(activity.application, TradeInputActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onContextItemSelected(item: MenuItem?): Boolean {
        val selectedTrade = tradeListAdapter.currentLongClickTrade!!
        TradeRepository.deleteTrade(selectedTrade)
        tradeListAdapter.reload()
        return super.onContextItemSelected(item)
    }
}
