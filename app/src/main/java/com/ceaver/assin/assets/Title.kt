package com.ceaver.assin.assets

import com.ceaver.assin.assets.TitleType.*

enum class Title(titleType: TitleType) {
    USD(FIAT),
    BTC(CRYPTO), ETH(CRYPTO), LTC(CRYPTO);
}