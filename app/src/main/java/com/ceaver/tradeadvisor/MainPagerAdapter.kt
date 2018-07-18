package com.ceaver.tradeadvisor

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import com.ceaver.tradeadvisor.advices.AdviceListFragment
import com.ceaver.tradeadvisor.trades.TradeListFragment

class MainPagerAdapter(fm: FragmentManager): FragmentStatePagerAdapter(fm) {

    override fun getItem(position: Int): Fragment? {
        when (position) {
            0 -> return TradeListFragment()
            1 -> return AdviceListFragment()
        }
        return null
    }

    override fun getCount(): Int {
        return 2
    }

    override fun getPageTitle(position: Int): CharSequence? {
        when (position) {
            0 -> return "Trades"
            1 -> return "Advices"
            else -> return null
        }
    }
}