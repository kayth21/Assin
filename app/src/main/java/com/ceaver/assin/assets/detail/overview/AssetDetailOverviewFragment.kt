package com.ceaver.assin.assets.detail.overview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.ceaver.assin.AssinWorkerEvents
import com.ceaver.assin.MyApplication
import com.ceaver.assin.R
import com.ceaver.assin.assets.Asset
import com.ceaver.assin.extensions.resIdByName
import com.ceaver.assin.extensions.toCurrencyString
import kotlinx.android.synthetic.main.asset_detail_overview_fragment.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class AssetDetailOverviewFragment(val symbol: String) : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lookupViewModel().init(this, Observer { onAssetLoaded(it!!) })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
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


    private fun lookupViewModel(): AssetDetailOverviewViewModel = ViewModelProviders.of(this).get(AssetDetailOverviewViewModel::class.java)

    private fun onAssetLoaded(asset: Asset) {
        assetDetailOverviewFragmentAssetImage.setImageResource(getImageIdentifier(asset.title.symbol))
        assetDetailOverviewFragmentAssetTitle.text = asset.title.name
        assetDetailOverviewFragmentTotalPositionSizeValue.text = asset.amount.toPlainString() + " " + asset.title.symbol
        assetDetailOverviewFragmentValueInBtcValue.text = asset.btcValue.toCurrencyString("BTC") + " BTC"
        assetDetailOverviewFragmentValueInUsdValue.text = asset.usdValue.toCurrencyString("USD") + " USD"
    }

    private fun getImageIdentifier(symbol: String): Int {
        val identifier = MyApplication.appContext!!.resIdByName(symbol.toLowerCase(), "drawable")
        return if (identifier == 0) R.drawable.unknown else identifier
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
        lookupViewModel().loadAsset(symbol)
    }
}
