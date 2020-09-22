package com.ceaver.assin.markets

import android.os.Parcelable
import androidx.recyclerview.widget.DiffUtil
import androidx.room.Embedded
import com.ceaver.assin.AssinApplication
import com.ceaver.assin.R
import com.ceaver.assin.assets.AssetCategory
import com.ceaver.assin.extensions.resIdByName
import kotlinx.android.parcel.Parcelize
import java.time.LocalDateTime
import java.util.*

@Parcelize
data class Title(//
        // common
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
        // primary
        @Embedded(prefix = "crypto")
        val cryptoQuotes: Quotes,
        // secondary
        @Embedded(prefix = "fiat")
        val fiatQuotes: Quotes
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

    fun getPercentChange1hString(): String {
        return "%.1f".format(cryptoQuotes.percentChange1h) // TODO let the user decide if primary or secondary
    }

    fun getPercentChange24hString(): String {
        return "%.1f".format(cryptoQuotes.percentChange24h) // TODO let the user decide if primary or secondary
    }

    fun getPercentChange7dString(): String {
        return "%.1f".format(cryptoQuotes.percentChange7d) // TODO let the user decide if primary or secondary
    }

    // TODO really??
    override fun toString(): String {
        return "$symbol ($name)"
    }

    fun getIcon(): Int {
        val identifier = AssinApplication.appContext!!.resIdByName(symbol.toLowerCase(Locale.ROOT), "drawable")
        return if (identifier == 0 || Character.isDigit(symbol[0])) R.drawable.generic else identifier // TODO some weird bug with symols that start with a number, e.g. 42
    }

    fun toEntity(): TitleEntity {
        return TitleEntity(
                id = id,
                symbol = symbol,
                active = active,
                betaValue = betaValue,
                category = category,
                circulatingSupply = circulatingSupply,
                cryptoQuotes = cryptoQuotes,
                fiatQuotes = fiatQuotes,
                lastUpdated = lastUpdated,
                maxSupply = maxSupply,
                name = name,
                rank = rank,
                totalSupply = totalSupply
        )
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