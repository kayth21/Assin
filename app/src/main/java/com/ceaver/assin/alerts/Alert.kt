package com.ceaver.assin.alerts

import com.ceaver.assin.notification.AssinNotification
import java.math.BigDecimal

interface Alert {
    val id: Long
    val active: Boolean
    val last: BigDecimal
    val target: BigDecimal
    val diff: BigDecimal?

    fun update(): Pair<Alert, AssinNotification?>

    fun toEntity(): AlertEntity
    fun toExport(): List<String>

    fun getImageResource(): Int
    fun getTitleText(): String
    fun getSubtitleText(): String
    fun getTypeText(): String
    fun getTargetText(): String
}