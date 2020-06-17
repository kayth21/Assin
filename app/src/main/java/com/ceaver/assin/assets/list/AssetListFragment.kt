package com.ceaver.assin.assets.list

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import com.ceaver.assin.AssinWorkerEvents
import com.ceaver.assin.AssinWorkers
import com.ceaver.assin.assets.Asset
import com.ceaver.assin.assets.AssetRepository
import com.ceaver.assin.intentions.input.IntentionInputFragment
import com.ceaver.assin.threading.BackgroundThreadExecutor
import com.ceaver.assin.trades.Trade
import com.ceaver.assin.trades.TradeEvents
import com.ceaver.assin.trades.TradeType
import com.ceaver.assin.trades.input.TradeInputFragment
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
        assetList.addItemDecoration(DividerItemDecoration(requireActivity().application, LinearLayoutManager.VERTICAL)) // TODO Seriously?
        assetDepositButton.setOnClickListener {
            var arguments = Bundle();
            arguments.putString(Trade.TRADE_TYPE, TradeType.DEPOSIT.name)
            val tradeInputFragment = TradeInputFragment()
            tradeInputFragment.arguments = arguments
            tradeInputFragment.show(parentFragmentManager, TradeInputFragment.TRADE_INPUT_FRAGMENT_TAG)
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

    @Suppress("UNUSED_PARAMETER")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: AssinWorkerEvents.Complete) {
        refreshAllAssets()
    }

    @Suppress("UNUSED_PARAMETER")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: AssinWorkerEvents.Observed) {
        refreshAllAssets()
    }

    @Suppress("UNUSED_PARAMETER")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: TradeEvents.DeleteAll) {
        refreshAllAssets()
    }

    @Suppress("UNUSED_PARAMETER")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: TradeEvents.Delete) {
        refreshAllAssets()
    }

    @Suppress("UNUSED_PARAMETER")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: TradeEvents.Insert) {
        refreshAllAssets()
    }

    @Suppress("UNUSED_PARAMETER")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: TradeEvents.Update) {
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

    override fun onContextItemSelected(item: MenuItem): Boolean {
        if (item.groupId == AssetListAdapter.CONTEXT_MENU_GROUP_ID) {
            val selectedAsset = assetListAdapter.currentLongClickAsset!!
            when {
                item.itemId in setOf(AssetListAdapter.CONTEXT_MENU_DEPOSIT_ITEM_ID, AssetListAdapter.CONTEXT_MENU_WITHDRAW_ITEM_ID) -> {
                    val arguments = Bundle();
                    arguments.putString(Trade.SYMBOL, selectedAsset.symbol)
                    arguments.putString(Trade.TRADE_TYPE, when (item.itemId) {
                        AssetListAdapter.CONTEXT_MENU_DEPOSIT_ITEM_ID -> TradeType.DEPOSIT.name
                        AssetListAdapter.CONTEXT_MENU_WITHDRAW_ITEM_ID -> TradeType.WITHDRAW.name
                        else -> throw IllegalStateException()
                    })
                    val tradeInputFragment = TradeInputFragment()
                    tradeInputFragment.arguments = arguments
                    tradeInputFragment.show(parentFragmentManager, TradeInputFragment.TRADE_INPUT_FRAGMENT_TAG)
                }
                item.itemId == AssetListAdapter.CONTEXT_MENU_INTENTION_ITEM_ID -> {
                    val arguments = Bundle();
                    arguments.putString(IntentionInputFragment.INTENTION_SYMBOL, selectedAsset.symbol)
                    arguments.putString(IntentionInputFragment.INTENTION_AMOUNT, selectedAsset.amount.toString())
                    val intentionInputFragment = IntentionInputFragment()
                    intentionInputFragment.arguments = arguments
                    intentionInputFragment.show(parentFragmentManager, TradeInputFragment.TRADE_INPUT_FRAGMENT_TAG)
                }
                else -> throw IllegalStateException()
            }
        }
        return super.onContextItemSelected(item)
    }
}