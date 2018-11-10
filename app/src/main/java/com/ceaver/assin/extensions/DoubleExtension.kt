package com.ceaver.assin.extensions

import com.ceaver.assin.assets.Symbol

fun Double.format(symbol: Symbol): String {
    return if (symbol.isCrypto()) {
        val s = "%.8f".format(this)
        if (s.indexOf(".") < 0) s else s.replace(Regex("0*$"), "").replace(Regex("\\.$"), "")
    }
    else "%.2f".format(this)
}