package com.ceaver.assin.logging

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.ceaver.assin.R
import kotlinx.android.synthetic.main.log_list_fragment.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class LogListFragment : Fragment() {

    private val logListAdapter = LogListAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.log_list_fragment, container, false)
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this);
        logList.adapter = logListAdapter
        logList.addItemDecoration(DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL)) // TODO Seriously?
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

    @Suppress("UNUSED_PARAMETER")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: LogEvents.Insert) {
        loadAllLogs()
    }

    @Suppress("UNUSED_PARAMETER")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: LogEvents.Update) {
        loadAllLogs()
    }
}
