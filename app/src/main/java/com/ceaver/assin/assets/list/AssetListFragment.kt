package com.ceaver.assin.assets.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.ceaver.assin.AssinWorkerEvents
import com.ceaver.assin.AssinWorkers
import com.ceaver.assin.action.ActionEvents
import com.ceaver.assin.action.ActionType
import com.ceaver.assin.assets.Asset
import com.ceaver.assin.assets.AssetRepository
import com.ceaver.assin.home.HomeFragmentDirections
import com.ceaver.assin.util.isConnected
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_asset_list.*
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class AssetListFragment : Fragment() {

    private val assetListAdapter = AssetListAdapter(OnListItemClickListener())

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(com.ceaver.assin.R.layout.fragment_asset_list, container, false)
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this);
        assetList.adapter = assetListAdapter
        assetList.addItemDecoration(DividerItemDecoration(requireActivity().application, LinearLayoutManager.VERTICAL)) // TODO Seriously?
        assetDepositButton.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToActionInputFragment(ActionType.DEPOSIT))
        }
        assetSwipeRefreshLayout.setOnRefreshListener {
            if (isConnected())
                lifecycleScope.launch { AssinWorkers.observedUpdate() }
            else {
                Snackbar.make(marketFrameLayout, "no internet connection", Snackbar.LENGTH_LONG).show(); assetSwipeRefreshLayout.isRefreshing = false
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
    fun onMessageEvent(event: ActionEvents.DeleteAll) {
        refreshAllAssets()
    }

    @Suppress("UNUSED_PARAMETER")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: ActionEvents.Delete) {
        refreshAllAssets()
    }

    @Suppress("UNUSED_PARAMETER")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: ActionEvents.Insert) {
        refreshAllAssets()
    }

    @Suppress("UNUSED_PARAMETER")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: ActionEvents.Update) {
        refreshAllAssets()
    }

    private fun refreshAllAssets() {
        loadAllAssets()
    }

    private fun loadAllAssets() {
        lifecycleScope.launch {
            val assets = AssetRepository.loadAllAssets()
            onAllAssetsLoaded(assets)
        }
    }

    private fun onAllAssetsLoaded(assets: List<Asset>) {
        assetListAdapter.assets = assets.toMutableList().sortedBy { it.btcValue }.reversed(); assetListAdapter.notifyDataSetChanged(); assetSwipeRefreshLayout?.isRefreshing = false
    }

    interface OnItemClickListener {
        fun onItemClick(item: Asset)
    }

    private inner class OnListItemClickListener : OnItemClickListener {
        override fun onItemClick(item: Asset) {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToAssetDetailFragment(item.title))
        }
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
                    val actionType = when (item.itemId) {
                        AssetListAdapter.CONTEXT_MENU_DEPOSIT_ITEM_ID -> ActionType.DEPOSIT
                        AssetListAdapter.CONTEXT_MENU_WITHDRAW_ITEM_ID -> ActionType.WITHDRAW
                        else -> throw IllegalStateException()
                    }
                    findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToActionInputFragment(actionType, null, selectedAsset.title))
                }
                item.itemId == AssetListAdapter.CONTEXT_MENU_INTENTION_ITEM_ID -> {
                    findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToIntentionInputFragment(null, selectedAsset.title, selectedAsset.amount))
                }
                else -> throw IllegalStateException()
            }
        }
        return super.onContextItemSelected(item)
    }
}