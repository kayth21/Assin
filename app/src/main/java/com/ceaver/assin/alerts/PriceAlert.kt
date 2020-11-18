package com.ceaver.assin.alerts

import com.ceaver.assin.extensions.toCurrencyString
import com.ceaver.assin.markets.Title
import com.ceaver.assin.markets.TitlePrice
import com.ceaver.assin.markets.TitleRepository
import org.apache.commons.csv.CSVRecord
import java.math.BigDecimal

data class PriceAlert(
        override val id: Long = 0,
        override val active: Boolean = true,
        val baseTitle: Title,
        val quoteTitle: Title,
        override val last: BigDecimal,
        override val target: BigDecimal,
        override val diff: BigDecimal?
) : Alert {

    companion object Factory {
        fun fromDto(alertDto: AlertDto): PriceAlert {
            require(AlertType.PRICE == alertDto.alert.type)
            return PriceAlert(
                    id = alertDto.alert.id,
                    active = alertDto.alert.active,
                    baseTitle = alertDto.baseTitle!!.toTitle(),
                    quoteTitle = alertDto.quoteTitle!!.toTitle(),
                    last = alertDto.alert.last,
                    target = alertDto.alert.target,
                    diff = alertDto.alert.diff
            )
        }

        suspend fun fromImport(csvRecord: CSVRecord): PriceAlert {
            require(AlertType.PRICE.name == csvRecord.get(0))
            return PriceAlert(
                    active = csvRecord.get(1).toBoolean(),
                    baseTitle = TitleRepository.loadById(csvRecord.get(2)),
                    quoteTitle = TitleRepository.loadById(csvRecord.get(3)),
                    last = csvRecord.get(4).toBigDecimal(),
                    target = csvRecord.get(5).toBigDecimal(),
                    diff = csvRecord.get(6).toBigDecimalOrNull()
            )
        }
    }

    override fun toEntity(): AlertEntity {
        return AlertEntity(
                id = id,
                type = AlertType.PRICE,
                active = active,
                baseTitleId = baseTitle.id,
                quoteTitleId = quoteTitle.id,
                last = last,
                target = target,
                diff = diff
        )
    }

    override fun toExport(): List<String> {
        return listOf(
                AlertType.PRICE.name,
                active.toString(),
                baseTitle.id,
                quoteTitle.id,
                last.toPlainString(),
                target.toPlainString(),
                diff?.toPlainString() ?: ""
        )
    }

    override suspend fun lookupCurrent(): BigDecimal {
        return TitlePrice.lookupPrice(baseTitle, quoteTitle).toBigDecimal()
    }

    override fun getBaseImageResource(): Int = baseTitle.getIcon()
    override fun getBaseName(): String = baseTitle.name
    override fun getQuoteNameShort(): String = quoteTitle.symbol

    override fun getListRowTitleText(): String = "${baseTitle.name} (${baseTitle.symbol})${if (active) "" else " (inactive)"}"
    override fun getListRowSubtitleText(): String = "Last: ${last.toCurrencyString(quoteTitle.symbol)} ${quoteTitle.symbol}"
    override fun getListRowTypeText(): String = "Target Price"
    override fun getListRowTargetText(): String = "${if (diff == null) "$target" else "${target - diff} / ${target + diff}"} ${quoteTitle.symbol}"

    override fun copyWithCurrent(current: BigDecimal): Alert = copy(last = current)
    override fun copyWithCurrentAndDeactivated(current: BigDecimal): Alert = copy(last = current, active = false)
    override fun copyWithCurrentAndTarget(current: BigDecimal, target: BigDecimal): Alert = copy(last = current, target = target)
}