package com.ceaver.assin.assets.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.ceaver.assin.R
import com.ceaver.assin.assets.detail.assets.AssetDetailAssetsFragment
import com.ceaver.assin.assets.detail.overview.AssetDetailOverviewFragment


class AssetDetailFragment : DialogFragment() {

    companion object {
        const val FRAGMENT_TAG = "com.ceaver.assin.assets.detail.AssetDetailFragment.Tag"
        const val SYMBOL =       "com.ceaver.assin.assets.detail.AssetDetailFragment.Symbol"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.asset_detail_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val symbol = lookupSymbol()
        insertFragment(R.id.assetDetailOverviewFragment, AssetDetailOverviewFragment(symbol))
        insertFragment(R.id.assetDetailAssetsFragment, AssetDetailAssetsFragment(symbol))
    }

    private fun lookupSymbol(): String = requireArguments().getString(SYMBOL)!!

    private fun insertFragment(position: Int, fragment: Fragment) {
        childFragmentManager.beginTransaction().replace(position, fragment).commit()
    }
}

