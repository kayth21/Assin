package com.ceaver.assin.alerts.list

import android.view.*
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ceaver.assin.alerts.Alert
import com.ceaver.assin.databinding.AlertListRowBinding
import kotlin.random.Random

class AlertListAdapter(private val onClickListener: AlertListFragment.OnItemClickListener) : ListAdapter<Alert, AlertListAdapter.ViewHolder>(Difference) {

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
            menu!!.add(Menu.NONE, MENU_ITEM_DELETE, Menu.NONE, "Remove")
        }

        fun bindItem(alert: Alert, onClickListener: AlertListFragment.OnItemClickListener) {
            binding.alert = alert
            binding.executePendingBindings()
            itemView.setOnCreateContextMenuListener(this)
            itemView.setOnClickListener { onClickListener.onItemClick(alert) }
        }
    }

    object Difference : DiffUtil.ItemCallback<Alert>() {
        override fun areItemsTheSame(oldItem: Alert, newItem: Alert): Boolean =
                oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Alert, newItem: Alert): Boolean =
                oldItem.equals(newItem)
    }

    companion object {
        val MENU_ITEM_DELETE = Random.nextInt()
    }
}