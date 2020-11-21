package com.ceaver.assin.assets.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
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

        viewModel.assets.observe(viewLifecycleOwner) { assetListAdapter.submitList(it.sortedBy { it.valueCrypto }.reversed()) }

        return binding.root
    }

    interface OnItemClickListener {
        fun onItemClick(item: Asset)
    }

    private inner class OnListItemClickListener : OnItemClickListener {
        override fun onItemClick(item: Asset) {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToAssetDetailFragment(item.title, item.label))
        }
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val selectedAsset = assetListAdapter.currentLongClickAsset!!
        when (item.itemId) {
            in setOf(AssetListAdapter.MENU_ITEM_DEPOSIT, AssetListAdapter.MENU_ITEM_WITHDRAW) -> {
                val actionType = when (item.itemId) {
                    AssetListAdapter.MENU_ITEM_DEPOSIT -> ActionType.DEPOSIT
                    AssetListAdapter.MENU_ITEM_WITHDRAW -> ActionType.WITHDRAW
                    else -> throw IllegalStateException()
                }
                findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToActionInputFragment(actionType, null, selectedAsset.title))
            }
            AssetListAdapter.MENU_ITEM_INTENTION -> {
                findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToIntentionInputFragment(null, selectedAsset.title, selectedAsset.quantity))
            }
            else -> throw IllegalStateException()
        }
        return super.onContextItemSelected(item)
    }
}