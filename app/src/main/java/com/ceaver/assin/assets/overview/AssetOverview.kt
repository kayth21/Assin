package com.ceaver.assin.assets.overview

import java.math.BigDecimal

data class AssetOverview (
        val valueCrypto: BigDecimal = BigDecimal.ZERO,
        val valueFiat: BigDecimal = BigDecimal.ZERO
)