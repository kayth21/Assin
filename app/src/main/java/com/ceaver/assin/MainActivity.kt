package com.ceaver.assin

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.work.*
import com.ceaver.assin.alerts.AlertListActivity
import com.ceaver.assin.backup.BackupActivity
import com.ceaver.assin.home.HomeFragmentDirections
import com.ceaver.assin.logging.LogListActivity
import com.ceaver.assin.logging.LogRepository
import com.ceaver.assin.util.isCharging
import com.ceaver.assin.util.isConnected
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.home_fragment.*
import kotlinx.android.synthetic.main.main_activity.*
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        drawerLayout = main_activity_drawer_layout
        navController = this.findNavController(R.id.main_activity_nav_host_fragment)

        NavigationUI.setupWithNavController(main_activity_navigation_view, navController)
        NavigationUI.setupActionBarWithNavController(this, navController, drawerLayout)

        main_activity_navigation_view.setNavigationItemSelectedListener(this)
    }

    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(navController, drawerLayout)
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
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

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.marketFragment -> {
                home_fragment_view_pager.setCurrentItem(0, true)
            }
            R.id.nav_assets -> {
                home_fragment_view_pager.setCurrentItem(1, true)
            }
            R.id.nav_intensions -> {
                home_fragment_view_pager.setCurrentItem(2, true)
            }
            R.id.nav_actions -> {
                navController.navigate(HomeFragmentDirections.actionHomeFragmentToActionListFragment())
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
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
}