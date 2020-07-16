package com.ceaver.assin.markets.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.ceaver.assin.AssinWorkerEvents
import com.ceaver.assin.AssinWorkers
import com.ceaver.assin.markets.Title
import com.ceaver.assin.markets.TitleRepository
import com.ceaver.assin.util.isConnected
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_market_list.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class MarketListFragment : Fragment() {

    private val marketListAdapter = MarketListAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(com.ceaver.assin.R.layout.fragment_market_list, container, false)
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this);
        marketList.adapter = marketListAdapter
        marketList.addItemDecoration(DividerItemDecoration(requireActivity().application, LinearLayoutManager.VERTICAL)) // TODO Seriously?
        marketSwipeRefreshLayout.setOnRefreshListener { refreshAllTitles() }
        loadActiveCryptoTitles()
    }

    private fun refreshAllTitles() {
        if (isConnected())
            AssinWorkers.completeUpdate()
        else {
            Snackbar.make(marketFrameLayout, "no internet connection", Snackbar.LENGTH_LONG).show(); marketSwipeRefreshLayout.isRefreshing = false
        }
    }

    private fun loadActiveCryptoTitles() {
        TitleRepository.loadActiveCryptoTitlesAsync(true) { onAllTitlesLoaded(it) };
    }

    private fun onAllTitlesLoaded(titles: List<Title>) {
        marketListAdapter.titles = titles; marketListAdapter.notifyDataSetChanged(); marketSwipeRefreshLayout?.isRefreshing = false
    }

    @Suppress("UNUSED_PARAMETER")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: AssinWorkerEvents.Complete) {
        loadActiveCryptoTitles()
        Toast.makeText(getActivity(), "Markets refreshed", Toast.LENGTH_SHORT).show();
    }

    @Suppress("UNUSED_PARAMETER")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: AssinWorkerEvents.Observed) {
        loadActiveCryptoTitles()
        Toast.makeText(getActivity(), "Observed refreshed", Toast.LENGTH_SHORT).show();
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this);
        marketList.adapter = null
        marketSwipeRefreshLayout.setOnRefreshListener(null)
        marketList.removeItemDecorationAt(0) // TODO Seriously?
    }
}
