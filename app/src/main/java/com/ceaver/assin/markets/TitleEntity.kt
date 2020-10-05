package com.ceaver.assin.markets

import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.ceaver.assin.assets.AssetCategory
import kotlinx.android.parcel.Parcelize
import java.time.LocalDateTime

@Parcelize
@Entity(tableName = "title", indices = [Index(value = ["symbol", "rank"])]) // TODO What is this index good for? https://developer.android.com/training/data-storage/room/defining-data
data class TitleEntity(//
        // common
        @PrimaryKey
        val id: String,
        val name: String,
        val symbol: String,
        val category: AssetCategory,
        val active: Int = 100,
        // common crypto
        val rank: Int = -1,
        val circulatingSupply: Long? = null,
        val totalSupply: Long? = null,
        val maxSupply: Long? = null,
        val betaValue: Double? = null,
        val lastUpdated: LocalDateTime,
        // primary
        @Embedded(prefix = "crypto")
        val cryptoQuotes: Quotes,
        // secondary
        @Embedded(prefix = "fiat")
        val fiatQuotes: Quotes
) : Parcelable {
        fun toTitle(): Title {
                return when (category) {
                        AssetCategory.CRYPTO -> CryptoTitle.fromEntity(this)
                        AssetCategory.FIAT -> FiatTitle.fromEntity(this)
                        AssetCategory.CUSTOM -> CustomTitle.fromEntity(this)
                }
        }
}