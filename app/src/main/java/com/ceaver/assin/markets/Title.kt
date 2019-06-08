package com.ceaver.assin.markets

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey
import com.ceaver.assin.assets.Category
import java.time.LocalDateTime
import java.util.*

@Entity(tableName = "title", indices = arrayOf(Index(value = ["symbol", "rank"])))
data class Title(//
        // common
        @ColumnInfo(name = "id") @PrimaryKey val id: String,
        @ColumnInfo(name = "name") val name: String,
        @ColumnInfo(name = "symbol") val symbol: String,
        @ColumnInfo(name = "category") val category: Category,
        @ColumnInfo(name = "active") val active: Integer,
        // common crypto
        @ColumnInfo(name = "rank") val rank: Int = -1,
        @ColumnInfo(name = "circulatingSupply") val circulatingSupply: Long = -1,
        @ColumnInfo(name = "totalSupply") val totalSupply: Long = -1,
        @ColumnInfo(name = "maxSupply") val maxSupply: Long = -1,
        @ColumnInfo(name = "betaValue") val betaValue: Double = -1.0,
        @ColumnInfo(name = "lastUpdated") val lastUpdated: Optional<LocalDateTime>,
        // usd
        @ColumnInfo(name = "priceUsd") val priceUsd: Double = -1.0,
        @ColumnInfo(name = "volume24hUsd") val volume24hUsd: Double = -1.0,
        @ColumnInfo(name = "marketCapUsd") val marketCapUsd: Double = -1.0,
        @ColumnInfo(name = "marketCapChange24hUsd") val marketCapChange24hUsd: Double = -1.0,
        @ColumnInfo(name = "percentChange1hUsd") val percentChange1hUsd: Double = -1.0,
        @ColumnInfo(name = "percentChange24hUsd") val percentChange24hUsd: Double = -1.0,
        @ColumnInfo(name = "percentChange7dUsd") val percentChange7dUsd: Double = -1.0,
        @ColumnInfo(name = "percentChange30dUsd") val percentChange30dUsd: Double = -1.0,
        @ColumnInfo(name = "percentChange1yUsd") val percentChange1yUsd: Double = -1.0,
        @ColumnInfo(name = "athPriceUsd") val athPriceUsd: Double = -1.0,
        @ColumnInfo(name = "athDateUsd") val athDateUsd: Optional<LocalDateTime>,
        @ColumnInfo(name = "athPercentUsd") val athPercentUsd: Double = -1.0,
        // btc
        @ColumnInfo(name = "priceBtc") val priceBtc: Double = -1.0,
        @ColumnInfo(name = "volume24hBtc") val volume24hBtc: Double = -1.0,
        @ColumnInfo(name = "marketCapBtc") val marketCapBtc: Double = -1.0,
        @ColumnInfo(name = "marketCapChange24hBtc") val marketCapChange24hBtc: Double = -1.0,
        @ColumnInfo(name = "percentChange1hBtc") val percentChange1hBtc: Double = -1.0,
        @ColumnInfo(name = "percentChange24hBtc") val percentChange24hBtc: Double = -1.0,
        @ColumnInfo(name = "percentChange7dBtc") val percentChange7dBtc: Double = -1.0,
        @ColumnInfo(name = "percentChange30dBtc") val percentChange30dBtc: Double = -1.0,
        @ColumnInfo(name = "percentChange1yBtc") val percentChange1yBtc: Double = -1.0,
        @ColumnInfo(name = "athPriceBtc") val athPriceBtc: Double = -1.0,
        @ColumnInfo(name = "athDateBtc") val athDateBtc: Optional<LocalDateTime>,
        @ColumnInfo(name = "athPercentBtc") val athPercentBtc: Double = -1.0,
        // eth
        @ColumnInfo(name = "priceEth") val priceEth: Double = -1.0,
        @ColumnInfo(name = "volume24hEth") val volume24hEth: Double = -1.0,
        @ColumnInfo(name = "marketCapEth") val marketCapEth: Double = -1.0,
        @ColumnInfo(name = "marketCapChange24hEth") val marketCapChange24hEth: Double = -1.0,
        @ColumnInfo(name = "percentChange1hEth") val percentChange1hEth: Double = -1.0,
        @ColumnInfo(name = "percentChange24hEth") val percentChange24hEth: Double = -1.0,
        @ColumnInfo(name = "percentChange7dEth") val percentChange7dEth: Double = -1.0,
        @ColumnInfo(name = "percentChange30dEth") val percentChange30dEth: Double = -1.0,
        @ColumnInfo(name = "percentChange1yEth") val percentChange1yEth: Double = -1.0,
        @ColumnInfo(name = "athPriceEth") val athPriceEth: Double = -1.0,
        @ColumnInfo(name = "athDateEth") val athDateEth: Optional<LocalDateTime>,
        @ColumnInfo(name = "athPercentEth") val athPercentEth: Double = -1.0
) {

    fun inactive(): Boolean {
        return active.toInt() == -100
    }

    fun incrementActiveCounter(): Title {
        return when {
            active.toInt() == 100 -> this
            active.toInt() == 49 -> this.copy(active = Integer(51))
            active.toInt() == -50 -> this.copy(active = Integer(50))
            else -> this.copy(active = Integer(active.toInt() + 1))
        }
    }

    fun decreaseActiveCounter(): Title {
        return when {
            active.toInt() == -100 -> this
            active.toInt() == 51 -> this.copy(active = Integer(49))
            active.toInt() == 0 -> this.copy(active = Integer(-100))
            else -> this.copy(active = Integer(active.toInt() - 1))
        }
    }

    fun getPercentChange1hUsdString(): String {
        return "%.1f".format(percentChange1hUsd)
    }

    fun getPercentChange24hUsdString(): String {
        return "%.1f".format(percentChange24hUsd)
    }

    fun getPercentChange7dUsdString(): String {
        return "%.1f".format(percentChange7dUsd)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Title
        if (id != other.id) return false
        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}