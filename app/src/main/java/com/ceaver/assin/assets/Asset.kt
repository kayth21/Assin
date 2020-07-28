package com.ceaver.assin.assets

import com.ceaver.assin.markets.Title
import java.math.BigDecimal

data class Asset(//
        val title: Title,
        val amount: BigDecimal,
        val btcValue: BigDecimal,
        val usdValue: BigDecimal
)
