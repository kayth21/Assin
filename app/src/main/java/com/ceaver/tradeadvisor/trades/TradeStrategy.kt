package com.ceaver.tradeadvisor.trades

enum class TradeStrategy {
    HODL {
        override fun test(purchasePrice: Double, currentPrice: Double): Boolean {
            return false
        }
    },
    DOUBLE_OUT {
        override fun test(purchasePrice: Double, currentPrice: Double): Boolean {
            return currentPrice >= purchasePrice * 2
        }
    },
    ASAP_NO_LOSSES {
        override fun test(purchasePrice: Double, currentPrice: Double): Boolean {
            return currentPrice >= purchasePrice
        }
    };

    abstract fun test(purchasePrice: Double, currentPrice: Double): Boolean

}