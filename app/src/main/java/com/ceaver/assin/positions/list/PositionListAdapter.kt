package com.ceaver.assin.positions.list

import android.view.*
import androidx.core.view.iterator
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.ceaver.assin.R
import com.ceaver.assin.extensions.asFormattedDateTime
import com.ceaver.assin.extensions.toCurrencyString
import com.ceaver.assin.positions.Position
import kotlinx.android.synthetic.main.position_list_row.view.*
import java.math.RoundingMode
import kotlin.random.Random

internal class PositionListAdapter(private val onClickListener: PositionListFragment.OnItemClickListener, val fragment: Fragment) : RecyclerView.Adapter<PositionListAdapter.PositionViewHolder>() {

    companion object {
        val CONTEXT_MENU_GROUP_ID = Random.nextInt() // TODO
        val CONTEXT_MENU_WITHDRAW_ITEM_ID = Random.nextInt() // TODO
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
            menu!!.add(CONTEXT_MENU_GROUP_ID, CONTEXT_MENU_WITHDRAW_ITEM_ID, 0, "Withdraw")

            // hack because of https://stackoverflow.com/questions/15929026/oncontextitemselected-does-not-get-called-in-a-dialogfragment
            menu.iterator().forEach { it.setOnMenuItemClickListener(MenuItem.OnMenuItemClickListener { fragment.onContextItemSelected(it);true }) }
        }

        fun bindItem(position: Position, onClickListener: PositionListFragment.OnItemClickListener) {
            view.positionListRowPositionSize.text = "${position.amount.toCurrencyString(position.title.symbol)} ${position.title.symbol}"
            view.positionListRowPositionValueBtc.text = "${position.currentValueInBtc().toCurrencyString("BTC")} BTC (${position.profitLossInPercentToBtc().setScale(0, RoundingMode.HALF_UP)}%)"
            view.positionListRowPositionValueUsd.text = "${position.currentValueInUsd().toCurrencyString("USD")} USD (${position.profitLossInPercentToUsd().setScale(0, RoundingMode.HALF_UP)}%)"
            view.positionListRowPositionOpenDate.text = "Open: ${position.openDate.asFormattedDateTime()}"
            if (!position.isActive())
                view.positionListRowPositionCloseDate.text = "Close: ${position.closeDate!!.asFormattedDateTime()}"


            itemView.setOnClickListener { onClickListener.onItemClick(position) }
            if (position.isActive())
                view.setOnCreateContextMenuListener(this)
        }
    }
}