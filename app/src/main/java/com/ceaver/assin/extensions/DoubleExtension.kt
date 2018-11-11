package com.ceaver.assin.extensions

import com.ceaver.assin.assets.Symbol
import java.math.MathContext

fun Double.format(symbol: Symbol): String {
    return if (symbol.isCrypto()) {
        val s = "%.8f".format(this.toBigDecimal().round(MathContext(3)))
        if (!s.contains(".")) s else s.replace(Regex("0*$"), "").replace(Regex("\\.$"), "")
    }
    else {
        val s = "%.2f".format(this.toBigDecimal().round(MathContext(3)))
        if (!s.contains(".")) s else if((s.indexOf(".") < s.length-2) && !s.endsWith("00")) s else s.replace(Regex("0*$"), "").replace(Regex("\\.$"), "")
    }
}