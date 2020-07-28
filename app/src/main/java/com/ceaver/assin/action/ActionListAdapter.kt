package com.ceaver.assin.action

import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ceaver.assin.MyApplication
import com.ceaver.assin.R
import com.ceaver.assin.extensions.resIdByName
import com.ceaver.assin.util.CalendarHelper

internal class ActionListAdapter(private val onClickListener: ActionListFragment.OnItemClickListener) : RecyclerView.Adapter<ActionListAdapter.ViewHolder>() {

    var actionList: List<Action> = ArrayList()
    var currentLongClickAction: Action? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.action_list_row, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(actionList[position], onClickListener)
        holder.itemView.setOnLongClickListener { currentLongClickAction = actionList[position]; false }
    }

    override fun getItemCount() = actionList.size

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view), View.OnCreateContextMenuListener {

        override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
            menu!!.add(3, 0, 0, "Delete")
        }

        fun bindItem(action: Action, onClickListener: ActionListFragment.OnItemClickListener) {
            (view.findViewById(R.id.actionListRowLeftImageView) as ImageView).setImageResource(getLeftImageResource(action))
            (view.findViewById(R.id.actionListRowTradeTypeTextView) as TextView).text = getActionTypeText(action)
            (view.findViewById(R.id.actionListRowTradeDateTextView) as TextView).text = CalendarHelper.convertDate(action.actionDate)
            (view.findViewById(R.id.actionListRowTradeTextView) as TextView).text = getActionText(action)
            (view.findViewById(R.id.actionListRowRightImageView) as ImageView).setImageResource(getRightImageResource(action))
            view.setOnCreateContextMenuListener(this)
            itemView.setOnClickListener { onClickListener.onItemClick(action) }
        }

        private fun getActionTypeText(action: Action): String {
            return when (action.actionType) {
                ActionType.DEPOSIT -> "Deposit ${action.buyTitle!!.name}"
                ActionType.WITHDRAW -> "Withdraw ${action.sellTitle!!.name}"
                ActionType.TRADE -> "${action.sellTitle!!.name} -> ${action.buyTitle!!.name}"
                ActionType.SPLIT -> "Split ${action.splitTitle!!.name} position"
            }
        }

        private fun getActionText(action: Action): String {
            return when (action.actionType) {
                ActionType.DEPOSIT -> "${action.buyAmount!!} ${action.buyTitle!!.symbol}"
                ActionType.WITHDRAW -> "${action.sellAmount!!} ${action.sellTitle!!.symbol}"
                ActionType.TRADE -> "${action.sellAmount!!} ${action.sellTitle!!.symbol} -> ${action.buyAmount!!} ${action.buyTitle!!.symbol}"
                ActionType.SPLIT -> "${action.splitAmount!!.add(action.splitRemaining)} ${action.splitTitle!!.symbol} splitted into ${action.splitAmount} ${action.splitTitle!!.symbol} and ${action.splitRemaining} ${action.splitTitle!!.symbol}"
            }
        }

        private fun getRightImageResource(action: Action): Int {
            return when (action.actionType) {
                ActionType.DEPOSIT -> getImageIdentifier(action.buyTitle!!.symbol.toLowerCase())
                ActionType.WITHDRAW -> R.drawable.withdraw
                ActionType.TRADE -> getImageIdentifier(action.buyTitle!!.symbol.toLowerCase())
                ActionType.SPLIT -> R.drawable.split
            }
        }

        private fun getLeftImageResource(action: Action): Int {
            return when (action.actionType) {
                ActionType.DEPOSIT -> R.drawable.deposit
                ActionType.WITHDRAW -> getImageIdentifier(action.sellTitle!!.symbol.toLowerCase())
                ActionType.TRADE -> getImageIdentifier(action.sellTitle!!.symbol.toLowerCase())
                ActionType.SPLIT -> getImageIdentifier(action.splitTitle!!.symbol.toLowerCase())
            }
        }

        private fun getImageIdentifier(symbol: String): Int {
            val identifier = MyApplication.appContext!!.resIdByName(symbol.toLowerCase(), "drawable")
            return if (identifier == 0) R.drawable.unknown else identifier
        }
    }
}