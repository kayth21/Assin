package com.ceaver.assin.logging

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ceaver.assin.databinding.LogListRowBinding
import java.time.format.DateTimeFormatter

class LogListAdapter : ListAdapter<LogEntity, LogListAdapter.ViewHolder>(LogEntity.Difference) {

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
            binding.logTimestampTextView.text = log.timestamp.format(DateTimeFormatter.ofPattern("dd.MM.yyyy - HH:mm:ss"))
            binding.logIdTextView.text = "#" + log.id
            binding.logMessageTextView.text = log.message
        }
    }
}