package com.ceaver.assin.trades

import android.support.v7.widget.RecyclerView
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.ceaver.assin.R

internal class TradeListAdapter(private val onClickListener: TradeListFragment.OnItemClickListener) : RecyclerView.Adapter<TradeListAdapter.ViewHolder>() {

    var tradeList: List<Trade> = ArrayList()
    var currentLongClickTrade: Trade? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.trade_list_row, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(tradeList[position], onClickListener)
        holder.itemView.setOnLongClickListener {currentLongClickTrade = tradeList[position]; false }
    }

    override fun getItemCount() = tradeList.size

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view), View.OnCreateContextMenuListener {

        override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
            menu!!.add(0, v!!.getId(), 0, "Delete")
        }

        fun bindItem(trade: Trade, onClickListener: TradeListFragment.OnItemClickListener) {
            (view.findViewById(R.id.tradeDateTextView) as TextView).text = trade.tradeDate.toString()
            (view.findViewById(R.id.sellTextView) as TextView).text = "${trade.sellAmount} ${trade.sellSymbol}"
            (view.findViewById(R.id.buyTextView) as TextView).text = "${trade.buyAmount} ${trade.buySymbol}"
            view.setOnCreateContextMenuListener(this)
            itemView.setOnClickListener { onClickListener.onItemClick(trade) }
        }
    }
}