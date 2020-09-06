package com.ceaver.assin.markets.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ceaver.assin.databinding.MarketListRowBinding
import com.ceaver.assin.markets.Title

internal class MarketListAdapter : ListAdapter<Title, MarketListAdapter.ViewHolder>(Title.Difference) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = MarketListRowBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(getItem(position))
    }

    class ViewHolder(val binding: MarketListRowBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bindItem(title: Title) {
            binding.title = title
            binding.executePendingBindings()
        }
    }
}