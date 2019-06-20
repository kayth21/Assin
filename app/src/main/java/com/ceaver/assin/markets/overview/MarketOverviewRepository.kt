package com.ceaver.assin.markets.overview

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Looper
import com.ceaver.assin.MyApplication
import com.ceaver.assin.threading.BackgroundThreadExecutor

object MarketOverviewRepository {

    fun loadMarketOverview(): MarketOverview {
        val sharedPreferences = MyApplication.appContext!!.getSharedPreferences(MarketOverview.SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE)
        return MarketOverview(
                marketCapUsd = sharedPreferences.getLong(MarketOverview.MARKET_CAP_USD, -1),
                dailyMarketCapChange = sharedPreferences.getString(MarketOverview.DAILY_MARKET_CAP_CHANGE, "")!!.toDouble(),
                marketCapAthValue = sharedPreferences.getLong(MarketOverview.MARKET_CAP_ATH_VALUE, -1),
                marketCapAthDate = sharedPreferences.getString(MarketOverview.MARKET_CAP_ATH_DATE, "")!!,
                dailyVolumeUsd = sharedPreferences.getLong(MarketOverview.DAILY_VOLUME_USD, -1),
                dailyVolumeChange = sharedPreferences.getString(MarketOverview.DAILY_VOLUME_CHANGE, "")!!.toDouble(),
                volumeAthValue = sharedPreferences.getLong(MarketOverview.VOLUME_ATH_VALUE, -1),
                volumeAthDate = sharedPreferences.getString(MarketOverview.VOLUME_ATH_DATE, "")!!,
                btcDominancePercentage = sharedPreferences.getString(MarketOverview.BTC_DOMINANCE_PERCENTAGE, "")!!.toDouble(),
                cryptocurrenciesAmount = sharedPreferences.getInt(MarketOverview.CRYPTOCURRENCIES_AMOUNT, -1),
                lastUpdated = sharedPreferences.getLong(MarketOverview.LAST_UPDATED, -1)
        )
    }

    fun loadMarketOverviewAsync(callbackInMainThread: Boolean, callback: (MarketOverview) -> Unit) {
        BackgroundThreadExecutor.execute {
            val marketOverview = loadMarketOverview()
            if (callbackInMainThread)
                Handler(Looper.getMainLooper()).post { callback.invoke(marketOverview) }
            else
                callback.invoke(marketOverview)
        }
    }

    @SuppressLint("ApplySharedPref")
    fun insertMarketOverview(marketOverview: MarketOverview) {
        MyApplication.appContext!!.getSharedPreferences(MarketOverview.SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE).edit()
                .putLong(MarketOverview.MARKET_CAP_USD, marketOverview.marketCapUsd)
                .putString(MarketOverview.DAILY_MARKET_CAP_CHANGE, marketOverview.dailyMarketCapChange.toString())
                .putLong(MarketOverview.MARKET_CAP_ATH_VALUE, marketOverview.marketCapAthValue)
                .putString(MarketOverview.MARKET_CAP_ATH_DATE, marketOverview.marketCapAthDate)
                .putLong(MarketOverview.DAILY_VOLUME_USD, marketOverview.dailyVolumeUsd)
                .putString(MarketOverview.DAILY_VOLUME_CHANGE, marketOverview.dailyVolumeChange.toString())
                .putLong(MarketOverview.VOLUME_ATH_VALUE, marketOverview.volumeAthValue)
                .putString(MarketOverview.VOLUME_ATH_DATE, marketOverview.volumeAthDate)
                .putString(MarketOverview.BTC_DOMINANCE_PERCENTAGE, marketOverview.btcDominancePercentage.toString())
                .putInt(MarketOverview.CRYPTOCURRENCIES_AMOUNT, marketOverview.cryptocurrenciesAmount)
                .putLong(MarketOverview.LAST_UPDATED, marketOverview.lastUpdated)
                .commit()
    }
}