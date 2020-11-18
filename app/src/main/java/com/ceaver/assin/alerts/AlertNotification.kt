package com.ceaver.assin.alerts

import com.ceaver.assin.R
import com.ceaver.assin.notification.AssinNotification

class AlertNotification(private val smallImage: Int, private val largeImage: Int, private val title: String, private val text: String) : AssinNotification() {
    override fun getSmallIcon(): Int = smallImage
    override fun getLargeIcon(): Int = largeImage
    override fun getContentTitle(): String = title
    override fun getContentText(): String = text
    override fun getChannelId(): String = CHANNEL_ID

    companion object {
        val CHANNEL_ID = AlertNotification::class.simpleName!!

        fun upperTarget(largeIcon: Int, title: String, text: String): AssinNotification {
            return target(R.drawable.uptrend_notification_icon, largeIcon, title, text)
        }

        fun lowerTarget(largeIcon: Int, title: String, text: String): AssinNotification {
            return target(R.drawable.downtrend_notification_icon, largeIcon, title, text)
        }

        private fun target(smallIcon: Int, largeIcon: Int, title: String, text: String): AssinNotification {
            return AlertNotification(smallIcon, largeIcon, title, text)
        }
    }
}