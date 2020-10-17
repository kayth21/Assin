package com.ceaver.assin.positions.list

import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ceaver.assin.databinding.PositionListRowBinding
import com.ceaver.assin.extensions.asFormattedDateTime
import com.ceaver.assin.extensions.toCurrencyString
import com.ceaver.assin.positions.Position
import com.ceaver.assin.preferences.Preferences
import java.math.RoundingMode
import kotlin.random.Random

internal class PositionListAdapter(private val onClickListener: PositionListFragment.OnItemClickListener, val fragment: Fragment) : ListAdapter<Position, PositionListAdapter.ViewHolder>(Position.Difference) {

    companion object {
        val CONTEXT_MENU_GROUP_ID = Random.nextInt() // TODO
        val CONTEXT_MENU_WITHDRAW_ITEM_ID = Random.nextInt() // TODO
    }

    var currentLongClickPosition: Position? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PositionListAdapter.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = PositionListRowBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(getItem(position), onClickListener)
        holder.itemView.setOnLongClickListener { currentLongClickPosition = getItem(holder.layoutPosition); false }
    }

    class ViewHolder(val binding: PositionListRowBinding) : RecyclerView.ViewHolder(binding.root), View.OnCreateContextMenuListener {
        override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
            menu!!.add(CONTEXT_MENU_GROUP_ID, CONTEXT_MENU_WITHDRAW_ITEM_ID, 0, "Withdraw")
        }

        fun bindItem(position: Position, onClickListener: PositionListFragment.OnItemClickListener) {
            binding.positionListRowPositionSize.text = "${position.quantity.toCurrencyString(position.title.symbol)} ${position.title.symbol}"
            binding.positionListRowPositionValuePrimary.text = "${position.currentValuePrimary.toCurrencyString(Preferences.getCryptoTitleSymbol())} ${Preferences.getCryptoTitleSymbol()} (${position.profitLossInPercentToPrimaryTitle.setScale(0, RoundingMode.HALF_UP).toPlainString()}%)"
            binding.positionListRowPositionValueSecondary.text = "${position.currentValueSecondary.toCurrencyString(Preferences.getFiatTitleSymbol())} ${Preferences.getFiatTitleSymbol()} (${position.profitLossInPercentToSecondaryValue.setScale(0, RoundingMode.HALF_UP).toPlainString()}%)"
            binding.positionListRowPositionOpenDate.text = "Open: ${position.open.date.asFormattedDateTime()}"
            if (!position.isActive())
                binding.positionListRowPositionCloseDate.text = "Close: ${position.close!!.date.asFormattedDateTime()}"

            itemView.setOnClickListener { onClickListener.onItemClick(position) }
            if (position.isActive())
                itemView.setOnCreateContextMenuListener(this)
        }
    }
}