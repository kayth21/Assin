package com.ceaver.assin.alerts

import com.ceaver.assin.R
import com.ceaver.assin.markets.Title
import com.ceaver.assin.notification.AssinNotification
import java.math.BigDecimal

class AlertNotification(private val smallImage: Int, private val largeImage: Int, private val title: String, private val text: String) : AssinNotification() {
    override fun getSmallIcon(): Int = smallImage
    override fun getLargeIcon(): Int = largeImage
    override fun getContentTitle(): String = title
    override fun getContentText(): String = text
    override fun getChannelId(): String = CHANNEL_ID

    companion object {
        val CHANNEL_ID = AlertNotification::class.simpleName!!

        fun upperTarget(baseTitle: Title, targetPrice: BigDecimal, quoteTitle: Title): AssinNotification {
            return target(R.drawable.uptrend_notification_icon, baseTitle, targetPrice, quoteTitle, "up")
        }

        fun lowerTarget(baseTitle: Title, targetPrice: BigDecimal, quoteTitle: Title): AssinNotification {
            return target(R.drawable.downtrend_notification_icon, baseTitle, targetPrice, quoteTitle, "down")
        }

        private fun target(smallIcon: Int, baseTitle: Title, targetPrice: BigDecimal, quoteTitle: Title, upOrDown: String): AssinNotification {
            return AlertNotification(
                    smallIcon,
                    baseTitle.getIcon(),
                    "${baseTitle.name} is $upOrDown!",
                    "Price target of $targetPrice ${quoteTitle.symbol} reached.")
        }
    }
}