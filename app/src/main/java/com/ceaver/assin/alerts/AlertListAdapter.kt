package com.ceaver.assin.alerts

import android.support.v7.widget.RecyclerView
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.ceaver.assin.R
import com.ceaver.assin.extensions.format

internal class AlertListAdapter(private val onClickListener: AlertListActivity.OnItemClickListener) : RecyclerView.Adapter<AlertListAdapter.ViewHolder>() {

    var alertList: List<Alert> = ArrayList()
    var currentLongClickAlert: Alert? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.alert_list_row, parent, false))
    }

    override fun onBindViewHolder(holder: AlertListAdapter.ViewHolder, position: Int) {
        holder.bindItem(alertList[position], onClickListener)
        holder.itemView.setOnLongClickListener { currentLongClickAlert = alertList[position]; false }
    }

    override fun getItemCount() = alertList.size

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view), View.OnCreateContextMenuListener {

        override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
            menu!!.add(0, v!!.getId(), 0, "Delete")
        }

        fun bindItem(alert: Alert, onClickListener: AlertListActivity.OnItemClickListener) {
            (view.findViewById(R.id.alertSymbolTextView) as TextView).text = "${alert.symbol} (${alert.symbol}/${alert.reference})"
            (view.findViewById(R.id.alertLowerTargetTextView) as TextView).text = "Lower Target: " + (alert.source - alert.target).format(alert.reference) + " ${alert.reference}"
            (view.findViewById(R.id.alertUpperTargetTextView) as TextView).text = "Upper Target: " + (alert.source + alert.target).format(alert.reference) + " ${alert.reference}"
            (view.findViewById(R.id.alertRangeTextView) as TextView).text = "Range (+/-): "+ alert.target.format(alert.reference) + " ${alert.reference}"
            view.setOnCreateContextMenuListener(this)
            itemView.setOnClickListener { onClickListener.onItemClick(alert) }
        }
    }
}