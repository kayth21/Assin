package com.ceaver.assin.assets.detail.overview

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.ceaver.assin.assets.Asset
import com.ceaver.assin.extensions.asPercentOf
import com.ceaver.assin.extensions.toCurrencyString
import com.ceaver.assin.preferences.Preferences
import java.math.RoundingMode

@BindingAdapter("totalInFiatOpen")
fun TextView.setAssetDetailOverviewBindingAdapterTotalInFiatOpen(asset: Asset?) {
    text = if (asset == null) "" else "Total Open: ${asset.open.valueFiat.toCurrencyString(Preferences.getFiatTitleSymbol())} ${Preferences.getFiatTitleSymbol()}"
}

@BindingAdapter("totalInCryptoOpen")
fun TextView.setAssetDetailOverviewBindingAdapterTotalInCryptoOpen(asset: Asset?) {
    text = if (asset == null) "" else "Total Open: ${asset.open.valueCrypto.toCurrencyString(Preferences.getCryptoTitleSymbol())} ${Preferences.getCryptoTitleSymbol()}"
}

@BindingAdapter("totalInFiatCurrent")
fun TextView.setAssetDetailOverviewBindingAdapterTotalInFiatCurrent(asset: Asset?) {
    text = if (asset == null) "" else "Total Today: ${asset.current.valueFiat.toCurrencyString(Preferences.getFiatTitleSymbol())} ${Preferences.getFiatTitleSymbol()} (${asset.current.valueFiat.asPercentOf(asset.open.valueFiat).setScale(0, RoundingMode.HALF_UP).toPlainString()}%)"
}

@BindingAdapter("totalInCryptoCurrent")
fun TextView.setAssetDetailOverviewBindingAdapterTotalInCryptoCurrent(asset: Asset?) {
    text = if (asset == null) "" else "Total Today: ${asset.current.valueCrypto.toCurrencyString(Preferences.getCryptoTitleSymbol())} ${Preferences.getCryptoTitleSymbol()} (${asset.current.valueCrypto.asPercentOf(asset.open.valueCrypto).setScale(0, RoundingMode.HALF_UP).toPlainString()}%)"
}