package com.ceaver.assin.extensions

import com.ceaver.assin.markets.Title

fun Double.asCurrencyString(title: Title): String {
    return toBigDecimal().asCurrencyString(title)
}

fun Double.asFactor(): Double {
    return (this + 100.0) / 100.0
}

