package com.ceaver.assin

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.core.view.GravityCompat
import androidx.viewpager.widget.ViewPager
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.fragment.app.FragmentPagerAdapter
import androidx.work.*
import com.ceaver.assin.alerts.AlertListActivity
import com.ceaver.assin.assets.AssetFragment
import com.ceaver.assin.backup.BackupActivity
import com.ceaver.assin.intentions.IntentionListFragment
import com.ceaver.assin.logging.LogListActivity
import com.ceaver.assin.logging.LogRepository
import com.ceaver.assin.markets.MarketFragment
import com.ceaver.assin.trades.TradeListFragment
import com.ceaver.assin.util.isCharging
import com.ceaver.assin.util.isConnected
import kotlinx.android.synthetic.main.activity_start.*
import kotlinx.android.synthetic.main.app_bar_start.*
import java.util.concurrent.TimeUnit

class StartActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

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

    override fun onStart() {
        super.onStart()
        val backgroundProcess = PeriodicWorkRequestBuilder<StartWorker>(15, TimeUnit.MINUTES, 5, TimeUnit.MINUTES).build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork("UNIQUE", ExistingPeriodicWorkPolicy.KEEP, backgroundProcess)
    }

    class StartWorker(appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams) {
        override fun doWork(): Result {
            if (isConnected())
                if (isCharging())
                    AssinWorkers.completeUpdate()
                else
                    AssinWorkers.observedUpdate()
            else
                LogRepository.insertLog("update skipped because of missing connection")
            return Result.success()
        }
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
            R.id.nav_intensions -> {
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
            R.id.nav_backup -> {
                val intent = Intent(this, BackupActivity::class.java)
                startActivity(intent)
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


    private class MainPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

        override fun getCount(): Int {
            return 4
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return when (position) {
                0 -> "Markets"
                1 -> "Assets"
                2 -> "Intentions"
                3 -> "Trades"
                else -> null
            }
        }

        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> MarketFragment()
                1 -> AssetFragment()
                2 -> IntentionListFragment()
                3 -> TradeListFragment()
                else -> null
            }!!
        }
    }
}
