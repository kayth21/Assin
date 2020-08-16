package com.ceaver.assin.assets.overview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.ceaver.assin.AssinWorkerEvents
import com.ceaver.assin.R
import com.ceaver.assin.action.ActionEvents
import com.ceaver.assin.extensions.toCurrencyString
import kotlinx.android.synthetic.main.asset_overview_fragment.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class AssetOverviewFragment : Fragment() {

    private lateinit var viewModel: AssetOverviewViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = viewModels<AssetOverviewViewModel>().value
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewModel.assetOverview.observe(viewLifecycleOwner, Observer { onAssetOverviewLoaded(it!!) })
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

    private fun loadAssetOverview() {
        val viewModel by viewModels<AssetOverviewViewModel>()
        viewModel.loadAssetOverview()
    }

    private fun onAssetOverviewLoaded(assetOverview: AssetOverview) {
        assetOverviewFragmentTotalBtcValue.text = "${assetOverview.btcValue.toCurrencyString("BTC")} BTC"
        assetOverviewFragmentTotalUsdValue.text = "${assetOverview.usdValue.toCurrencyString("USD")} USD"
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
    fun onMessageEvent(event: ActionEvents.Insert) {
        loadAssetOverview()
    }

    @Suppress("UNUSED_PARAMETER")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: ActionEvents.Update) {
        loadAssetOverview()
    }

    @Suppress("UNUSED_PARAMETER")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: ActionEvents.Delete) {
        loadAssetOverview()
    }

    @Suppress("UNUSED_PARAMETER")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: ActionEvents.DeleteAll) {
        loadAssetOverview()
    }
}
