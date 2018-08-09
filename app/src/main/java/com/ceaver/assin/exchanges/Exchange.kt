package com.ceaver.assin.exchanges

enum class Exchange(val tradingPairs: Set<TradingPair>) {
    BITSTAMP(setOf(
            TradingPair.USD_BTC,
            TradingPair.USD_BTC,
            TradingPair.BTC_ETH)),

    BINANCE(setOf(
            TradingPair.BTC_LTC))
    ;

    fun getExchangesByTradingPair(tradingPair: TradingPair): List<Exchange> {
        return Exchange.values().filter { it.tradingPairs.contains(tradingPair) }
    }
}