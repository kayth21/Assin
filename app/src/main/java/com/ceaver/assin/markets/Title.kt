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
        @ColumnInfo(name = "lastUpdated") val lastUpdated: LocalDateTime,
        @ColumnInfo(name = "active") val active: Integer
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

    fun getPercentChange1hString(): String {
        return if (percentChange1h.isPresent) "%.1f".format(percentChange1h.get()) else "N/A"
    }

    fun getPercentChange24hString(): String {
        return if (percentChange24h.isPresent) "%.1f".format(percentChange24h.get()) else "N/A"
    }

    fun getPercentChange7dString(): String {
        return if (percentChange7d.isPresent) "%.1f".format(percentChange7d.get()) else "N/A"
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