package com.ceaver.assin.action.list

import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ceaver.assin.action.Action
import com.ceaver.assin.databinding.ActionListRowBinding

internal class ActionListAdapter(private val onClickListener: ActionListFragment.OnItemClickListener) : ListAdapter<Action, ActionListAdapter.ViewHolder>(Action.Difference) {

    var currentLongClickAction: Action? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ActionListRowBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(getItem(position), onClickListener)
        holder.itemView.setOnLongClickListener { currentLongClickAction = getItem(holder.layoutPosition); false }
    }

    class ViewHolder(val binding: ActionListRowBinding) : RecyclerView.ViewHolder(binding.root), View.OnCreateContextMenuListener {

        override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
            menu!!.add(3, 0, 0, "Delete")
        }

        fun bindItem(action: Action, onClickListener: ActionListFragment.OnItemClickListener) {
            binding.action = action
            binding.executePendingBindings()
            itemView.setOnCreateContextMenuListener(this)
            itemView.setOnClickListener { onClickListener.onItemClick(action) }
        }
    }
}