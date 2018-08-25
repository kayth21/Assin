package com.ceaver.assin.exchanges

import com.ceaver.assin.assets.Symbol
import com.ceaver.assin.markets.Title

enum class Exchange(val symbols: Set<Symbol>) {
    BITSTAMP(setOf(
            // USD
            Symbol.BTC,
            // BTC
            Symbol.BCH, Symbol.ETH, Symbol.LTC, Symbol.XRP)) {

        override fun update(symbol : Symbol)  {
            Bitstamp.update(symbol)
        }
    },

    BINANCE(setOf(
            // BTC
            Symbol.LSK, Symbol.ADA, Symbol.XMR, Symbol.DASH, Symbol.OMG, Symbol.ONT, Symbol.ZRX, Symbol.NANO, Symbol.XLM)) {

        override fun update(symbol : Symbol)  {
            Binance.update(symbol)
        }

    },
    ;

    abstract fun update(symbol : Symbol)

    fun contains(symbol : Symbol): Boolean {
        return symbols.contains(symbol)
    }

    companion object {
        fun getExchanges(symbol : Symbol): List<Exchange> {
            return Exchange.values().filter { it.contains(symbol) }
        }

    }
}