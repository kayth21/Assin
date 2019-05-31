package com.ceaver.assin.assets

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_asset_list.*

class AssetListFragment : Fragment() {

    private val assetListAdapter = AssetListAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(com.ceaver.assin.R.layout.fragment_asset_list, container, false)
    }

    override fun onStart() {
        super.onStart()
        assetList.adapter = assetListAdapter
        assetList.addItemDecoration(DividerItemDecoration(activity!!.application, LinearLayoutManager.VERTICAL)) // TODO Seriously?
        assetSwipeRefreshLayout.setOnRefreshListener { refreshAllAssets() }
        loadAllAssets()
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
        assetList.adapter = null
        assetSwipeRefreshLayout.setOnRefreshListener(null)
    }

}