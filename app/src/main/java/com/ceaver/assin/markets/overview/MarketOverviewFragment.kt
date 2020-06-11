package com.ceaver.assin.markets.overview

import androidx.lifecycle.Observer
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.ceaver.assin.AssinWorkerEvents
import com.ceaver.assin.R
import kotlinx.android.synthetic.main.market_overview_fragment.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class MarketOverviewFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createViewModel(Observer { onMarketOverviewLoaded(it!!) })
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

    private fun createViewModel(marketOverviewObserver: Observer<MarketOverview>): MarketOverviewViewModel {
        val viewModel by viewModels<MarketOverviewViewModel>()
        viewModel.init(this, marketOverviewObserver)
        return viewModel
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