package com.ceaver.assin.markets

import com.ceaver.assin.assets.Symbol
import org.hamcrest.CoreMatchers
import org.junit.Assert
import org.junit.jupiter.api.Test

internal class MarketPathFinderTest {

    @Test
    fun fiatToBaseFiat() {
        // fiat to base fiat (and vice versa)
        assert(MarketPathFinder.findPath(Symbol.USD, Symbol.EUR), Symbol.USD to Symbol.EUR);
        assert(MarketPathFinder.findPath(Symbol.EUR, Symbol.USD), Symbol.EUR to Symbol.USD);
        // crypto to base crypto (and vice versa)
        assert(MarketPathFinder.findPath(Symbol.BTC, Symbol.ETH), Symbol.BTC to Symbol.ETH);
        assert(MarketPathFinder.findPath(Symbol.ETH, Symbol.BTC), Symbol.ETH to Symbol.BTC);
        // base crypto to base fiat (and vice versa)
        assert(MarketPathFinder.findPath(Symbol.BTC, Symbol.USD), Symbol.BTC to Symbol.USD);
        assert(MarketPathFinder.findPath(Symbol.USD, Symbol.BTC), Symbol.USD to Symbol.BTC);
        // fiat to fiat (and vice versa)
        assert(MarketPathFinder.findPath(Symbol.CHF, Symbol.EUR), Symbol.CHF to Symbol.USD, Symbol.USD to Symbol.EUR);
        assert(MarketPathFinder.findPath(Symbol.EUR, Symbol.CHF), Symbol.EUR to Symbol.USD, Symbol.USD to Symbol.CHF);
        // crypto to crypto (and vice versa)
        assert(MarketPathFinder.findPath(Symbol.AION, Symbol.WAVES), Symbol.AION to Symbol.BTC, Symbol.BTC to Symbol.WAVES);
        assert(MarketPathFinder.findPath(Symbol.WAVES, Symbol.AION), Symbol.WAVES to Symbol.BTC, Symbol.BTC to Symbol.AION);
        // crypto to fiat (and vice versa)
        assert(MarketPathFinder.findPath(Symbol.WAN, Symbol.CHF), Symbol.WAN to Symbol.BTC, Symbol.BTC to Symbol.USD, Symbol.USD to Symbol.CHF);
        assert(MarketPathFinder.findPath(Symbol.CHF, Symbol.WAN), Symbol.CHF to Symbol.USD, Symbol.USD to Symbol.BTC, Symbol.BTC to Symbol.WAN);
    }

    private fun assert(path: List<Pair<Symbol, Symbol>>, vararg expected : Pair<Symbol, Symbol> ) {
        Assert.assertThat(path, CoreMatchers.`is`(expected.toList()))
    }
}