package com.ceaver.assin.trades

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import com.ceaver.assin.R
import kotlinx.android.synthetic.main.fragment_trade_list.*
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
        tradeList.addItemDecoration(DividerItemDecoration(activity!!.application, LinearLayoutManager.VERTICAL)) // TODO Seriously?
        createTradeButton.setOnClickListener {
            val intent = Intent(activity!!.application, TradeInputActivity::class.java);
            intent.putExtra(TradeInputActivity.INTENT_EXTRA_TRADE_TYPE, TradeType.TRADE.toString())
            startActivity(intent)
        }
        loadAllTrades()
        swipeRefreshLayout.setOnRefreshListener { loadAllTrades() }
    }

    private fun loadAllTrades() {
        TradeRepository.loadAllTradesAsync(true) { onAllTradesLoaded(it) }
    }

    private fun onAllTradesLoaded(trades: List<Trade>) {
        tradeListAdapter.tradeList = trades; tradeListAdapter.notifyDataSetChanged(); swipeRefreshLayout.isRefreshing = false
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
        tradeList.adapter = null
        tradeList.removeItemDecorationAt(0) // TODO Seriously?
        createTradeButton.setOnClickListener(null)
        swipeRefreshLayout.setOnRefreshListener(null)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: TradeEvents.Delete) {
        loadAllTrades()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: TradeEvents.Insert) {
        loadAllTrades()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: TradeEvents.Update) {
        loadAllTrades()
    }

    interface OnItemClickListener {
        fun onItemClick(item: Trade)
    }

    private inner class OnListItemClickListener : OnItemClickListener {
        override fun onItemClick(trade: Trade) {
            val intent = Intent(activity!!.application, TradeInputActivity::class.java);
            intent.putExtra(TradeInputActivity.INTENT_EXTRA_TRADE_TYPE, trade.getTradeType().toString())
            intent.putExtra(TradeInputActivity.INTENT_EXTRA_TRADE_ID, trade.id)
            startActivity(intent)
        }
    }

    override fun onContextItemSelected(item: MenuItem?): Boolean {
        if (item!!.groupId == 3 && item.itemId == 0) {
            val selectedTrade = tradeListAdapter.currentLongClickTrade!!
            TradeRepository.deleteTradeAsync(selectedTrade)
        }
        return super.onContextItemSelected(item)
    }
}