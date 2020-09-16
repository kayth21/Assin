package com.ceaver.assin.markets

import android.os.Parcelable
import androidx.recyclerview.widget.DiffUtil
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.ceaver.assin.AssinApplication
import com.ceaver.assin.R
import com.ceaver.assin.assets.AssetCategory
import com.ceaver.assin.extensions.resIdByName
import kotlinx.android.parcel.Parcelize
import java.time.LocalDateTime
import java.util.*

@Parcelize
@Entity(tableName = "title", indices = [Index(value = ["symbol", "rank"])]) // TODO What is this index good for? https://developer.android.com/training/data-storage/room/defining-data
data class Title(//
        // common
        @PrimaryKey
        val id: String,
        val name: String,
        val symbol: String,
        val category: AssetCategory,
        val active: Int,
        // common crypto
        val rank: Int = -1,
        val circulatingSupply: Long? = null,
        val totalSupply: Long? = null,
        val maxSupply: Long? = null,
        val betaValue: Double? = null,
        val lastUpdated: LocalDateTime? = null,
        // usd
        val priceUsd: Double? = null,
        val volume24hUsd: Double? = null,
        val marketCapUsd: Double? = null,
        val marketCapChange24hUsd: Double? = null,
        val percentChange1hUsd: Double? = null,
        val percentChange24hUsd: Double? = null,
        val percentChange7dUsd: Double? = null,
        val percentChange30dUsd: Double? = null,
        val percentChange1yUsd: Double? = null,
        val athPriceUsd: Double? = null,
        val athDateUsd: LocalDateTime? = null,
        val athPercentUsd: Double? = null,
        // btc
        val priceBtc: Double? = null,
        val volume24hBtc: Double? = null,
        val marketCapBtc: Double? = null,
        val marketCapChange24hBtc: Double? = null,
        val percentChange1hBtc: Double? = null,
        val percentChange24hBtc: Double? = null,
        val percentChange7dBtc: Double? = null,
        val percentChange30dBtc: Double? = null,
        val percentChange1yBtc: Double? = null,
        val athPriceBtc: Double? = null,
        val athDateBtc: LocalDateTime? = null,
        val athPercentBtc: Double? = null
) : Parcelable {

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

    override fun toString(): String {
        return "$symbol ($name)"
    }

    fun getIcon(): Int {
        val identifier = AssinApplication.appContext!!.resIdByName(symbol.toLowerCase(Locale.ROOT), "drawable")
        return if (identifier == 0 || Character.isDigit(symbol[0])) R.drawable.generic else identifier // TODO some weird bug with symols that start with a number, e.g. 42
    }

    object Difference : DiffUtil.ItemCallback<Title>() {
        override fun areItemsTheSame(oldItem: Title, newItem: Title): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Title, newItem: Title): Boolean {
            return oldItem == newItem
        }
    }
}

