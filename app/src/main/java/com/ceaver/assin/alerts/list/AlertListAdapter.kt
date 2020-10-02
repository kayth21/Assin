package com.ceaver.assin.alerts.list

import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ceaver.assin.alerts.Alert
import com.ceaver.assin.databinding.AlertListRowBinding

internal class AlertListAdapter(private val onClickListener: AlertListFragment.OnItemClickListener) : ListAdapter<Alert, AlertListAdapter.ViewHolder>(Alert.Difference) {

    var currentLongClickAlert: Alert? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = AlertListRowBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(getItem(position), onClickListener)
        holder.itemView.setOnLongClickListener { currentLongClickAlert = getItem(holder.layoutPosition); false }
    }

    class ViewHolder(val binding: AlertListRowBinding) : RecyclerView.ViewHolder(binding.root), View.OnCreateContextMenuListener {

        override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
            menu!!.add(0, v!!.id, 0, "Delete")
        }

        fun bindItem(alert: Alert, onClickListener: AlertListFragment.OnItemClickListener) {
            binding.alertSymbolTextView.text = "${alert.title.symbol} (${alert.title.name})"
            binding.alertLowerTargetTextView.text = "Lower Target: " + alert.source.minus(alert.target).toPlainString() + " ${alert.referenceTitle.symbol}"
            binding.alertUpperTargetTextView.text = "Upper Target: " + alert.source.plus(alert.target).toPlainString() + " ${alert.referenceTitle.symbol}"
            binding.alertRangeTextView.text = "Range (+/-): " + alert.target.toPlainString() + " ${alert.referenceTitle.symbol}"
            itemView.setOnCreateContextMenuListener(this)
            itemView.setOnClickListener { onClickListener.onItemClick(alert) }
        }
    }
}