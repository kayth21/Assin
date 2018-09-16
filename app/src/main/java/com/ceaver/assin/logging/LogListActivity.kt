package com.ceaver.assin.logging

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import com.ceaver.assin.R
import kotlinx.android.synthetic.main.activity_log_list.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class LogListActivity : AppCompatActivity() {

    private val logListAdapter = LogListAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_list)
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this);
        logList.adapter = logListAdapter
        logList.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL)) // TODO Seriously?
        loadAllLogs()
        logSwipeRefreshLayout.setOnRefreshListener { loadAllLogs() }
    }

    private fun loadAllLogs() {
        LogRepository.loadAllLogsAsync(true) { onAllLogsLoaded(it.sortedByDescending { it.id }) }
    }

    private fun onAllLogsLoaded(logs: List<Log>) {
        logListAdapter.logList = logs; logListAdapter.notifyDataSetChanged(); logSwipeRefreshLayout.isRefreshing = false
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this);
        logList.adapter = null
        logSwipeRefreshLayout.setOnRefreshListener(null)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: LogEvents.Insert) {
        loadAllLogs()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: LogEvents.Update) {
        loadAllLogs()
    }

}
