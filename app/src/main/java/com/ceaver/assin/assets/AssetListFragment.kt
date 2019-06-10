package com.ceaver.assin.assets

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import com.ceaver.assin.AssinWorkerEvents
import com.ceaver.assin.AssinWorkers
import com.ceaver.assin.IntentKeys
import com.ceaver.assin.threading.BackgroundThreadExecutor
import com.ceaver.assin.trades.TradeInputActivity
import com.ceaver.assin.trades.TradeType
import com.ceaver.assin.util.isConnected
import kotlinx.android.synthetic.main.fragment_asset_list.*
import kotlinx.android.synthetic.main.fragment_asset_list.marketFrameLayout
import kotlinx.android.synthetic.main.fragment_market_list.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class AssetListFragment : Fragment() {

    private val assetListAdapter = AssetListAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(com.ceaver.assin.R.layout.fragment_asset_list, container, false)
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this);
        assetList.adapter = assetListAdapter
        assetList.addItemDecoration(DividerItemDecoration(activity!!.application, LinearLayoutManager.VERTICAL)) // TODO Seriously?
        assetDepositButton.setOnClickListener {
            val intent = Intent(activity!!.application, TradeInputActivity::class.java);
            intent.putExtra(IntentKeys.TRADE_TYPE, TradeType.DEPOSIT.toString())
            startActivity(intent)
        }
        assetSwipeRefreshLayout.setOnRefreshListener {
            if (isConnected())
                BackgroundThreadExecutor.execute { AssinWorkers.observedUpdate() }
            else {
                Snackbar.make(marketFrameLayout, "no internet connection", Snackbar.LENGTH_LONG).show(); marketSwipeRefreshLayout.isRefreshing = false
            }
        }
        loadAllAssets()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: AssinWorkerEvents.Complete) {
        refreshAllAssets()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: AssinWorkerEvents.Observed) {
        refreshAllAssets()
    }

    private fun refreshAllAssets() {
        loadAllAssets()
    }

    private fun loadAllAssets() {
        AssetRepository.loadAllAssetsAsync(true) { onAllAssetsLoaded(it) };
    }

    private fun onAllAssetsLoaded(assets: List<Asset>) {
        assetListAdapter.assets = assets; assetListAdapter.notifyDataSetChanged(); assetSwipeRefreshLayout.isRefreshing = false
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this);
        assetList.adapter = null
        assetSwipeRefreshLayout.setOnRefreshListener(null)
    }

    override fun onContextItemSelected(item: MenuItem?): Boolean {
        if (item!!.groupId == 1) {
            val selectedAsset = assetListAdapter.currentLongClickAsset!!
            val intent = Intent(activity!!.application, TradeInputActivity::class.java);
            intent.putExtra(IntentKeys.SYMBOL, selectedAsset.symbol)
            intent.putExtra(IntentKeys.TRADE_TYPE, when (item.itemId) {
                0 -> TradeType.DEPOSIT.toString()
                1 -> TradeType.WITHDRAW.toString()
                else -> throw IllegalStateException()
            })
            startActivity(intent)
        }
        return super.onContextItemSelected(item)
    }
}