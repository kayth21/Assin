package com.ceaver.assin.alerts

import com.ceaver.assin.notification.AssinNotification
import java.math.BigDecimal

interface Alert {
    val id: Long
    val active: Boolean
    val last: BigDecimal
    val target: BigDecimal
    val diff: BigDecimal?

    fun toEntity(): AlertEntity
    fun toExport(): List<String>

    fun getBaseImageResource(): Int
    fun getQuoteImageResource(): Int
    fun getBaseName(): String
    fun getQuoteNameShort(): String

    fun getListRowTitleText(): String
    fun getListRowSubtitleText(): String
    fun getListRowTypeText(): String
    fun getListRowTargetText(): String

    suspend fun lookupCurrent(): BigDecimal

    fun copyWithCurrent(current: BigDecimal): Alert
    fun copyWithCurrentAndTarget(current: BigDecimal, target: BigDecimal): Alert
    fun copyWithCurrentAndDeactivated(current: BigDecimal): Alert

    suspend fun evaluate(): Pair<Alert, AssinNotification?> {
        val current = lookupCurrent()

        if (!active)
            return Pair(copyWithCurrent(current), null)

        fun createUpperNotification(target: BigDecimal) =
                AlertNotification.upperTarget(getBaseImageResource(), "${getBaseName()} up", "Target of $target ${getQuoteNameShort()} reached.")

        fun createLowerNotification(target: BigDecimal) =
                AlertNotification.lowerTarget(getBaseImageResource(), "${getBaseName()} down", "Target of $target ${getQuoteNameShort()} reached.")

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