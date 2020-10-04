package com.ceaver.assin.markets

import com.ceaver.assin.AssinApplication
import com.ceaver.assin.R
import com.ceaver.assin.assets.AssetCategory
import com.ceaver.assin.extensions.resIdByName
import com.coinpaprika.apiclient.entity.FiatEntity
import com.coinpaprika.apiclient.entity.QuoteEntity
import kotlinx.android.parcel.Parcelize
import java.time.LocalDateTime
import java.util.*

@Parcelize
data class FiatTitle(
        override val id: String,
        override val symbol: String,
        override val name: String,
        override val lastUpdated: LocalDateTime,
        override val cryptoQuotes: Quotes,
        override val fiatQuotes: Quotes
) : Title {

    companion object {
        fun fromEntity(entity: TitleEntity) : FiatTitle {
            return FiatTitle(
                    id = entity.id,
                    symbol = entity.symbol,
                    name = entity.name,
                    lastUpdated = entity.lastUpdated,
                    cryptoQuotes = entity.cryptoQuotes,
                    fiatQuotes = entity.fiatQuotes
            )
        }

        fun fromMarket(fiatEntity: FiatEntity, cryptoQuote: QuoteEntity, fiatQuote: QuoteEntity): FiatTitle {
            return FiatTitle(
                    id = fiatEntity.id,
                    name = fiatEntity.name,
                    symbol = fiatEntity.symbol,
                    lastUpdated = LocalDateTime.now(),
                    cryptoQuotes = Quotes(1.0 * cryptoQuote.price), // TODO
                    fiatQuotes = Quotes(1.0 * fiatQuote.price)
            )
        }
    }

    override fun getIcon(): Int {
        val identifier = AssinApplication.appContext!!.resIdByName(symbol.toLowerCase(Locale.ROOT), "drawable")
        return if (identifier == 0 || Character.isDigit(symbol[0])) R.drawable.generic else identifier // TODO some weird bug with symols that start with a number, e.g. 42
    }

    override fun getPercentChange1hString(): String {
        return "%.1f".format(cryptoQuotes.percentChange1h) // TODO let the user decide if primary or secondary
    }

    override fun getPercentChange24hString(): String {
        return "%.1f".format(cryptoQuotes.percentChange24h) // TODO let the user decide if primary or secondary
    }

    override fun getPercentChange7dString(): String {
        return "%.1f".format(cryptoQuotes.percentChange7d) // TODO let the user decide if primary or secondary
    }

    override fun toEntity(): TitleEntity {
        return TitleEntity(
                id = id,
                name = name,
                symbol = symbol,
                category = AssetCategory.FIAT,
                lastUpdated = lastUpdated,
                cryptoQuotes = cryptoQuotes,
                fiatQuotes = fiatQuotes
        )
    }
}