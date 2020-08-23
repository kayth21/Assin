package com.ceaver.assin.assets.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ceaver.assin.R
import com.ceaver.assin.assets.detail.overview.AssetDetailOverviewFragment
import com.ceaver.assin.positions.list.PositionListFragment


class AssetDetailFragment : Fragment() {

    private lateinit var arguments: AssetDetailFragmentArgs

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments = AssetDetailFragmentArgs.fromBundle(requireArguments())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.asset_detail_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val title = arguments.title
        insertFragment(R.id.assetDetailOverviewFragment, AssetDetailOverviewFragment(title))
        insertFragment(R.id.assetDetailPositionsFragment, PositionListFragment(title))
    }

    private fun insertFragment(position: Int, fragment: Fragment) {
        childFragmentManager.beginTransaction().replace(position, fragment).commit()
    }
}

