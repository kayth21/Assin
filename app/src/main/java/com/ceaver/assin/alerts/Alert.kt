package com.ceaver.assin.alerts

import com.ceaver.assin.common.Exportable
import com.ceaver.assin.extensions.asCurrencyString
import com.ceaver.assin.markets.Title
import com.ceaver.assin.notification.AssinNotification
import java.math.BigDecimal

interface Alert : Exportable {
    val id: Long
    val active: Boolean
    val quoteTitle: Title
    val last: BigDecimal
    val target: BigDecimal
    val diff: BigDecimal?

    fun toEntity(): AlertEntity

    fun getBaseImageResource(): Int
    fun getQuoteImageResource(): Int

    fun getNotificationTitle(direction: String): String
    fun getNotificationContent(target: BigDecimal): String

    fun getAlertType(): String
    fun getBaseText(): String

    fun getListRowTitleText(): String = "${getBaseText()}/${quoteTitle.symbol}${if (active) "" else " (inactive)"}"
    fun getListRowSubtitleText(): String = "${getAlertType()} Alert"
    fun getListRowLastText(): String = "Last: ${last.asCurrencyString(quoteTitle)}"
    fun getListRowTargetText(): String = "Target: ${if (diff == null) "$target" else "${target - diff!!} / ${target + diff!!}"} ${quoteTitle.symbol}"

    fun copyWithCurrent(current: BigDecimal): Alert
    fun copyWithCurrentAndTarget(current: BigDecimal, target: BigDecimal): Alert
    fun copyWithCurrentAndDeactivated(current: BigDecimal): Alert

    suspend fun lookupCurrent(): BigDecimal

    suspend fun evaluate(): Pair<Alert, AssinNotification?> {
        val current = lookupCurrent()

        if (!active)
            return Pair(copyWithCurrent(current), null)

        fun createUpperNotification(target: BigDecimal) =
                AlertNotification.upperTarget(getBaseImageResource(), getNotificationTitle("Up"), getNotificationContent(target))

        fun createLowerNotification(target: BigDecimal) =
                AlertNotification.lowerTarget(getBaseImageResource(), getNotificationTitle("Down"), getNotificationContent(target))

        return when (diff) {
            null -> { // one time alerts
                when {
                    last < target && current >= target -> Pair(copyWithCurrentAndDeactivated(current), createUpperNotification(target))
                    last > target && current <= target -> Pair(copyWithCurrentAndDeactivated(current), createLowerNotification(target))
                    else -> Pair(copyWithCurrent(current), null)
                }
            }
            else -> { // recurring alerts
                val upperTarget = target + diff!!
                val lowerTarget = target - diff!!
                when {
                    current >= upperTarget -> Pair(copyWithCurrentAndTarget(current, upperTarget), createUpperNotification(upperTarget))
                    current <= lowerTarget -> Pair(copyWithCurrentAndTarget(current, lowerTarget), createLowerNotification(lowerTarget))
                    else -> Pair(copyWithCurrent(current), null)
                }
            }
        }
    }
}