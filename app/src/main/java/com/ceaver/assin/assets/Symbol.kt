package com.ceaver.assin.assets

import com.ceaver.assin.assets.Category.CRYPTO
import com.ceaver.assin.assets.Category.FIAT

enum class Symbol(val titleType: Category, val label: String) {
    USD(FIAT, "US Dollar"), EUR(FIAT, "Euro"), CHF(FIAT, "Schweizer Franken"),

    BTC(CRYPTO, "Bitcoin"),
    ETH(CRYPTO, "Ethereum"),
    XRP(CRYPTO, "XRP"),
    EOS(CRYPTO, "EOS"),
    XLM(CRYPTO, "Stellar"),
    LTC(CRYPTO, "Litecoin"),
    BCH(CRYPTO, "Bitcoin Cash"),
    ADA(CRYPTO, "Cardano"),
    XMR(CRYPTO, "Monero"),
    DASH(CRYPTO, "Dash"),
    IOTA(CRYPTO, "MIOTA"),
    TRX(CRYPTO, "TRON"),
    ETC(CRYPTO, "Ethereum Classic"),
    NEO(CRYPTO, "NEO"),
    XEM(CRYPTO, "NEM"),
    VET(CRYPTO, "VeChain"),
    ONT(CRYPTO, "Ontology"),
    ZRX(CRYPTO, "0x"),
    NANO(CRYPTO, "Nano"),
    LSK(CRYPTO, "Lisk"),
    DOGE(CRYPTO, "Dogecoin"),
    ZEC(CRYPTO, "Zcash"),
    BTS(CRYPTO, "Bitshares"),
    DGB(CRYPTO, "Digibyte"),
    ICX(CRYPTO, "ICON"),
    WAVES(CRYPTO, "WAVES"),
    STEEM(CRYPTO, "STEEM"),
    XVG(CRYPTO, "Verge"),
    SC(CRYPTO, "Siacoin"),
    OMG(CRYPTO, "OmiseGO"),
    BAT(CRYPTO, "Basic Attention Token"),
    REP(CRYPTO, "Augur"),
    HOT(CRYPTO, "Holo"),
    STRAT(CRYPTO, "Stratis"),
    GNT(CRYPTO, "Golem"),
    WTC(CRYPTO, "Waltonchain"),
    SNT(CRYPTO, "Status"),
    KMD(CRYPTO, "Komodo"),
//    MAID(CRYPTO, "MaidSafeCoin"),
    KCS(CRYPTO, "KuCoin Shares"),
    WAN(CRYPTO, "Wanchain"),
    AION(CRYPTO, "Aion"),
    LINK(CRYPTO, "Chainlink"),
    ELF(CRYPTO, "aelf"),
    PAY(CRYPTO, "TenX"),
    MCO(CRYPTO, "MCO"),
    NXS(CRYPTO, "Nexus"),
    NULS(CRYPTO, "Nuls"),
    SUB(CRYPTO, "Substratum"),
    POLY(CRYPTO, "Polymath"),
    CVC(CRYPTO, "Civic"),
    STORJ(CRYPTO, "Storj"),
    GVT(CRYPTO, "Genesis Vision"),
    GNO(CRYPTO, "Gnosis"),
    REQ(CRYPTO, "Request Network"),
    NCASH(CRYPTO, "Nucleus Vision"),
    NEBL(CRYPTO, "Neblio"),
    RDN(CRYPTO, "Raiden Network"),
    POE(CRYPTO, "Po.et"),
    EDG(CRYPTO, "Edgeless"),
    SNM(CRYPTO, "SONM"),
    POA(CRYPTO, "POA Network"),
    MOD(CRYPTO, "Modum"),
    NAV(CRYPTO, "NavCoin"),
    TRAC(CRYPTO, "OriginTrail"),
    ;

    fun isBtc() : Boolean{
        return isSymbol(BTC)
    }

    fun isUsd() : Boolean{
        return isSymbol(USD)
    }

    fun isSymbol(symbol: Symbol) : Boolean{
        return symbol == this
    }

    fun isCrypto() : Boolean {
        return Category.CRYPTO == this.titleType
    }

    fun isFiat() : Boolean {
        return Category.FIAT == this.titleType
    }

    companion object {
        fun values(titleType: Category): List<Symbol> {
            return Symbol.values().filter { it.titleType == titleType }
        }

    }
}

