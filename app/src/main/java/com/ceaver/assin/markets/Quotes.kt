package com.ceaver.assin.markets

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Quotes(
        val price: Double,
        val volume24h: Double? = null,
        val marketCap: Double? = null,
        val marketCapChange24h: Double? = null,
        val percentChange1h: Double? = null,
        val percentChange24h: Double? = null,
        val percentChange7d: Double? = null,
        val percentChange30d: Double? = null,
        val percentChange1y: Double? = null
) : Parcelable