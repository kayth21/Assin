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
        @ColumnInfo(name = "id") @PrimaryKey val id: String,
        @ColumnInfo(name = "symbol") val symbol: String,
        @ColumnInfo(name = "category") val category: Category,
        @ColumnInfo(name = "name") val name: String,
        @ColumnInfo(name = "priceUsd") val priceUsd: Double,
        @ColumnInfo(name = "priceBtc") val priceBtc: Double,
        @ColumnInfo(name = "marketCapUsd") val marketCapUsd: Long,
        @ColumnInfo(name = "rank") val rank: Int,
        @ColumnInfo(name = "percentChange1h") val percentChange1h: Optional<Double>,
        @ColumnInfo(name = "percentChange24h") val percentChange24h: Optional<Double>,
        @ColumnInfo(name = "percentChange7d") val percentChange7d: Optional<Double>,
        @ColumnInfo(name = "lastUpdated") val lastUpdated: LocalDateTime) {


    fun getPercentChange1hString(): String {
        return if (percentChange1h.isPresent) "%.1f".format(percentChange1h.get()) else "N/A"
    }
    fun getPercentChange24hString(): String {
        return if (percentChange24h.isPresent) "%.1f".format(percentChange24h.get()) else "N/A"
    }
    fun getPercentChange7dString(): String {
        return if (percentChange7d.isPresent) "%.1f".format(percentChange7d.get()) else "N/A"
    }
}