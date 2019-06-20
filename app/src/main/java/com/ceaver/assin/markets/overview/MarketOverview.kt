package com.ceaver.assin.markets.overview

data class MarketOverview(
        val marketCapUsd: Long,
        val dailyMarketCapChange: Double,
        val marketCapAthValue: Long,
        val marketCapAthDate: String,
        val dailyVolumeUsd: Long,
        val dailyVolumeChange: Double,
        val volumeAthValue: Long,
        val volumeAthDate: String,
        val btcDominancePercentage: Double,
        val cryptocurrenciesAmount: Int,
        val lastUpdated: Long) {

    companion object {
        val SHARED_PREFERENCES_KEY = "com.ceaver.assin.markets.overview.MarketOverview.SharedPreferences"
        val MARKET_CAP_USD = "com.ceaver.assin.markets.overview.MarketOverview.marketCapUsd"
        val DAILY_MARKET_CAP_CHANGE = "com.ceaver.assin.markets.overview.MarketOverview.dailyMarketCapChange"
        val MARKET_CAP_ATH_VALUE = "com.ceaver.assin.markets.overview.MarketOverview.marketCapAthValue"
        val MARKET_CAP_ATH_DATE = "com.ceaver.assin.markets.overview.MarketOverview.marketCapAthDate"
        val DAILY_VOLUME_USD = "com.ceaver.assin.markets.overview.MarketOverview.dailyVolumeUsd"
        val DAILY_VOLUME_CHANGE = "com.ceaver.assin.markets.overview.MarketOverview.dailyVolumeChange"
        val VOLUME_ATH_VALUE = "com.ceaver.assin.markets.overview.MarketOverview.volumeAthValue"
        val VOLUME_ATH_DATE = "com.ceaver.assin.markets.overview.MarketOverview.volumeAthDate"
        val BTC_DOMINANCE_PERCENTAGE = "com.ceaver.assin.markets.overview.MarketOverview.btcDominancePercentage"
        val CRYPTOCURRENCIES_AMOUNT = "com.ceaver.assin.markets.overview.MarketOverview.cryptocurrenciesAmount"
        val LAST_UPDATED = "com.ceaver.assin.markets.overview.MarketOverview.lastUpdated"
    }
}