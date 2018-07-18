package com.ceaver.tradeadvisor

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.ceaver.tradeadvisor.R.id.tablayout
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.view.View
import com.ceaver.tradeadvisor.trades.Trade
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
        val myPagerAdapter = MainPagerAdapter(supportFragmentManager)
        viewPager.adapter = myPagerAdapter
        val tabLayout = findViewById<View>(R.id.tablayout) as TabLayout
        tabLayout.setupWithViewPager(viewPager)
    }
}
