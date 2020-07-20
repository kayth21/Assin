package com.ceaver.assin.positions.list

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
import com.ceaver.assin.extensions.toCurrencyString
import com.ceaver.assin.markets.Title
import com.ceaver.assin.positions.Position
import java.math.RoundingMode
import kotlin.random.Random

internal class PositionListAdapter(private val onClickListener: PositionListFragment.OnItemClickListener) : RecyclerView.Adapter<PositionListAdapter.PositionViewHolder>() {

    companion object {
        val CONTEXT_MENU_GROUP_ID = Random.nextInt() // TODO
        val CONTEXT_MENU_EDIT_ITEM_ID = Random.nextInt() // TODO
        val CONTEXT_MENU_DELETE_ITEM_ID = Random.nextInt() // TODO
        val CONTEXT_MENU_RESET_ITEM_ID = Random.nextInt() // TODO
        val CONTEXT_MENU_SHOW_ITEM_ID = Random.nextInt() // TODO
    }

    var positionList: List<Position> = ArrayList()
    var currentLongClickPosition: Position? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PositionViewHolder {
        return PositionViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.position_list_row, parent, false))
    }

    override fun getItemCount(): Int = positionList.size

    override fun onBindViewHolder(holder: PositionViewHolder, position: Int) {
        holder.bindItem(positionList[position], onClickListener)
        holder.itemView.setOnLongClickListener { currentLongClickPosition = positionList[position]; false }
    }

    inner class PositionViewHolder(private val view: View) : RecyclerView.ViewHolder(view), View.OnCreateContextMenuListener {
        override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
            menu!!.add(CONTEXT_MENU_GROUP_ID, CONTEXT_MENU_EDIT_ITEM_ID, 0, "Edit")
            menu.add(CONTEXT_MENU_GROUP_ID, CONTEXT_MENU_DELETE_ITEM_ID, 1, "Delete")
            menu.add(CONTEXT_MENU_GROUP_ID, CONTEXT_MENU_RESET_ITEM_ID, 2, "Reset")
            menu.add(CONTEXT_MENU_GROUP_ID, CONTEXT_MENU_SHOW_ITEM_ID, 3, "Show")
        }

        fun bindItem(position: Position, onClickListener: PositionListFragment.OnItemClickListener) {
            (view.findViewById(R.id.positionListRowSymbol) as ImageView).setImageResource(getImageIdentifier(position.title))
            (view.findViewById(R.id.positionListRowPositionTitle) as TextView).text = position.title.name
            (view.findViewById(R.id.positionListRowPositionSize) as TextView).text = "${position.amount.toCurrencyString(position.title.symbol)} ${position.title.symbol}"
            (view.findViewById(R.id.positionListRowPositionValueBtc) as TextView).text = "${position.currentValueInBtc().toCurrencyString("BTC")} BTC (${position.profitLossInPercentToBtc().setScale(0, RoundingMode.HALF_UP)}%)"
            (view.findViewById(R.id.positionListRowPositionValueUsd) as TextView).text = "${position.currentValueInUsd().toCurrencyString("USD")} USD (${position.profitLossInPercentToUsd().setScale(0, RoundingMode.HALF_UP)}%)"

            view.setOnCreateContextMenuListener(this)
            itemView.setOnClickListener { onClickListener.onItemClick(position) }
        }

        private fun getImageIdentifier(title: Title): Int {
            val identifier = MyApplication.appContext!!.resIdByName(title.symbol.toLowerCase(), "drawable")
            return if (identifier == 0) R.drawable.unknown else identifier
        }
    }
}