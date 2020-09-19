package com.ceaver.assin.markets

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.ceaver.assin.extensions.format
import com.ceaver.assin.preferences.Preferences

@BindingAdapter("titleImage")
fun ImageView.setTitleImage(title: Title) {
    setImageResource(title.getIcon())
}

@BindingAdapter("titleBtcPrice")
fun TextView.setTitleBtcPrice(title: Title) {
    val symbol = Preferences.getCryptoTitleSymbol()
    text = title.cryptoQuotes.price.format(symbol) + " " + symbol
}

@BindingAdapter("titleUsdPrice")
fun TextView.setTitleUsdPrice(title: Title) {
    val symbol = Preferences.getFiatTitleSymbol()
    text = title.fiatQuotes.price.format(symbol) + " " + symbol
}

@BindingAdapter("title1hChange")
fun TextView.setTitle1hChange(title: Title) {
    text = "1h: ${title.getPercentChange1hString()}%"
}

@BindingAdapter("title24hChange")
fun TextView.setTitle24hChange(title: Title) {
    text = "24h: ${title.getPercentChange24hString()}%"
}

@BindingAdapter("title7dChange")
fun TextView.setTitle7dChange(title: Title) {
    text = "7d: ${title.getPercentChange7dString()}%"
}

@BindingAdapter("titleRank")
fun TextView.setTitleRank(title: Title) {
    text = title.rank.toString()
}