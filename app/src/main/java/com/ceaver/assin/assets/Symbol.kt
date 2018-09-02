package com.ceaver.assin.assets

import android.content.Context
import com.ceaver.assin.MyApplication
import com.ceaver.assin.assets.Category.*

enum class Symbol(val titleType: Category, val label: String) {
    USD(FIAT, "US Dollar"), EUR(FIAT, "Euro"), CHF(FIAT, "Schweizer Franken"),

    BTC(CRYPTO, "Bitcoin"),
    ETH(CRYPTO, "Ether"),
    XRP(CRYPTO, "XRP"),
    LTC(CRYPTO, "Litecoin"),
    BCH(CRYPTO, "Bitcoin Cash"),
    ADA(CRYPTO, "Cardano"),
    XMR(CRYPTO, "Monero"),
    DASH(CRYPTO, "Dash"),
    OMG(CRYPTO, "OmiseGO"),
    ONT(CRYPTO, "Ontology"),
    ZRX(CRYPTO, "0x"),
    NANO(CRYPTO, "Nano"),
    XLM(CRYPTO, "Stellar"),
    LSK(CRYPTO, "Lisk")
    ;

    companion object {
        fun values(titleType: Category): List<Symbol> {
            return Symbol.values().filter { it.titleType == titleType }
        }

    }
}

