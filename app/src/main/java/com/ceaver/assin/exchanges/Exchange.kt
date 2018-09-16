package com.ceaver.assin.exchanges

import com.ceaver.assin.assets.Symbol
import com.ceaver.assin.assets.Symbol.*

enum class Exchange(val symbols: Set<Symbol>) {
    BITSTAMP(setOf(
            // USD
            BTC,
            // BTC
            BCH, ETH, LTC, XRP)) {

        override fun update(symbol: Symbol) {
            Bitstamp.update(symbol)
        }
    },

    BITTREX(setOf(
            // BTC
            PAY, XEM, DOGE, DGB, SC, GNO, EDG, ETH, XRP, XLM, LTC, BCH, ADA, XMR, DASH, ETC, NEO, ZRX, LSK, ZEC, WAVES, STEEM, XVG, OMG, BAT, REP, STRAT, TRX, GNT, SNT, KMD, MCO, NXS, POLY, CVC, STORJ, NAV
    )) {
        override fun update(symbol: Symbol) {
            Bittrex.update(symbol)
        }
    },


    KUCOIN(setOf(
            // BTC
            TRAC, KCS
    )) {
        override fun update(symbol: Symbol) {
            Kucoin.update(symbol)
        }
    },

    BINANCE(setOf(
            // BTC
            ETH, XRP, EOS, XLM, LTC, BCH, ADA, XMR, DASH, IOTA, BTS, ETC, NEO, VET, ONT, ZRX, NANO, LSK, ZEC, ICX, WAVES, STEEM, XVG, OMG, BAT, REP, HOT, STRAT, TRX, GNT, WTC, SNT, KMD, WAN, AION, LINK, ELF, MCO, NXS, NULS, SUB, POLY, CVC, STORJ, GVT, REQ, NCASH, NEBL, RDN, POE, SNM, POA, MOD, NAV)) {

        override fun update(symbol: Symbol) {
            Binance.update(symbol)
        }

    },
    ;

    abstract fun update(symbol: Symbol)

    fun contains(symbol: Symbol): Boolean {
        return symbols.contains(symbol)
    }

    companion object {
        fun getExchanges(symbol: Symbol): List<Exchange> {
            return Exchange.values().filter { it.contains(symbol) }
        }

    }
}