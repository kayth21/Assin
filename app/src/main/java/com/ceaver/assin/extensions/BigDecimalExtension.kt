package com.ceaver.assin.extensions

import com.ceaver.assin.markets.Title
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.NumberFormat

fun BigDecimal.asCurrencyString(title: Title): String {
    val value = this.round(MathContext(3))
    val format = DecimalFormat("#,###.########");
    return "${format.format(value)} ${title.symbol}"
}

fun BigDecimal.asQuantityString(): String {
    val format = DecimalFormat("#,###.########");
    return format.format(this)
}

fun BigDecimal.asPercentString(): String {
    return NumberFormat.getPercentInstance().format(this.round(MathContext(0, RoundingMode.HALF_UP)))
}

fun BigDecimal.asPercentStringOf(other: BigDecimal): String {
    return this.divide(other,2, RoundingMode.HALF_UP).minus(BigDecimal.ONE).asPercentString()
}