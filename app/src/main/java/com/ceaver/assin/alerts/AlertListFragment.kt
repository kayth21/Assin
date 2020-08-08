package com.ceaver.assin.alerts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.ceaver.assin.R
import kotlinx.android.synthetic.main.alert_list_fragment.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class AlertListFragment : Fragment() {

    private val alertListAdapter = AlertListAdapter(OnListItemClickListener())

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.alert_list_fragment, container, false)
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this);
        alertList.adapter = alertListAdapter
        alertList.addItemDecoration(DividerItemDecoration(requireActivity(), LinearLayoutManager.VERTICAL)) // TODO Seriously?
        createAlertButton.setOnClickListener { findNavController().navigate(AlertListFragmentDirections.actionAlertListActivityToAlertInputFragment()) }
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
        override fun onItemClick(alert: Alert) {
            findNavController().navigate(AlertListFragmentDirections.actionAlertListActivityToAlertInputFragment(alert))
        }
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val selectedAlert = alertListAdapter.currentLongClickAlert!!
        AlertRepository.deleteAlertAsync(selectedAlert)
        return super.onContextItemSelected(item)
    }
}
