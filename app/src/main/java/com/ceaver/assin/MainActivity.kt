package com.ceaver.assin

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.ceaver.assin.trades.TradeListFragment

class MainActivity : AppCompatActivity() {

    init {
        com.ceaver.assin.engine.TradeAdviceEngine.wakeup()
        com.ceaver.assin.TestdataProvider.cleanDatabaseAndInsertSomeDataAfterwards() // TODO Replace with EventBus.getDefault().post(EngineEvents.Run())
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
                1 -> com.ceaver.assin.advices.AdviceListFragment()
                else -> null
            }
        }
    }
}
