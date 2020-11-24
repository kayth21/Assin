package com.ceaver.assin.positions.list

import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ceaver.assin.databinding.PositionListRowBinding
import com.ceaver.assin.positions.Position
import kotlin.random.Random

internal class PositionListAdapter(private val onClickListener: PositionListFragment.OnItemClickListener, val fragment: Fragment) : ListAdapter<Position, PositionListAdapter.ViewHolder>(Difference) {

    var currentLongClickPosition: Position? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PositionListAdapter.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = PositionListRowBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(getItem(position), onClickListener)
        holder.itemView.setOnLongClickListener { currentLongClickPosition = getItem(holder.layoutPosition); false }
    }

    class ViewHolder(val binding: PositionListRowBinding) : RecyclerView.ViewHolder(binding.root), View.OnCreateContextMenuListener {
        override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
            menu!!.add(Menu.NONE, MENU_ITEM_WITHDRAW, Menu.NONE, "Withdraw")
        }

        fun bindItem(position: Position, onClickListener: PositionListFragment.OnItemClickListener) {
            binding.position = position
            binding.executePendingBindings()

            itemView.setOnClickListener { onClickListener.onItemClick(position) }
            if (position.isOpen())
                itemView.setOnCreateContextMenuListener(this)
        }
    }

    companion object {
        val MENU_ITEM_WITHDRAW = Random.nextInt()
    }

    object Difference : DiffUtil.ItemCallback<Position>() {
        override fun areItemsTheSame(oldItem: Position, newItem: Position): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Position, newItem: Position): Boolean {
            return oldItem == newItem
        }
    }
}