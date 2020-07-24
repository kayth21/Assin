package com.ceaver.assin.assets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ceaver.assin.R
import com.ceaver.assin.assets.list.AssetListFragment
import com.ceaver.assin.assets.overview.AssetOverviewFragment

class AssetFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.asset_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        insertFragment(R.id.assetFragmentAssetOverviewFragment, AssetOverviewFragment())
        insertFragment(R.id.assetFragmentAssetListFragment, AssetListFragment())
    }

    private fun insertFragment(position: Int, fragment: Fragment) {
        childFragmentManager.beginTransaction().replace(position, fragment).commit()
    }
}
