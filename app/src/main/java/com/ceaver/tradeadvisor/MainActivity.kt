package com.ceaver.tradeadvisor

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.ViewPager
import android.view.View
import com.ceaver.tradeadvisor.advices.AdviceListFragment
import com.ceaver.tradeadvisor.trades.Trade
import com.ceaver.tradeadvisor.trades.list.TradeListFragment
import com.ceaver.tradeadvisor.trades.TradeRepository
import java.util.*


class MainActivity : AppCompatActivity() {

    init {
        TradeRepository.deleteAllTrades() // TODO Temporary Helper - Remove!
        TradeRepository.insertTrade(Trade(0,1, Date(), 5.0, 5.0)) // TODO Temporary Helper - Remove!
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val viewPager = findViewById<View>(R.id.pager) as ViewPager
        viewPager.adapter = MainPagerAdapter(supportFragmentManager)
        val tabLayout = findViewById<View>(R.id.tablayout) as TabLayout
        tabLayout.setupWithViewPager(viewPager)
    }

    private class MainPagerAdapter(fm: FragmentManager): FragmentStatePagerAdapter(fm) {

        override fun getCount(): Int {
            return 2
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return when (position) {
                0 -> "Trades"
                1 -> "Advices"
                else -> null
            }
        }
        override fun getItem(position: Int): Fragment? {
            return when (position) {
                0 -> TradeListFragment()
                1 -> AdviceListFragment()
                else -> null
            }
        }
    }
}
