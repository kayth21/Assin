package com.ceaver.assin.extensions

import java.math.MathContext

fun Double.format(symbol: String): String {
    return if (symbol == "USD" || symbol == "EUR" || symbol == "CHF") {
        val s = "%.2f".format(this.toBigDecimal().round(MathContext(3)))
        val b = if (!s.contains(".")) s else if ((s.indexOf(".") < s.length - 2) && !s.endsWith("00")) s else s.replace(Regex("0*$"), "").replace(Regex("\\.$"), "")
        b.toBigDecimal(MathContext(3)).toPlainString()
    } else {
        val s = "%.8f".format(this.toBigDecimal().round(MathContext(3)))
        if (!s.contains(".")) s else s.replace(Regex("0*$"), "").replace(Regex("\\.$"), "")
    }
}

fun Double.asFactor() : Double {
    return  (this + 100.0) / 100.0
}

