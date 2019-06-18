package com.ceaver.assin.markets

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.ceaver.assin.MyApplication
import com.ceaver.assin.R
import com.ceaver.assin.extensions.format
import com.ceaver.assin.extensions.resIdByName

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
            (view.findViewById(R.id.titleImageView) as ImageView).setImageResource(getImageIdentifier(title))
            (view.findViewById(R.id.titleRankTextView) as TextView).text = title.rank.toString()
            (view.findViewById(R.id.titleSymbolTextView) as TextView).text = title.symbol
            (view.findViewById(R.id.titleNameTextView) as TextView).text = title.name
            (view.findViewById(R.id.titleCryptoPriceTextView) as TextView).text = title.priceBtc!!.format("BTC") + " BTC"
            (view.findViewById(R.id.titleFiatPriceTextView) as TextView).text = title.priceUsd!!.format("USD") + " USD"
            (view.findViewById(R.id.title1hChangeTextView) as TextView).text = "1h: ${title.getPercentChange1hUsdString()}%"
            (view.findViewById(R.id.title24hChangeTextView) as TextView).text = "24h: ${title.getPercentChange24hUsdString()}%"
            (view.findViewById(R.id.title7dChangeTextView) as TextView).text = "7d: ${title.getPercentChange7dUsdString()}%"
        }

        private fun getImageIdentifier(title: Title): Int {
            val identifier = MyApplication.appContext!!.resIdByName(title.symbol.toLowerCase(), "drawable")
            return if (identifier == 0) R.drawable.unknown else identifier
        }
    }
}