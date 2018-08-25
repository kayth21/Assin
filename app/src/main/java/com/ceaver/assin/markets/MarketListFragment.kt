package com.ceaver.assin.markets

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.ceaver.assin.R
import kotlinx.android.synthetic.main.fragment_market_list.*

class MarketListFragment : Fragment() {

    private val marketListAdapter = MarketListAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_market_list, container, false)
    }

    override fun onStart() {
        super.onStart()
//        EventBus.getDefault().register(this);
        marketList.adapter = marketListAdapter
        marketList.addItemDecoration(DividerItemDecoration(activity.application, LinearLayoutManager.VERTICAL)) // TODO Seriously?
        marketSwipeRefreshLayout.setOnRefreshListener { loadAllTitles() }
        marketSwipeRefreshLayout.isRefreshing = true;
        loadAllTitles()
    }

    private fun loadAllTitles() {
        MarketRepository.loadAllTitlesAsync(true) { onAllTitlesLoaded(it) }
    }

    private fun onAllTitlesLoaded(titles: List<Title>) {
        marketListAdapter.titles = titles;
        marketListAdapter.notifyDataSetChanged();
        marketSwipeRefreshLayout.isRefreshing = false;
    }

    override fun onStop() {
        super.onStop()
//        EventBus.getDefault().unregister(this);
        marketList.adapter = null
//        swipeRefreshLayout.setOnRefreshListener(null)
    }

}
