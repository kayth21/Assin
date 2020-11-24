package com.ceaver.assin.positions

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.ceaver.assin.extensions.asFormattedDateTime
import com.ceaver.assin.extensions.setInactive
import com.ceaver.assin.extensions.toCurrencyString
import com.ceaver.assin.preferences.Preferences
import java.math.RoundingMode

@BindingAdapter("positionImageResource")
fun ImageView.setPositionImageResource(position: Position) {
    setImageResource(position.title.getIcon())
    setInactive(position.isClosed())
}

@BindingAdapter("positionListRowPositionSize")
fun TextView.setPositionListRowPositionSize(position: Position) {
    text = "${position.quantity.toCurrencyString(position.title.symbol)} ${position.title.symbol}"
}

@BindingAdapter("positionListRowPositionDate")
fun TextView.setPositionListRowPositionDate(position: Position) {
    text = "${position.open.date.asFormattedDateTime()}${if (position.isClosed()) " - ${position.close!!.date.asFormattedDateTime()}" else ""}"
}

@BindingAdapter("positionListRowOpenPositionValueFiat")
fun TextView.setPositionListRowOpenPositionValueFiat(position: Position) {
    text = "Open: ${position.open.valueFiat.toCurrencyString(Preferences.getCryptoTitleSymbol())} ${Preferences.getFiatTitleSymbol()}"
}

@BindingAdapter("positionListRowOpenPositionValueCrypto")
fun TextView.setPositionListRowOpenPositionValueCrypto(position: Position) {
    text = "Open: ${position.open.valueCrypto.toCurrencyString(Preferences.getCryptoTitleSymbol())} ${Preferences.getCryptoTitleSymbol()}"
}

@BindingAdapter("positionListRowClosePositionValueFiat")
fun TextView.setPositionListRowClosePositionValueFiat(position: Position) {
    text = if (position.isClosed()) {
        "Close: ${position.close!!.valueFiat.toCurrencyString(Preferences.getFiatTitleSymbol())} ${Preferences.getFiatTitleSymbol()} (${position.profitLossInPercentToClosedFiatValue.setScale(0, RoundingMode.HALF_UP).toPlainString()}%)"
    } else {
        "Today: ${position.current.valueFiat.toCurrencyString(Preferences.getFiatTitleSymbol())} ${Preferences.getFiatTitleSymbol()} (${position.profitLossInPercentToFiatValue.setScale(0, RoundingMode.HALF_UP).toPlainString()}%)"
    }
}

@BindingAdapter("positionListRowClosePositionValueCrypto")
fun TextView.setPositionListRowClosePositionValueCrypto(position: Position) {
    text = if (position.isClosed()) {
        "Close: ${position.close!!.valueCrypto.toCurrencyString(Preferences.getCryptoTitleSymbol())} ${Preferences.getCryptoTitleSymbol()} (${position.profitLossInPercentToClosedCryptoTitle.setScale(0, RoundingMode.HALF_UP).toPlainString()}%)"
    } else {
        "Today: ${position.current.valueCrypto.toCurrencyString(Preferences.getCryptoTitleSymbol())} ${Preferences.getCryptoTitleSymbol()} (${position.profitLossInPercentToCryptoTitle.setScale(0, RoundingMode.HALF_UP).toPlainString()}%)"
    }
}
