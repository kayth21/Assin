package com.ceaver.assin.exchanges

import com.ceaver.assin.assets.Title
import com.ceaver.assin.assets.Title.*

enum class TradingPair(title1: Title, title2: Title) {
    USD_BTC(USD, BTC),
    USD_ETH(USD, ETH),

    BTC_ETH(BTC, ETH),
    BTC_LTC(BTC, LTC);
}