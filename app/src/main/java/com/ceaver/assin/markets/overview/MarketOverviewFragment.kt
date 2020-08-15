package com.ceaver.assin.markets.overview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.ceaver.assin.AssinWorkerEvents
import com.ceaver.assin.R
import kotlinx.android.synthetic.main.market_overview_fragment.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class MarketOverviewFragment : Fragment() {

    private lateinit var viewModel: MarketOverviewViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = viewModels<MarketOverviewViewModel> { MarketOverviewViewModel.Factory(this, Observer { onMarketOverviewLoaded(it!!) }) }.value
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.market_overview_fragment, container, false)
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this);
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this);
    }

    private fun onMarketOverviewLoaded(marketOverview: MarketOverview) {
        marketOverviewFragmentMarketCapUsdValue.text = "${marketOverview.marketCapUsd.div(1000000000).toString()} bn USD (${marketOverview.dailyMarketCapChange}%)"
        marketOverviewFragmentBtcDominanceValue.text = marketOverview.btcDominancePercentage.toString() + "%"
    }

    @Suppress("UNUSED_PARAMETER")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: AssinWorkerEvents.Complete) {
        val viewModel by viewModels<MarketOverviewViewModel>()
        viewModel.loadMarketOverview()
    }
}