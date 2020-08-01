package com.ceaver.assin.logging

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ceaver.assin.R
import kotlinx.android.synthetic.main.log_list_row.view.*
import java.time.format.DateTimeFormatter

class LogListAdapter : RecyclerView.Adapter<LogListAdapter.ViewHolder>() {
    var logList: List<Log> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.log_list_row, parent, false))
    }

    override fun onBindViewHolder(holder: LogListAdapter.ViewHolder, position: Int) {
        holder.bindItem(logList[position])
    }

    override fun getItemCount() = logList.size

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        fun bindItem(log: Log) {
            view.logTimestampTextView.text = log.timestamp.format(DateTimeFormatter.ofPattern("dd.MM.yyyy - HH:mm:ss"))
            view.logIdTextView.text = "#" + log.id
            view.logMessageTextView.text = log.message
        }
    }
}