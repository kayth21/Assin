package com.ceaver.assin.markets

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey
import com.ceaver.assin.assets.AssetCategory
import java.time.LocalDateTime

@Entity(tableName = "title", indices = [Index(value = ["symbol", "rank"])]) // TODO What is this index good for? https://developer.android.com/training/data-storage/room/defining-data
data class Title(//
        // common
        @ColumnInfo(name = "id") @PrimaryKey val id: String,
        @ColumnInfo(name = "name") val name: String,
        @ColumnInfo(name = "symbol") val symbol: String,
        @ColumnInfo(name = "category") val category: AssetCategory,
        @ColumnInfo(name = "active") val active: Integer,
        // common crypto
        @ColumnInfo(name = "rank") val rank: Int = -1,
        @ColumnInfo(name = "circulatingSupply") val circulatingSupply: Long? = null,
        @ColumnInfo(name = "totalSupply") val totalSupply: Long? = null,
        @ColumnInfo(name = "maxSupply") val maxSupply: Long? = null,
        @ColumnInfo(name = "betaValue") val betaValue: Double? = null,
        @ColumnInfo(name = "lastUpdated") val lastUpdated: LocalDateTime? = null,
        // usd
        @ColumnInfo(name = "priceUsd") val priceUsd: Double? = null,
        @ColumnInfo(name = "volume24hUsd") val volume24hUsd: Double? = null,
        @ColumnInfo(name = "marketCapUsd") val marketCapUsd: Double? = null,
        @ColumnInfo(name = "marketCapChange24hUsd") val marketCapChange24hUsd: Double? = null,
        @ColumnInfo(name = "percentChange1hUsd") val percentChange1hUsd: Double? = null,
        @ColumnInfo(name = "percentChange24hUsd") val percentChange24hUsd: Double? = null,
        @ColumnInfo(name = "percentChange7dUsd") val percentChange7dUsd: Double? = null,
        @ColumnInfo(name = "percentChange30dUsd") val percentChange30dUsd: Double? = null,
        @ColumnInfo(name = "percentChange1yUsd") val percentChange1yUsd: Double? = null,
        @ColumnInfo(name = "athPriceUsd") val athPriceUsd: Double? = null,
        @ColumnInfo(name = "athDateUsd") val athDateUsd: LocalDateTime? = null,
        @ColumnInfo(name = "athPercentUsd") val athPercentUsd: Double? = null,
        // btc
        @ColumnInfo(name = "priceBtc") val priceBtc: Double? = null,
        @ColumnInfo(name = "volume24hBtc") val volume24hBtc: Double? = null,
        @ColumnInfo(name = "marketCapBtc") val marketCapBtc: Double? = null,
        @ColumnInfo(name = "marketCapChange24hBtc") val marketCapChange24hBtc: Double? = null,
        @ColumnInfo(name = "percentChange1hBtc") val percentChange1hBtc: Double? = null,
        @ColumnInfo(name = "percentChange24hBtc") val percentChange24hBtc: Double? = null,
        @ColumnInfo(name = "percentChange7dBtc") val percentChange7dBtc: Double? = null,
        @ColumnInfo(name = "percentChange30dBtc") val percentChange30dBtc: Double? = null,
        @ColumnInfo(name = "percentChange1yBtc") val percentChange1yBtc: Double? = null,
        @ColumnInfo(name = "athPriceBtc") val athPriceBtc: Double? = null,
        @ColumnInfo(name = "athDateBtc") val athDateBtc: LocalDateTime? = null,
        @ColumnInfo(name = "athPercentBtc") val athPercentBtc: Double? = null,
        // eth
        @ColumnInfo(name = "priceEth") val priceEth: Double? = null,
        @ColumnInfo(name = "volume24hEth") val volume24hEth: Double? = null,
        @ColumnInfo(name = "marketCapEth") val marketCapEth: Double? = null,
        @ColumnInfo(name = "marketCapChange24hEth") val marketCapChange24hEth: Double? = null,
        @ColumnInfo(name = "percentChange1hEth") val percentChange1hEth: Double? = null,
        @ColumnInfo(name = "percentChange24hEth") val percentChange24hEth: Double? = null,
        @ColumnInfo(name = "percentChange7dEth") val percentChange7dEth: Double? = null,
        @ColumnInfo(name = "percentChange30dEth") val percentChange30dEth: Double? = null,
        @ColumnInfo(name = "percentChange1yEth") val percentChange1yEth: Double? = null,
        @ColumnInfo(name = "athPriceEth") val athPriceEth: Double? = null,
        @ColumnInfo(name = "athDateEth") val athDateEth: LocalDateTime? = null,
        @ColumnInfo(name = "athPercentEth") val athPercentEth: Double? = null
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

    override fun toString(): String {
        return "$symbol ($name)"
    }
}