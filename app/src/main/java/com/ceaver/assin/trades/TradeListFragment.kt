package com.ceaver.assin.trades

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import com.ceaver.assin.trades.input.TradeInputFragment
import kotlinx.android.synthetic.main.fragment_trade_list.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class TradeListFragment : Fragment() {

    private val tradeListAdapter = TradeListAdapter(OnListItemClickListener())

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(com.ceaver.assin.R.layout.fragment_trade_list, container, false)
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this);
        tradeList.adapter = tradeListAdapter
        tradeList.addItemDecoration(DividerItemDecoration(activity!!.application, LinearLayoutManager.VERTICAL)) // TODO Seriously?
        createTradeButton.setOnClickListener {
            var arguments = Bundle();
            arguments.putString(Trade.TRADE_TYPE, TradeType.TRADE.name)
            val tradeInputFragment = TradeInputFragment()
            tradeInputFragment.arguments = arguments
            tradeInputFragment.show(fragmentManager, TradeInputFragment.TRADE_INPUT_FRAGMENT_TAG)
        }
        loadAllTrades()
        swipeRefreshLayout.setOnRefreshListener { loadAllTrades() }
    }

    private fun loadAllTrades() {
        TradeRepository.loadAllTradesAsync(true) { onAllTradesLoaded(it) }
    }

    private fun onAllTradesLoaded(trades: List<Trade>) {
        tradeListAdapter.tradeList = trades.toMutableList().sortedBy { it.tradeDate }.reversed(); tradeListAdapter.notifyDataSetChanged(); swipeRefreshLayout.isRefreshing = false
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
        tradeList.adapter = null
        tradeList.removeItemDecorationAt(0) // TODO Seriously?
        createTradeButton.setOnClickListener(null)
        swipeRefreshLayout.setOnRefreshListener(null)
    }

    @Suppress("UNUSED_PARAMETER")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: TradeEvents.DeleteAll) {
        loadAllTrades()
    }

    @Suppress("UNUSED_PARAMETER")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: TradeEvents.Delete) {
        loadAllTrades()
    }

    @Suppress("UNUSED_PARAMETER")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: TradeEvents.Insert) {
        loadAllTrades()
    }

    @Suppress("UNUSED_PARAMETER")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: TradeEvents.Update) {
        loadAllTrades()
    }

    interface OnItemClickListener {
        fun onItemClick(item: Trade)
    }

    private inner class OnListItemClickListener : OnItemClickListener {
        override fun onItemClick(item: Trade) {
            val arguments = Bundle();
            arguments.putLong(Trade.TRADE_ID, item.id)
            arguments.putString(Trade.TRADE_TYPE, item.getTradeType().name)
            val tradeInputFragment = TradeInputFragment()
            tradeInputFragment.arguments = arguments
            tradeInputFragment.show(fragmentManager, TradeInputFragment.TRADE_INPUT_FRAGMENT_TAG)
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