package com.ceaver.assin.markets

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.ceaver.assin.AssinWorkerEvents
import com.ceaver.assin.AssinWorkers
import com.ceaver.assin.R
import kotlinx.android.synthetic.main.fragment_market_list.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class MarketListFragment : Fragment() {

    private val marketListAdapter = MarketListAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_market_list, container, false)
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this);
        marketList.adapter = marketListAdapter
        marketList.addItemDecoration(DividerItemDecoration(activity!!.application, LinearLayoutManager.VERTICAL)) // TODO Seriously?
        marketSwipeRefreshLayout.setOnRefreshListener { refreshAllTitles() }
        loadAllTitles()
    }

    private fun refreshAllTitles() {
        AssinWorkers.completeUpdate()
    }

    private fun loadAllTitles() {
        TitleRepository.loadAllTitlesAsync(true) { onAllTitlesLoaded(it) };
    }

    private fun onAllTitlesLoaded(titles: List<Title>) {
        marketListAdapter.titles = titles; marketListAdapter.notifyDataSetChanged(); marketSwipeRefreshLayout.isRefreshing = false
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: AssinWorkerEvents.Complete) {
        loadAllTitles()
        Toast.makeText(getActivity(), "Markets refreshed", Toast.LENGTH_SHORT).show();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: AssinWorkerEvents.Observed) {
        loadAllTitles()
        Toast.makeText(getActivity(), "Observed refreshed", Toast.LENGTH_SHORT).show();
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this);
        marketList.adapter = null
        marketSwipeRefreshLayout.setOnRefreshListener(null)
    }

}
