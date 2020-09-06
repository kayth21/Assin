package com.ceaver.assin.markets

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.ceaver.assin.extensions.format

@BindingAdapter("titleImage")
fun ImageView.setTitleImage(title: Title) {
    setImageResource(title.getIcon())
}

@BindingAdapter("titleBtcPrice")
fun TextView.setTitleBtcPrice(title: Title) {
    text = title.priceBtc!!.format("BTC") + " BTC"
}

@BindingAdapter("titleUsdPrice")
fun TextView.setTitleUsdPrice(title: Title) {
    text = title.priceUsd!!.format("USD") + " USD"
}

@BindingAdapter("title1hChange")
fun TextView.setTitle1hChange(title: Title) {
    text = "1h: ${title.getPercentChange1hUsdString()}%"
}

@BindingAdapter("title24hChange")
fun TextView.setTitle24hChange(title: Title) {
    text = "24h: ${title.getPercentChange24hUsdString()}%"
}

@BindingAdapter("title7dChange")
fun TextView.setTitle7dChange(title: Title) {
    text = "7d: ${title.getPercentChange7dUsdString()}%"
}

@BindingAdapter("titleRank")
fun TextView.setTitleRank(title: Title) {
    text = title.rank.toString()
}