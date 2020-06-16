package com.ceaver.assin.assets

import java.math.BigDecimal

data class Asset(//
        val name: String,
        val symbol: String,
        val amount: BigDecimal,
        val btcValue: BigDecimal,
        val usdValue: BigDecimal
)
