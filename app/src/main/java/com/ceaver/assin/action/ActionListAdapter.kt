package com.ceaver.assin.action

import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ceaver.assin.R
import com.ceaver.assin.util.CalendarHelper
import kotlinx.android.synthetic.main.action_list_row.view.*

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
            view.actionListRowLeftImageView.setImageResource(action.getLeftImageResource())
            view.actionListRowTradeTypeTextView.text = action.getTitleText()
            view.actionListRowTradeDateTextView.text = CalendarHelper.convertDate(action.getActionDate())
            view.actionListRowTradeTextView.text = action.getDetailText()
            view.actionListRowRightImageView.setImageResource(action.getRightImageResource())
            view.setOnCreateContextMenuListener(this)
            itemView.setOnClickListener { onClickListener.onItemClick(action) }
        }
    }
}