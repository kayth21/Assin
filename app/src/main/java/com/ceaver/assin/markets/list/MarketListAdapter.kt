package com.ceaver.assin.markets.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ceaver.assin.databinding.MarketListRowBinding
import com.ceaver.assin.markets.CryptoTitle

internal class MarketListAdapter(private val onClickListener: MarketListFragment.OnItemClickListener) : ListAdapter<CryptoTitle, MarketListAdapter.ViewHolder>(Difference) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = MarketListRowBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(getItem(position), onClickListener)
    }

    class ViewHolder(val binding: MarketListRowBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bindItem(title: CryptoTitle, onClickListener: MarketListFragment.OnItemClickListener) {
            binding.title = title
            binding.executePendingBindings()
            itemView.setOnClickListener { onClickListener.onItemClick(title) }
        }
    }
}

// TODO it should only compare what is displayed on screen, so not comparing exact but rounded values
object Difference : DiffUtil.ItemCallback<CryptoTitle>() {
    override fun areItemsTheSame(oldItem: CryptoTitle, newItem: CryptoTitle): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: CryptoTitle, newItem: CryptoTitle): Boolean {
        return oldItem.rank == newItem.rank &&
                oldItem.cryptoQuotes == newItem.cryptoQuotes &&
                oldItem.fiatQuotes == newItem.fiatQuotes
    }
}