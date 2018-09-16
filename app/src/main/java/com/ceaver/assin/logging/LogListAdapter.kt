package com.ceaver.assin.logging

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.ceaver.assin.R
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
            (view.findViewById(R.id.logTimestampTextView) as TextView).text = log.timestamp.format(DateTimeFormatter.ofPattern("dd.MM.yyyy - HH:mm:ss"))
            (view.findViewById(R.id.logMessageTextView) as TextView).text = log.message
        }
    }
}