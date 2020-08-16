package com.ceaver.assin.assets.detail.overview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.ceaver.assin.AssinWorkerEvents
import com.ceaver.assin.R
import com.ceaver.assin.assets.Asset
import com.ceaver.assin.extensions.toCurrencyString
import com.ceaver.assin.markets.Title
import kotlinx.android.synthetic.main.asset_detail_overview_fragment.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class AssetDetailOverviewFragment(val title: Title) : Fragment() {

    private lateinit var viewModel: AssetDetailOverviewViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = viewModels<AssetDetailOverviewViewModel>().value
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewModel.asset.observe(viewLifecycleOwner,  Observer { onAssetLoaded(it!!) })
        return inflater.inflate(R.layout.asset_detail_overview_fragment, container, false)
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this);
        loadAsset()
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    private fun onAssetLoaded(asset: Asset) {
        assetDetailOverviewFragmentAssetImage.setImageResource(asset.title.getIcon())
        assetDetailOverviewFragmentAssetTitle.text = asset.title.name
        assetDetailOverviewFragmentTotalPositionSizeValue.text = asset.amount.toPlainString() + " " + asset.title.symbol
        assetDetailOverviewFragmentValueInBtcValue.text = asset.btcValue.toCurrencyString("BTC") + " BTC"
        assetDetailOverviewFragmentValueInUsdValue.text = asset.usdValue.toCurrencyString("USD") + " USD"
    }

    @Suppress("UNUSED_PARAMETER")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: AssinWorkerEvents.Complete) {
        loadAsset()
    }

    @Suppress("UNUSED_PARAMETER")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: AssinWorkerEvents.Observed) {
        loadAsset()
    }

    private fun loadAsset() {
        viewModel.loadAsset(title)
    }
}
