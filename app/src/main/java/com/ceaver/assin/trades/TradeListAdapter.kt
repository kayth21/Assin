package com.ceaver.assin.trades

import android.support.v7.widget.RecyclerView
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.ceaver.assin.MyApplication
import com.ceaver.assin.R
import com.ceaver.assin.extensions.resIdByName
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
            menu!!.add(3, 0, 0, "Delete")
        }

        fun bindItem(trade: Trade, onClickListener: TradeListFragment.OnItemClickListener) {
            (view.findViewById(R.id.tradeListRowLeftImageView) as ImageView).setImageResource(getLeftImageResource(trade))
            (view.findViewById(R.id.tradeListRowTradeTypeTextView) as TextView).text = getTradeTypeText(trade)
            (view.findViewById(R.id.tradeListRowTradeDateTextView) as TextView).text = CalendarHelper.convertDate(trade.tradeDate)
            (view.findViewById(R.id.tradeListRowTradeTextView) as TextView).text = getTradeText(trade)
            (view.findViewById(R.id.tradeListRowRightImageView) as ImageView).setImageResource(getRightImageResource(trade))
            view.setOnCreateContextMenuListener(this)
            itemView.setOnClickListener { onClickListener.onItemClick(trade) }
        }

        private fun getTradeTypeText(trade: Trade): String {
            return when (trade.getTradeType()) {
                TradeType.DEPOSIT -> "Deposit ${trade.buyTitle.get().name}"
                TradeType.WITHDRAW -> "Withdraw ${trade.sellTitle.get().name}"
                TradeType.TRADE -> "${trade.sellTitle.get().name} -> ${trade.buyTitle.get().name}"
            }
        }

        private fun getTradeText(trade: Trade): String {
            return when (trade.getTradeType()) {
                TradeType.DEPOSIT -> "${trade.buyAmount.get()} ${trade.buyTitle.get().symbol}"
                TradeType.WITHDRAW -> "${trade.sellAmount.get()} ${trade.sellTitle.get().symbol}"
                TradeType.TRADE -> "${trade.sellAmount.get()} ${trade.sellTitle.get().symbol} -> ${trade.buyAmount.get()} ${trade.buyTitle.get().symbol}"
            }
        }

        private fun getRightImageResource(trade: Trade): Int {
            return when (trade.getTradeType()) {
                TradeType.DEPOSIT -> getImageIdentifier(trade.buyTitle.get().symbol.toLowerCase())
                TradeType.WITHDRAW -> R.drawable.withdraw
                TradeType.TRADE -> getImageIdentifier(trade.buyTitle.get().symbol.toLowerCase())
            }
        }

        private fun getLeftImageResource(trade: Trade): Int {
            return when (trade.getTradeType()) {
                TradeType.DEPOSIT -> R.drawable.deposit
                TradeType.WITHDRAW -> getImageIdentifier(trade.sellTitle.get().symbol.toLowerCase())
                TradeType.TRADE -> getImageIdentifier(trade.sellTitle.get().symbol.toLowerCase())
            }
        }

        private fun getImageIdentifier(symbol: String): Int {
            val identifier = MyApplication.appContext!!.resIdByName(symbol.toLowerCase(), "drawable")
            return if (identifier == 0) R.drawable.unknown else identifier
        }
    }
}