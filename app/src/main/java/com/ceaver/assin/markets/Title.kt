package com.ceaver.assin.markets

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.ceaver.assin.assets.AssetCategory
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity(tableName = "title", indices = [Index(value = ["symbol", "rank"])]) // TODO What is this index good for? https://developer.android.com/training/data-storage/room/defining-data
data class Title(//
        // common
        @ColumnInfo(name = "id") @PrimaryKey val id: String,
        @ColumnInfo(name = "name") val name: String,
        @ColumnInfo(name = "symbol") val symbol: String,
        @ColumnInfo(name = "category") val category: AssetCategory,
        @ColumnInfo(name = "active") val active: Int,
        // common crypto
        @ColumnInfo(name = "rank") val rank: Int = -1,
        @ColumnInfo(name = "circulatingSupply") val circulatingSupply: Long? = null,
        @ColumnInfo(name = "totalSupply") val totalSupply: Long? = null,
        @ColumnInfo(name = "maxSupply") val maxSupply: Long? = null,
        @ColumnInfo(name = "betaValue") val betaValue: Double? = null,
        @ColumnInfo(name = "lastUpdated") val lastUpdated: LocalDateTime? = null,
        // usd
        @ColumnInfo(name = "priceUsd") val priceUsd: BigDecimal? = null,
        @ColumnInfo(name = "volume24hUsd") val volume24hUsd: BigDecimal? = null,
        @ColumnInfo(name = "marketCapUsd") val marketCapUsd: BigDecimal? = null,
        @ColumnInfo(name = "marketCapChange24hUsd") val marketCapChange24hUsd: BigDecimal? = null,
        @ColumnInfo(name = "percentChange1hUsd") val percentChange1hUsd: BigDecimal? = null,
        @ColumnInfo(name = "percentChange24hUsd") val percentChange24hUsd: BigDecimal? = null,
        @ColumnInfo(name = "percentChange7dUsd") val percentChange7dUsd: BigDecimal? = null,
        @ColumnInfo(name = "percentChange30dUsd") val percentChange30dUsd: BigDecimal? = null,
        @ColumnInfo(name = "percentChange1yUsd") val percentChange1yUsd: BigDecimal? = null,
        @ColumnInfo(name = "athPriceUsd") val athPriceUsd: BigDecimal? = null,
        @ColumnInfo(name = "athDateUsd") val athDateUsd: LocalDateTime? = null,
        @ColumnInfo(name = "athPercentUsd") val athPercentUsd: BigDecimal? = null,
        // btc
        @ColumnInfo(name = "priceBtc") val priceBtc: BigDecimal? = null,
        @ColumnInfo(name = "volume24hBtc") val volume24hBtc: BigDecimal? = null,
        @ColumnInfo(name = "marketCapBtc") val marketCapBtc: BigDecimal? = null,
        @ColumnInfo(name = "marketCapChange24hBtc") val marketCapChange24hBtc: BigDecimal? = null,
        @ColumnInfo(name = "percentChange1hBtc") val percentChange1hBtc: BigDecimal? = null,
        @ColumnInfo(name = "percentChange24hBtc") val percentChange24hBtc: BigDecimal? = null,
        @ColumnInfo(name = "percentChange7dBtc") val percentChange7dBtc: BigDecimal? = null,
        @ColumnInfo(name = "percentChange30dBtc") val percentChange30dBtc: BigDecimal? = null,
        @ColumnInfo(name = "percentChange1yBtc") val percentChange1yBtc: BigDecimal? = null,
        @ColumnInfo(name = "athPriceBtc") val athPriceBtc: BigDecimal? = null,
        @ColumnInfo(name = "athDateBtc") val athDateBtc: LocalDateTime? = null,
        @ColumnInfo(name = "athPercentBtc") val athPercentBtc: BigDecimal? = null,
        // eth
        @ColumnInfo(name = "priceEth") val priceEth: BigDecimal? = null,
        @ColumnInfo(name = "volume24hEth") val volume24hEth: BigDecimal? = null,
        @ColumnInfo(name = "marketCapEth") val marketCapEth: BigDecimal? = null,
        @ColumnInfo(name = "marketCapChange24hEth") val marketCapChange24hEth: BigDecimal? = null,
        @ColumnInfo(name = "percentChange1hEth") val percentChange1hEth: BigDecimal? = null,
        @ColumnInfo(name = "percentChange24hEth") val percentChange24hEth: BigDecimal? = null,
        @ColumnInfo(name = "percentChange7dEth") val percentChange7dEth: BigDecimal? = null,
        @ColumnInfo(name = "percentChange30dEth") val percentChange30dEth: BigDecimal? = null,
        @ColumnInfo(name = "percentChange1yEth") val percentChange1yEth: BigDecimal? = null,
        @ColumnInfo(name = "athPriceEth") val athPriceEth: BigDecimal? = null,
        @ColumnInfo(name = "athDateEth") val athDateEth: LocalDateTime? = null,
        @ColumnInfo(name = "athPercentEth") val athPercentEth: BigDecimal? = null
) {

    fun inactive(): Boolean {
        return active == -100
    }

    fun incrementActiveCounter(): Title {
        return when {
            active == 100 -> this
            active == 49 -> this.copy(active = 51)
            active == -50 -> this.copy(active = 50)
            else -> this.copy(active = active + 1)
        }
    }

    fun decreaseActiveCounter(): Title {
        return when {
            active == -100 -> this
            active == 51 -> this.copy(active = 49)
            active == 0 -> this.copy(active = -100)
            else -> this.copy(active = active - 1)
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