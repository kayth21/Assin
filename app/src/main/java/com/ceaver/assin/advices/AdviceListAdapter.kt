package com.ceaver.assin.advices

import android.support.v7.widget.RecyclerView
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import android.view.View
import com.ceaver.assin.R
import com.ceaver.assin.util.CalendarHelper

internal class AdviceListAdapter(private val onClickListener: com.ceaver.assin.advices.AdviceListFragment.OnItemClickListener) : RecyclerView.Adapter<AdviceListAdapter.ViewHolder>() {

    var adviceList: List<com.ceaver.assin.advices.Advice> = ArrayList()
    var currentLongClickTrade: com.ceaver.assin.advices.Advice? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.advice_list_row, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(adviceList[position], onClickListener)
        holder.itemView.setOnLongClickListener {currentLongClickTrade = adviceList[position]; false }
    }

    override fun getItemCount() = adviceList.size

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view), View.OnCreateContextMenuListener {

        override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
            menu!!.add(0, v!!.getId(), 0, "Delete")
        }

        fun bindItem(advice: com.ceaver.assin.advices.Advice, onClickListener: com.ceaver.assin.advices.AdviceListFragment.OnItemClickListener) {
            (view.findViewById(R.id.adviceDateTextView) as TextView).text = CalendarHelper.convertDate(advice.adviceDate)
            view.setOnCreateContextMenuListener(this)
            itemView.setOnClickListener { onClickListener.onItemClick(advice) }
        }
    }
}