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
import com.ceaver.assin.intentions.IntentionInputActivity
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
            intent.putExtra(TradeInputActivity.INTENT_EXTRA_TRADE_TYPE, TradeType.DEPOSIT.toString())
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
        assetListAdapter.assets = assets.toMutableList().sortedBy { it.btcValue }.reversed(); assetListAdapter.notifyDataSetChanged(); assetSwipeRefreshLayout.isRefreshing = false
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this);
        assetList.adapter = null
        assetList.removeItemDecorationAt(0) // TODO Seriously?
        assetSwipeRefreshLayout.setOnRefreshListener(null)
    }

    override fun onContextItemSelected(item: MenuItem?): Boolean {
        if (item!!.groupId == AssetListAdapter.CONTEXT_MENU_GROUP_ID) {
            val selectedAsset = assetListAdapter.currentLongClickAsset!!
            when {
                item.itemId in setOf(AssetListAdapter.CONTEXT_MENU_DEPOSIT_ITEM_ID, AssetListAdapter.CONTEXT_MENU_WITHDRAW_ITEM_ID) -> {
                    val intent = Intent(activity!!.application, TradeInputActivity::class.java);
                    intent.putExtra(TradeInputActivity.INTENT_EXTRA_TRADE_SYMBOL, selectedAsset.symbol)
                    intent.putExtra(TradeInputActivity.INTENT_EXTRA_TRADE_TYPE, when (item.itemId) {
                        AssetListAdapter.CONTEXT_MENU_DEPOSIT_ITEM_ID -> TradeType.DEPOSIT.toString()
                        AssetListAdapter.CONTEXT_MENU_WITHDRAW_ITEM_ID -> TradeType.WITHDRAW.toString()
                        else -> throw IllegalStateException()
                    })
                    startActivity(intent)
                }
                item.itemId == AssetListAdapter.CONTEXT_MENU_INTENTION_ITEM_ID -> {
                    val intent = Intent(activity!!.application, IntentionInputActivity::class.java);
                    intent.putExtra(IntentionInputActivity.INTENT_EXTRA_INTENTION_SYMBOL, selectedAsset.symbol)
                    intent.putExtra(IntentionInputActivity.INTENT_EXTRA_INTENTION_AMOUNT, selectedAsset.amount)
                    startActivity(intent)
                }
                else -> throw IllegalStateException()
            }
        }
        return super.onContextItemSelected(item)
    }
}