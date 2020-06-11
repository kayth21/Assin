package com.ceaver.assin.assets.overview

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.ceaver.assin.AssinWorkerEvents
import com.ceaver.assin.R
import com.ceaver.assin.extensions.format
import com.ceaver.assin.trades.TradeEvents
import kotlinx.android.synthetic.main.asset_overview_fragment.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class AssetOverviewFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createViewModel(Observer { onAssetOverviewLoaded(it!!) })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.asset_overview_fragment, container, false)
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this);
        loadAssetOverview()
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this);
    }

    private fun createViewModel(assetOverviewObserver: Observer<AssetOverview>): AssetOverviewViewModel {
        val viewModel by viewModels<AssetOverviewViewModel>()
        viewModel.init(this, assetOverviewObserver)
        return viewModel
    }

    private fun loadAssetOverview() {
        val viewModel by viewModels<AssetOverviewViewModel>()
        viewModel.loadAssetOverview()
    }

    private fun onAssetOverviewLoaded(assetOverview: AssetOverview) {
        assetOverviewFragmentTotalBtcValue.text = "${assetOverview.btcValue.format("BTC")} BTC"
        assetOverviewFragmentTotalUsdValue.text = "${assetOverview.usdValue.format("USD")} USD"
    }

    @Suppress("UNUSED_PARAMETER")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: AssinWorkerEvents.Complete) {
        loadAssetOverview()
    }

    @Suppress("UNUSED_PARAMETER")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: AssinWorkerEvents.Observed) {
        loadAssetOverview()
    }

    @Suppress("UNUSED_PARAMETER")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: TradeEvents.Insert) {
        loadAssetOverview()
    }

    @Suppress("UNUSED_PARAMETER")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: TradeEvents.Update) {
        loadAssetOverview()
    }

    @Suppress("UNUSED_PARAMETER")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: TradeEvents.Delete) {
        loadAssetOverview()
    }

    @Suppress("UNUSED_PARAMETER")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: TradeEvents.DeleteAll) {
        loadAssetOverview()
    }
}
