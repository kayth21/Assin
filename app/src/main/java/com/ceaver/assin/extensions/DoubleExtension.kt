package com.ceaver.assin.extensions

import com.ceaver.assin.assets.Symbol

fun Double.format(symbol: Symbol) : String = if (symbol.isCrypto()) "%.8f".format(this) else "%.2f".format(this)