package com.ceaver.assin.assets.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.ceaver.assin.R
import com.ceaver.assin.action.ActionType
import com.ceaver.assin.assets.Asset
import com.ceaver.assin.databinding.AssetListFragmentBinding
import com.ceaver.assin.home.HomeFragmentDirections

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
        binding.assetDepositButton.setOnClickListener { findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToActionInputFragment(ActionType.DEPOSIT)) }
        binding.assetList.addItemDecoration(DividerItemDecoration(requireActivity().application, LinearLayoutManager.VERTICAL))

        viewModel.assets.observe(viewLifecycleOwner, Observer { assetListAdapter.submitList(it.sortedBy { it.valueCrypto }.reversed()) })

        return binding.root
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