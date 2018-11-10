package com.ceaver.assin.markets

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.ceaver.assin.R
import com.ceaver.assin.assets.Symbol
import com.ceaver.assin.extensions.format

internal class MarketListAdapter : RecyclerView.Adapter<MarketListAdapter.ViewHolder>() {

    var titles: List<Title> = ArrayList()


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
            (view.findViewById(R.id.dailyChangeTextView) as TextView).text = "%.2f".format((((100/title.open(Symbol.USD).asDouble)*title.last(Symbol.USD).asDouble)-100)) + " % (USD)"
            (view.findViewById(R.id.cryptoValueTextView) as TextView).text = title.last(Symbol.BTC).asDouble.format(Symbol.BTC) + " BTC"
            (view.findViewById(R.id.fiatValueTextView) as TextView).text = title.last(Symbol.USD).asDouble.format(Symbol.USD) + " USD"
        }
    }
}