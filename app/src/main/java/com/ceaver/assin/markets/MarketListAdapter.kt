package com.ceaver.assin.markets

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import android.view.View
import com.ceaver.assin.R
import com.ceaver.assin.assets.Category
import com.ceaver.assin.assets.Symbol

internal class MarketListAdapter : RecyclerView.Adapter<MarketListAdapter.ViewHolder>() {

    var titles: List<Title> = Symbol.values(Category.CRYPTO).map { Title(it) }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.market_list_row, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(titles[position])
    }

    override fun getItemCount() = titles.size

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        fun bindItem(title: Title) {
            (view.findViewById(R.id.assetNameTextView) as TextView).text = title.symbol.label
            (view.findViewById(R.id.dailyChangeTextView) as TextView).text = "24h change not available"
            (view.findViewById(R.id.cryptoValueTextView) as TextView).text = "%.8f".format(title.symbol.loadBtcPrice()) + " BTC"
            (view.findViewById(R.id.fiatValueTextView) as TextView).text = "%.2f".format(title.symbol.loadUsdPrice()) + " USD"
        }
    }
}