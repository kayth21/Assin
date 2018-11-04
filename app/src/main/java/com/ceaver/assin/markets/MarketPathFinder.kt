package com.ceaver.assin.markets

import com.ceaver.assin.assets.Symbol

object MarketPathFinder {

    fun findPath(symbol: Symbol, reference: Symbol): List<Pair<Symbol, Symbol>> {
        val result = mutableListOf<Pair<Symbol, Symbol>>()

        // convert any crypto to BTC or any fiat to USD
        if (symbol.isCrypto() && !symbol.isBtc()) {
            result.add(symbol to Symbol.BTC)
        }
        if (symbol.isFiat() && !symbol.isUsd()) {
            result.add(symbol to Symbol.USD)
        }

        // bridge crypto to fiat (or vice versa)
        if (symbol.isCrypto() && reference.isFiat()) {
            result.add(Symbol.BTC to Symbol.USD)
        }
        if (symbol.isFiat() && reference.isCrypto()) {
            result.add(Symbol.USD to Symbol.BTC)
        }

        // convert any reference crypto to BTC or any reference fiat to USD
        if (reference.isCrypto() && !reference.isBtc()) {
            result.add(Symbol.BTC to reference)
        }
        if (reference.isFiat() && !reference.isUsd()) {
            result.add(Symbol.USD to reference)
        }

        // TODO extend this for coins which are not traded against BTC, e.g. TRAC -> ETH -> BTC -> USD -> CHF

        return result
    }
}