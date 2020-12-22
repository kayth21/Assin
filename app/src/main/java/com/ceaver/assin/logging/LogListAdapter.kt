package com.ceaver.assin.logging

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ceaver.assin.databinding.LogListRowBinding
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class LogListAdapter : ListAdapter<LogEntity, LogListAdapter.ViewHolder>(Difference) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = LogListRowBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(getItem(position))
    }

    class ViewHolder(val binding: LogListRowBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bindItem(log: LogEntity) {
            binding.logTimestampTextView.text = log.timestamp.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM))
            binding.logIdTextView.text = "#${log.id}"
            binding.logMessageTextView.text = log.message
        }
    }

    object Difference : DiffUtil.ItemCallback<LogEntity>() {
        override fun areItemsTheSame(oldItem: LogEntity, newItem: LogEntity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: LogEntity, newItem: LogEntity): Boolean {
            return oldItem == newItem
        }
    }
}