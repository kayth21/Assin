package com.ceaver.assin.markets

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ceaver.assin.R
import com.ceaver.assin.markets.list.MarketListFragment
import com.ceaver.assin.markets.overview.MarketOverviewFragment


class MarketFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(com.ceaver.assin.R.layout.market_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        insertFragment(R.id.marketFragmentMarketOverviewFragment, MarketOverviewFragment())
        insertFragment(R.id.marketFragmentMarketListFragment, MarketListFragment())
    }

    private fun insertFragment(position: Int, fragment: Fragment) {
        childFragmentManager.beginTransaction().replace(position, fragment).commit()
    }
}
