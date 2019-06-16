package com.ceaver.assin.intentions

import android.support.v7.widget.RecyclerView
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.ceaver.assin.MyApplication
import com.ceaver.assin.R
import com.ceaver.assin.extensions.format
import com.ceaver.assin.extensions.resIdByName
import com.ceaver.assin.markets.Title

internal class IntentionListAdapter(private val onClickListener: IntentionListFragment.OnItemClickListener) : RecyclerView.Adapter<IntentionListAdapter.IntentionViewHolder>() {

    var intentionList: List<Intention> = ArrayList()
    var currentLongClickIntention: Intention? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IntentionListAdapter.IntentionViewHolder {
        return IntentionViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.intention_list_row, parent, false))
    }

    override fun getItemCount(): Int = intentionList.size

    override fun onBindViewHolder(holder: IntentionViewHolder, position: Int) {
        holder.bindItem(intentionList[position], onClickListener)
        holder.itemView.setOnLongClickListener { currentLongClickIntention = intentionList[position]; false }
    }

    inner class IntentionViewHolder(val view: View) : RecyclerView.ViewHolder(view), View.OnCreateContextMenuListener {
        override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
            menu!!.add(2, 0, 0, "Delete")
        }

        fun bindItem(intention: Intention, onClickListener: IntentionListFragment.OnItemClickListener) {
            (view.findViewById(R.id.intentionListRowLeftImageView) as ImageView).setImageResource(getImageIdentifier(intention.title))
            (view.findViewById(R.id.intentionListRowAssetTextView) as TextView).text = "${intention.type} ${intention.amountAsString()} ${intention.title}"
            (view.findViewById(R.id.intentionListRowReferenceTextView) as TextView).text = "Target Price: ${intention.referencePrice} ${intention.referenceTitle.symbol}"
            (view.findViewById(R.id.intentionListRowPercentTextView) as TextView).text = "${intention.percentToReferencePrice().format("abc")}%"
            view.setOnCreateContextMenuListener(this)
            itemView.setOnClickListener { onClickListener.onItemClick(intention) }
        }

        private fun getImageIdentifier(title: Title): Int {
            val identifier = MyApplication.appContext!!.resIdByName(title.symbol.toLowerCase(), "drawable")
            return if (identifier == 0) R.drawable.unknown else identifier
        }
    }
}