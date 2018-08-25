package com.ceaver.assin

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.ceaver.assin.advices.AdviceListFragment
import com.ceaver.assin.assets.AssetListFragment
import com.ceaver.assin.engine.TradeAdviceEngine
import com.ceaver.assin.markets.MarketEngine
import com.ceaver.assin.markets.MarketEngineEvents
import com.ceaver.assin.markets.MarketListFragment
import com.ceaver.assin.trades.TradeListFragment
import org.greenrobot.eventbus.EventBus

class MainActivity : AppCompatActivity() {

    init {
        TradeAdviceEngine.wakeup()
        MarketEngine.wakeup()
        TestdataProvider.cleanDatabaseAndInsertSomeDataAfterwards() // TODO Replace with EventBus.getDefault().post(EngineEvents.Run())
        EventBus.getDefault().post(MarketEngineEvents.Run())
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
            return 4
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return when (position) {
                0 -> "Markets"
                1 -> "Assets"
                2 -> "Tasks"
                3 -> "Trades"
                else -> null
            }
        }

        override fun getItem(position: Int): Fragment? {
            return when (position) {
                0 -> MarketListFragment()
                1 -> AssetListFragment()
                2 -> AdviceListFragment()
                3 -> TradeListFragment()
                else -> null
            }
        }
    }
}
