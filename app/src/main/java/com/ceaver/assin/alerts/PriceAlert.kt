package com.ceaver.assin.alerts

import com.ceaver.assin.extensions.toCurrencyString
import com.ceaver.assin.markets.Title
import com.ceaver.assin.markets.TitlePrice
import com.ceaver.assin.markets.TitleRepository
import com.ceaver.assin.notification.AssinNotification
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

    override fun update(): Pair<Alert, AssinNotification?> {
        val currentPrice = TitlePrice.lookupPrice(baseTitle, quoteTitle).toBigDecimal()

        if (!active)
            return Pair(copy(last = currentPrice), null)

        return when (diff) {
            null -> { // one time alerts
                when {
                    last < target && currentPrice >= target ->
                        Pair(copy(last = currentPrice, active = false), AlertNotification.upperTarget(baseTitle, target, quoteTitle))
                    last > target && currentPrice <= target ->
                        Pair(copy(last = currentPrice, active = false), AlertNotification.lowerTarget(baseTitle, target, quoteTitle))
                    else ->
                        Pair(copy(last = currentPrice), null)
                }
            }
            else -> { // recurring alerts
                val upperTarget = target + diff
                val lowerTarget = target - diff
                when {
                    currentPrice >= upperTarget ->
                        Pair(copy(last = currentPrice, target = upperTarget), AlertNotification.upperTarget(baseTitle, upperTarget, quoteTitle))
                    currentPrice <= (lowerTarget) ->
                        Pair(copy(last = currentPrice, target = lowerTarget), AlertNotification.lowerTarget(baseTitle, lowerTarget, quoteTitle))
                    else ->
                        Pair(copy(last = currentPrice), null)
                }
            }
        }
    }

    override fun getImageResource(): Int = baseTitle.getIcon()

    override fun getTitleText(): String = "${baseTitle.name} (${baseTitle.symbol})${if (active) "" else " (inactive)"}"

    override fun getTypeText(): String = "Target Price"

    override fun getTargetText(): String = "${if (diff == null) "$target" else "${target - diff} / ${target + diff}"} ${quoteTitle.symbol}"

    override fun getSubtitleText(): String = "Last: ${last.toCurrencyString(quoteTitle.symbol)} ${quoteTitle.symbol}"
}