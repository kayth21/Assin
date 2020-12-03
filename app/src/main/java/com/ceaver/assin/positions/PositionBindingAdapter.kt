package com.ceaver.assin.positions

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.ceaver.assin.extensions.asCurrencyString
import com.ceaver.assin.extensions.asFormattedDateTime
import com.ceaver.assin.extensions.setInactive
import com.ceaver.assin.preferences.Preferences

@BindingAdapter("positionImageResource")
fun ImageView.setPositionImageResource(position: Position) {
    setImageResource(position.title.getIcon())
    setInactive(position.isClosed())
}

@BindingAdapter("positionListRowPositionSize")
fun TextView.setPositionListRowPositionSize(position: Position) {
    text = position.quantity.asCurrencyString(position.title)
}

@BindingAdapter("positionListRowPositionDate")
fun TextView.setPositionListRowPositionDate(position: Position) {
    text = "${position.open.date.asFormattedDateTime()}${if (position.isClosed()) " - ${position.close!!.date.asFormattedDateTime()}" else ""}"
}

@BindingAdapter("positionListRowOpenPositionValueFiat")
fun TextView.setPositionListRowOpenPositionValueFiat(position: Position) {
    text = "Open: ${position.open.valueFiat.asCurrencyString(Preferences.getFiatTitle())}"
}

@BindingAdapter("positionListRowOpenPositionValueCrypto")
fun TextView.setPositionListRowOpenPositionValueCrypto(position: Position) {
    text = "Open: ${position.open.valueCrypto.asCurrencyString(Preferences.getCryptoTitle())}"
}

@BindingAdapter("positionListRowClosePositionValueFiat")
fun TextView.setPositionListRowClosePositionValueFiat(position: Position) {
    text = if (position.isClosed()) {
        "Close: ${position.close!!.valueFiat.asCurrencyString(Preferences.getFiatTitle())} (${position.profitLossInPercentToClosedFiatValue})"
    } else {
        "Today: ${position.current.valueFiat.asCurrencyString(Preferences.getFiatTitle())} (${position.profitLossInPercentToFiatValue})"
    }
}

@BindingAdapter("positionListRowClosePositionValueCrypto")
fun TextView.setPositionListRowClosePositionValueCrypto(position: Position) {
    text = if (position.isClosed()) {
        "Close: ${position.close!!.valueCrypto.asCurrencyString(Preferences.getCryptoTitle())} (${position.profitLossInPercentToClosedCryptoTitle})"
    } else {
        "Today: ${position.current.valueCrypto.asCurrencyString(Preferences.getCryptoTitle())} (${position.profitLossInPercentToCryptoTitle})"
    }
}
