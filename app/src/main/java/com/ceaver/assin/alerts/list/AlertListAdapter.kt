package com.ceaver.assin.alerts.list

import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ceaver.assin.R
import com.ceaver.assin.alerts.Alert
import kotlinx.android.synthetic.main.alert_list_row.view.*

internal class AlertListAdapter(private val onClickListener: AlertListFragment.OnItemClickListener) : RecyclerView.Adapter<AlertListAdapter.ViewHolder>() {

    var alerts = listOf<Alert>()
        set(value) {
            field = value
            notifyDataSetChanged();
        }
    var currentLongClickAlert: Alert? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.alert_list_row, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(alerts[position], onClickListener)
        holder.itemView.setOnLongClickListener { currentLongClickAlert = alerts[position]; false }
    }

    override fun getItemCount() = alerts.size

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view), View.OnCreateContextMenuListener {

        override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
            menu!!.add(0, v!!.getId(), 0, "Delete")
        }

        fun bindItem(alert: Alert, onClickListener: AlertListFragment.OnItemClickListener) {
            view.alertSymbolTextView.text = alert.symbol.toString()
            view.alertLowerTargetTextView.text = "Lower Target: " + alert.source.minus(alert.target).toPlainString() + " ${alert.reference.symbol}"
            view.alertUpperTargetTextView.text = "Upper Target: " + alert.source.plus(alert.target).toPlainString() + " ${alert.reference.symbol}"
            view.alertRangeTextView.text = "Range (+/-): " + alert.target.toPlainString() + " ${alert.reference.symbol}"
            view.setOnCreateContextMenuListener(this)
            itemView.setOnClickListener { onClickListener.onItemClick(alert) }
        }
    }
}