package com.ceaver.tradeadvisor.trades.list

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.ceaver.tradeadvisor.IntentKeys

import com.ceaver.tradeadvisor.R
import com.ceaver.tradeadvisor.trades.Trade
import com.ceaver.tradeadvisor.trades.TradeRepository
import com.ceaver.tradeadvisor.trades.input.TradeInputActivity
import kotlinx.android.synthetic.main.fragment_trade_list.*
import com.ceaver.tradeadvisor.trades.TradeEvents
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class TradeListFragment : Fragment() {

    private val tradeListAdapter = TradeListAdapter(OnListItemClickListener())

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_trade_list, container, false)
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this);
        tradeList.adapter = tradeListAdapter
        tradeList.addItemDecoration(DividerItemDecoration(activity.application, LinearLayoutManager.VERTICAL)) // TODO Seriously?
        createTradeButton.setOnClickListener { startActivity(Intent(activity.application, TradeInputActivity::class.java)) }
        TradeRepository.loadAllTrades()
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this);
        tradeList.adapter = null
        createTradeButton.setOnClickListener(null)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: TradeEvents.Delete) {
        TradeRepository.loadAllTrades()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: TradeEvents.Insert) {
        TradeRepository.loadAllTrades()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: TradeEvents.Update) {
        TradeRepository.loadAllTrades()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: TradeEvents.LoadAll) {
        tradeListAdapter.tradeList = event.trades
        tradeListAdapter.notifyDataSetChanged()
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

    override fun onContextItemSelected(item: MenuItem?): Boolean {
        val selectedTrade = tradeListAdapter.currentLongClickTrade!!
        TradeRepository.deleteTrade(selectedTrade)
        return super.onContextItemSelected(item)
    }
}