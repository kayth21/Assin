package com.ceaver.assin.assets.overview

import java.math.BigDecimal

data class AssetOverview (
        val btcValue: BigDecimal = BigDecimal.ZERO,
        val usdValue: BigDecimal = BigDecimal.ZERO
)