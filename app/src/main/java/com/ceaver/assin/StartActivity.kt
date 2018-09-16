package com.ceaver.assin

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.GravityCompat
import android.support.v4.view.ViewPager
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.ceaver.assin.advices.AdviceListFragment
import com.ceaver.assin.alerts.AlertListActivity
import com.ceaver.assin.assets.AssetListFragment
import com.ceaver.assin.engine.TradeAdviceEngine
import com.ceaver.assin.logging.LogListActivity
import com.ceaver.assin.markets.MarketListFragment
import com.ceaver.assin.trades.TradeListFragment
import kotlinx.android.synthetic.main.activity_start.*
import kotlinx.android.synthetic.main.app_bar_start.*

class StartActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    init {
        TradeAdviceEngine.wakeup()
        TestdataProvider.cleanDatabaseAndInsertSomeDataAfterwards() // TODO Replace with EventBus.getDefault().post(EngineEvents.Run())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)
        setSupportActionBar(toolbar)

        val viewPager = findViewById<View>(R.id.pager) as ViewPager
        viewPager.adapter = StartActivity.MainPagerAdapter(supportFragmentManager)
        val tabLayout = findViewById<View>(R.id.tablayout) as TabLayout
        tabLayout.setupWithViewPager(viewPager)

        val toggle = ActionBarDrawerToggle(this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.start, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_settings -> return true
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_markets -> {
                val viewPager = findViewById<View>(R.id.pager) as ViewPager
                viewPager.setCurrentItem(0, true)
            }
            R.id.nav_assets -> {
                val viewPager = findViewById<View>(R.id.pager) as ViewPager
                viewPager.setCurrentItem(1, true)

            }
            R.id.nav_tasks -> {
                val viewPager = findViewById<View>(R.id.pager) as ViewPager
                viewPager.setCurrentItem(2, true)

            }
            R.id.nav_trades -> {
                val viewPager = findViewById<View>(R.id.pager) as ViewPager
                viewPager.setCurrentItem(3, true)

            }
            R.id.nav_alerts -> {
                val intent = Intent(this, AlertListActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_settings -> {
                Toast.makeText(applicationContext, "No Implementation", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_import_export -> {
                Toast.makeText(applicationContext, "No Implementation", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_chat -> {
                Toast.makeText(applicationContext, "No Implementation", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_forum -> {
                Toast.makeText(applicationContext, "No Implementation", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_contact -> {
                Toast.makeText(applicationContext, "No Implementation", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_donate -> {
                Toast.makeText(applicationContext, "No Implementation", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_logging -> {
                val intent = Intent(this, LogListActivity::class.java)
                startActivity(intent)
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }


    private class MainPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

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
