package com.ceaver.tradeadvisor.advices

import android.support.v7.widget.RecyclerView
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import android.view.View
import com.ceaver.tradeadvisor.R
import com.ceaver.tradeadvisor.trades.Trade
import com.ceaver.tradeadvisor.util.CalendarHelper

internal class AdviceListAdapter(private val onClickListener: AdviceListFragment.OnItemClickListener) : RecyclerView.Adapter<AdviceListAdapter.ViewHolder>() {

    var adviceList: List<Advice> = ArrayList()
    var currentLongClickTrade: Advice? = null

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

        fun bindItem(advice: Advice, onClickListener: AdviceListFragment.OnItemClickListener) {
            (view.findViewById(R.id.adviceDateTextView) as TextView).text = CalendarHelper.convertDate(advice.adviceDate)
            view.setOnCreateContextMenuListener(this)
            itemView.setOnClickListener { onClickListener.onItemClick(advice) }
        }
    }
}