package com.ceaver.assin.action.list

import android.view.*
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ceaver.assin.action.Action
import com.ceaver.assin.databinding.ActionListRowBinding
import kotlin.random.Random

class ActionListAdapter(private val onClickListener: ActionListFragment.OnItemClickListener) : ListAdapter<Action, ActionListAdapter.ViewHolder>(Difference) {

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
            menu!!.add(Menu.NONE, MENU_ITEM_DELETE, Menu.NONE, "Remove")
        }

        fun bindItem(action: Action, onClickListener: ActionListFragment.OnItemClickListener) {
            binding.action = action
            binding.executePendingBindings()
            itemView.setOnCreateContextMenuListener(this)
            itemView.setOnClickListener { onClickListener.onItemClick(action) }
        }
    }

    object Difference : DiffUtil.ItemCallback<Action>() {
        override fun areItemsTheSame(oldItem: Action, newItem: Action): Boolean =
                oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Action, newItem: Action): Boolean =
                oldItem.equals(newItem)
    }

    companion object {
        val MENU_ITEM_DELETE = Random.nextInt()
    }
}