package com.ceaver.tradeadvisor.trades.active

import android.support.v7.widget.RecyclerView
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import android.view.View
import com.ceaver.tradeadvisor.DaggerAppComponent
import com.ceaver.tradeadvisor.R
import com.ceaver.tradeadvisor.trades.Trade
import com.ceaver.tradeadvisor.trades.TradeRepository
import com.ceaver.tradeadvisor.util.CalendarHelper
import java.text.SimpleDateFormat
import javax.inject.Inject

class ActiveTradesAdapter(private val onClickListener: ActiveTradesActivity.OnItemClickListener) : RecyclerView.Adapter<ActiveTradesAdapter.ViewHolder>() {

    private var activeTrades: List<Trade>
    var currentLongClickTrade: Trade? = null

    init {
        activeTrades = TradeRepository.loadTrades()
    }

    fun refresh() {
        activeTrades = TradeRepository.loadTrades()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.trade_list_row, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(activeTrades[position], onClickListener)
        holder.itemView.setOnLongClickListener {
            currentLongClickTrade = activeTrades[position]
            false
        }
    }

    override fun getItemCount() = activeTrades.size

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view), View.OnCreateContextMenuListener {

        override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
            menu!!.add(0, v!!.getId(), 0, "Delete")//groupId, itemId, order, title
        }

        fun bindItem(trade: Trade, onClickListener: ActiveTradesActivity.OnItemClickListener) {
            (view.findViewById(R.id.nameTextView) as TextView).text = "Bitcoin (BTC)"
            (view.findViewById(R.id.purchaseDateTextView) as TextView).text = CalendarHelper.convertDate(trade.tradeDate)
            (view.findViewById(R.id.purchaseAmountTextView) as TextView).text = trade.purchaseAmount.toString()
            (view.findViewById(R.id.purchasePriceTextView) as TextView).text = trade.purchasePrice.toString()
            view.setOnCreateContextMenuListener(this)
            itemView.setOnClickListener { onClickListener.onItemClick(trade) }
        }
    }
}