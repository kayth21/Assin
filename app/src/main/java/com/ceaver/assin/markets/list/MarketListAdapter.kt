package com.ceaver.assin.markets.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ceaver.assin.R
import com.ceaver.assin.extensions.format
import com.ceaver.assin.markets.Title
import kotlinx.android.synthetic.main.market_list_row.view.*

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
            view.titleImageView.setImageResource(title.getIcon())
            view.titleRankTextView.text = title.rank.toString()
            view.titleSymbolTextView.text = title.symbol
            view.titleNameTextView.text = title.name
            view.titleCryptoPriceTextView.text = title.priceBtc!!.format("BTC") + " BTC"
            view.titleFiatPriceTextView.text = title.priceUsd!!.format("USD") + " USD"
            view.title1hChangeTextView.text = "1h: ${title.getPercentChange1hUsdString()}%"
            view.title24hChangeTextView.text = "24h: ${title.getPercentChange24hUsdString()}%"
            view.title7dChangeTextView.text = "7d: ${title.getPercentChange7dUsdString()}%"
        }
    }
}