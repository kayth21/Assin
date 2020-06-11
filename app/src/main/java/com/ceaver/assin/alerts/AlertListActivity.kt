package com.ceaver.assin.alerts

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.MenuItem
import com.ceaver.assin.R
import kotlinx.android.synthetic.main.activity_alert_list.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class AlertListActivity : AppCompatActivity() {

    private val alertListAdapter = AlertListAdapter(OnListItemClickListener())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alert_list)
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this);
        alertList.adapter = alertListAdapter
        alertList.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL)) // TODO Seriously?
        createAlertButton.setOnClickListener { startActivity(Intent(this, AlertInputActivity::class.java)) }
        loadAllAlerts()
        alertSwipeRefreshLayout.setOnRefreshListener { loadAllAlerts() }
    }

    private fun loadAllAlerts() {
        AlertRepository.loadAllAlertsAsync(true) { onAllAlertsLoaded(it) }
    }

    private fun onAllAlertsLoaded(alerts: List<Alert>) {
        alertListAdapter.alertList = alerts; alertListAdapter.notifyDataSetChanged(); alertSwipeRefreshLayout.isRefreshing = false
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this);
        alertList.adapter = null
        createAlertButton.setOnClickListener(null)
        alertSwipeRefreshLayout.setOnRefreshListener(null)
    }

    @Suppress("UNUSED_PARAMETER")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: AlertEvents.Delete) {
        loadAllAlerts()
    }

    @Suppress("UNUSED_PARAMETER")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: AlertEvents.Insert) {
        loadAllAlerts()
    }

    @Suppress("UNUSED_PARAMETER")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: AlertEvents.Update) {
        loadAllAlerts()
    }

    interface OnItemClickListener {
        fun onItemClick(item: Alert)
    }

    private inner class OnListItemClickListener : OnItemClickListener {
        override fun onItemClick(item: Alert) {
            val intent = Intent(this@AlertListActivity, AlertInputActivity::class.java);
            intent.putExtra(AlertInputActivity.INTENT_EXTRA_ALERT_ID, item.id)
            startActivity(intent)
        }
    }

    override fun onContextItemSelected(item: MenuItem?): Boolean {
        val selectedAlert = alertListAdapter.currentLongClickAlert!!
        AlertRepository.deleteAlertAsync(selectedAlert)
        return super.onContextItemSelected(item)
    }
}
