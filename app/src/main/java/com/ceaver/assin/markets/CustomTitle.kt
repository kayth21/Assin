package com.ceaver.assin.markets

import com.ceaver.assin.R
import com.ceaver.assin.assets.AssetCategory
import com.coinpaprika.apiclient.entity.QuoteEntity
import kotlinx.android.parcel.Parcelize
import org.apache.commons.csv.CSVRecord
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@Parcelize
data class CustomTitle(
        override val id: String,
        override val symbol: String,
        override val name: String,
        override val lastUpdated: LocalDateTime,
        override val cryptoQuotes: Quotes,
        override val fiatQuotes: Quotes
) : Title {

    companion object {
        fun fromEntity(entity: TitleEntity): CustomTitle {
            return CustomTitle(
                    id = entity.id,
                    symbol = entity.symbol,
                    name = entity.name,
                    lastUpdated = entity.lastUpdated,
                    cryptoQuotes = entity.cryptoQuotes,
                    fiatQuotes = entity.fiatQuotes
            )
        }

        fun fromImport(csvRecord: CSVRecord, cryptoTitle: CryptoTitle): CustomTitle {
            return CustomTitle(
                    id = csvRecord.get(0),
                    symbol = csvRecord.get(1),
                    name = csvRecord.get(2),
                    lastUpdated = LocalDateTime.of(LocalDate.parse(csvRecord.get(3)), LocalTime.NOON),
                    cryptoQuotes = Quotes(price = csvRecord.get(4).toDouble() / cryptoTitle.fiatQuotes.price),
                    fiatQuotes = Quotes(price = csvRecord.get(4).toDouble())
            )
        }

        fun fromMarket(it: CustomTitle, cryptoEntity: QuoteEntity, fiatEntity: QuoteEntity): CustomTitle {
            return it.copy(
                    cryptoQuotes = Quotes(
                            price = it.fiatQuotes.price / cryptoEntity.price
                    ),
                    fiatQuotes = Quotes(
                            price = it.fiatQuotes.price / fiatEntity.price
                    )
            )
        }
    }

    override fun getIcon(): Int {
        return R.drawable.generic  // TODO
    }

    override fun toEntity(): TitleEntity {
        return TitleEntity(
                id = id,
                symbol = symbol,
                name = name,
                category = AssetCategory.CUSTOM,
                lastUpdated = lastUpdated,
                cryptoQuotes = cryptoQuotes,
                fiatQuotes = fiatQuotes
        )
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

    fun toExport(): List<String> {
        return listOf(
                id,
                symbol,
                name,
                lastUpdated.toString(),
                cryptoQuotes.price.toString(), // TODO actually it should call toExport on Quotes, but by now exporting titles makes only sense on CustomTitle, and by now the only property useful to export is price. By doing it like this, there is no need to write ,,,,,,, in the export files.
                fiatQuotes.price.toString()
        )
    }
}