package com.ceaver.assin.assets.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.ceaver.assin.AssinWorkerEvents
import com.ceaver.assin.R
import com.ceaver.assin.action.ActionType
import com.ceaver.assin.assets.Asset
import com.ceaver.assin.databinding.AssetListFragmentBinding
import com.ceaver.assin.home.HomeFragmentDirections
import kotlinx.android.synthetic.main.asset_list_fragment.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class AssetListFragment : Fragment() {

    private val assetListAdapter = AssetListAdapter(OnListItemClickListener())
    private lateinit var viewModel: AssetListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = viewModels<AssetListViewModel>().value
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding: AssetListFragmentBinding = DataBindingUtil.inflate(inflater, R.layout.asset_list_fragment, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        binding.assetList.adapter = assetListAdapter

        viewModel.assets.observe(viewLifecycleOwner, Observer { assetListAdapter.assets = it })

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this);
        assetList.addItemDecoration(DividerItemDecoration(requireActivity().application, LinearLayoutManager.VERTICAL)) // TODO Seriously?
        assetDepositButton.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToActionInputFragment(ActionType.DEPOSIT))
        }
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this);
        assetList.removeItemDecorationAt(0) // TODO Seriously?
        assetDepositButton.setOnClickListener(null)
    }

    @Suppress("UNUSED_PARAMETER")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: AssinWorkerEvents.Complete) {
        Toast.makeText(activity, "Markets refreshed", Toast.LENGTH_SHORT).show();
        assetSwipeRefreshLayout?.isRefreshing = false
    }

    @Suppress("UNUSED_PARAMETER")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: AssinWorkerEvents.Observed) {
        Toast.makeText(activity, "Observed refreshed", Toast.LENGTH_SHORT).show();
        assetSwipeRefreshLayout?.isRefreshing = false
    }

    interface OnItemClickListener {
        fun onItemClick(item: Asset)
    }

    private inner class OnListItemClickListener : OnItemClickListener {
        override fun onItemClick(item: Asset) {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToAssetDetailFragment(item.title))
        }
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