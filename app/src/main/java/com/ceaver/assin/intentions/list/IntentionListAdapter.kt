package com.ceaver.assin.intentions.list

import android.view.*
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ceaver.assin.databinding.IntentionListRowBinding
import com.ceaver.assin.intentions.Intention
import kotlin.random.Random

class IntentionListAdapter(private val onClickListener: IntentionListFragment.OnItemClickListener) : ListAdapter<Intention, IntentionListAdapter.ViewHolder>(Difference) {

    var currentLongClickIntention: Intention? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = IntentionListRowBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(getItem(position), onClickListener)
        holder.itemView.setOnLongClickListener { currentLongClickIntention = getItem(holder.layoutPosition); false }
    }

    class ViewHolder(val binding: IntentionListRowBinding) : RecyclerView.ViewHolder(binding.root), View.OnCreateContextMenuListener {

        override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
            menu!!.add(Menu.NONE, MENU_ITEM_DELETE, Menu.NONE, "Remove")
        }

        fun bindItem(intention: Intention, onClickListener: IntentionListFragment.OnItemClickListener) {
            binding.intention = intention
            binding.executePendingBindings()
            itemView.setOnCreateContextMenuListener(this)
            itemView.setOnClickListener { onClickListener.onItemClick(intention) }
        }
    }

    object Difference : DiffUtil.ItemCallback<Intention>() {
        override fun areItemsTheSame(oldItem: Intention, newItem: Intention): Boolean =
                oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Intention, newItem: Intention): Boolean =
                oldItem == newItem && oldItem.factorToReferencePrice == newItem.factorToReferencePrice
    }

    companion object {
        val MENU_ITEM_DELETE = Random.nextInt()
    }
}