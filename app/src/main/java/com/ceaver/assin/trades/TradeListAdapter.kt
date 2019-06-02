package com.ceaver.assin.trades

import android.support.v7.widget.RecyclerView
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.ceaver.assin.R
import com.ceaver.assin.util.CalendarHelper

internal class TradeListAdapter(private val onClickListener: TradeListFragment.OnItemClickListener) : RecyclerView.Adapter<TradeListAdapter.ViewHolder>() {

    var tradeList: List<Trade> = ArrayList()
    var currentLongClickTrade: Trade? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.trade_list_row, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(tradeList[position], onClickListener)
        holder.itemView.setOnLongClickListener { currentLongClickTrade = tradeList[position]; false }
    }

    override fun getItemCount() = tradeList.size

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view), View.OnCreateContextMenuListener {

        override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
            menu!!.add(0, v!!.getId(), 0, "Delete")
        }

        fun bindItem(trade: Trade, onClickListener: TradeListFragment.OnItemClickListener) {
            (view.findViewById(R.id.tradeDateTextView) as TextView).text = CalendarHelper.convertDate(trade.tradeDate)
            (view.findViewById(R.id.sellTextView) as TextView).text = if (trade.sellAmount.isPresent) "${trade.sellAmount.get()} ${trade.sellSymbol.get()}" else ""
            (view.findViewById(R.id.buyTextView) as TextView).text = if (trade.buyAmount.isPresent) "${trade.buyAmount.get()} ${trade.buySymbol.get()}" else ""
            view.setOnCreateContextMenuListener(this)
            itemView.setOnClickListener { onClickListener.onItemClick(trade) }
        }
    }
}