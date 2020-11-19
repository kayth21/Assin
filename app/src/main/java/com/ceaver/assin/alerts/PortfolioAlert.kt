package com.ceaver.assin.alerts

import com.ceaver.assin.R
import com.ceaver.assin.assets.overview.AssetOverviewValue
import com.ceaver.assin.extensions.toCurrencyString
import com.ceaver.assin.markets.Title
import com.ceaver.assin.markets.TitleRepository
import org.apache.commons.csv.CSVRecord
import java.math.BigDecimal

data class PortfolioAlert(
        override val id: Long = 0,
        override val active: Boolean,
        val quoteTitle: Title,
        override val last: BigDecimal,
        override val target: BigDecimal,
        override val diff: BigDecimal?
) : Alert {

    companion object Factory {
        fun fromDto(alertDto: AlertDto): PortfolioAlert {
            require(AlertType.PORTFOLIO == alertDto.alert.type)
            return PortfolioAlert(
                    id = alertDto.alert.id,
                    active = alertDto.alert.active,
                    quoteTitle = alertDto.quoteTitle!!.toTitle(),
                    last = alertDto.alert.last,
                    target = alertDto.alert.target,
                    diff = alertDto.alert.diff
            )
        }

        suspend fun fromImport(csvRecord: CSVRecord): PortfolioAlert {
            require(AlertType.PORTFOLIO.name == csvRecord.get(0))
            return PortfolioAlert(
                    active = csvRecord.get(1).toBoolean(),
                    quoteTitle = TitleRepository.loadById(csvRecord.get(2)),
                    last = csvRecord.get(3).toBigDecimal(),
                    target = csvRecord.get(4).toBigDecimal(),
                    diff = csvRecord.get(5).toBigDecimalOrNull()
            )
        }
    }

    override fun toEntity(): AlertEntity {
        return AlertEntity(
                id = id,
                type = AlertType.PORTFOLIO,
                active = active,
                quoteTitleId = quoteTitle.id,
                last = last,
                target = target,
                diff = diff
        )
    }

    override fun toExport(): List<String> {
        return listOf(
                AlertType.PORTFOLIO.name,
                active.toString(),
                quoteTitle.id,
                last.toPlainString(),
                target.toPlainString(),
                diff?.toPlainString() ?: ""
        )
    }


    override suspend fun lookupCurrent(): BigDecimal {
        return AssetOverviewValue.lookupPrice(quoteTitle)
    }

    override fun getBaseImageResource(): Int = R.drawable.polis
    override fun getQuoteImageResource(): Int = quoteTitle.getIcon()
    override fun getBaseName(): String = "Portfolio"
    override fun getQuoteNameShort(): String = quoteTitle.symbol

    override fun getListRowTitleText(): String = "${getBaseName()}/${quoteTitle.symbol}${if (active) "" else " (inactive)"}"
    override fun getListRowSubtitleText(): String = "Portfolio Alert"
    override fun getListRowTypeText(): String = "Last: ${last.toCurrencyString(quoteTitle.symbol)} ${quoteTitle.symbol}"
    override fun getListRowTargetText(): String = "Target: ${if (diff == null) "$target" else "${target - diff} / ${target + diff}"} ${quoteTitle.symbol}"

    override fun copyWithCurrent(current: BigDecimal): Alert = copy(last = current)
    override fun copyWithCurrentAndDeactivated(current: BigDecimal): Alert = copy(last = current, active = false)
    override fun copyWithCurrentAndTarget(current: BigDecimal, target: BigDecimal): Alert = copy(last = current, target = target)
}