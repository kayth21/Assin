package com.ceaver.assin.assets.detail.overview

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.ceaver.assin.assets.Asset
import com.ceaver.assin.extensions.asCurrencyString
import com.ceaver.assin.extensions.asPercentStringOf
import com.ceaver.assin.preferences.Preferences

@BindingAdapter("totalOfAsset")
fun TextView.setAssetDetailOverviewBindingAdapterTotalOfAsset(asset: Asset?) {
    text = if (asset == null) "" else asset.quantity.asCurrencyString(asset.title)
}

@BindingAdapter("totalInFiatOpen")
fun TextView.setAssetDetailOverviewBindingAdapterTotalInFiatOpen(asset: Asset?) {
    text = if (asset == null) "" else "Total Open: ${asset.open.valueFiat.asCurrencyString(Preferences.getFiatTitle())}"
}

@BindingAdapter("totalInCryptoOpen")
fun TextView.setAssetDetailOverviewBindingAdapterTotalInCryptoOpen(asset: Asset?) {
    text = if (asset == null) "" else "Total Open: ${asset.open.valueCrypto.asCurrencyString(Preferences.getCryptoTitle())}"
}

@BindingAdapter("totalInFiatCurrent")
fun TextView.setAssetDetailOverviewBindingAdapterTotalInFiatCurrent(asset: Asset?) {
    text = if (asset == null) "" else "Total Today: ${asset.current.valueFiat.asCurrencyString(Preferences.getFiatTitle())} (${asset.current.valueFiat.asPercentStringOf(asset.open.valueFiat)})"
}

@BindingAdapter("totalInCryptoCurrent")
fun TextView.setAssetDetailOverviewBindingAdapterTotalInCryptoCurrent(asset: Asset?) {
    text = if (asset == null) "" else "Total Today: ${asset.current.valueCrypto.asCurrencyString(Preferences.getCryptoTitle())} (${asset.current.valueCrypto.asPercentStringOf(asset.open.valueCrypto)})"
}