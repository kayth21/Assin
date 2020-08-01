package com.ceaver.assin.alerts

import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ceaver.assin.R
import kotlinx.android.synthetic.main.alert_list_row.view.*

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
            view.alertSymbolTextView.text = alert.symbol.toString()
            view.alertLowerTargetTextView.text = "Lower Target: " + alert.source.minus(alert.target).toPlainString() + " ${alert.reference.symbol}"
            view.alertUpperTargetTextView.text = "Upper Target: " + alert.source.plus(alert.target).toPlainString() + " ${alert.reference.symbol}"
            view.alertRangeTextView.text = "Range (+/-): "+ alert.target.toPlainString() + " ${alert.reference.symbol}"
            view.setOnCreateContextMenuListener(this)
            itemView.setOnClickListener { onClickListener.onItemClick(alert) }
        }
    }
}