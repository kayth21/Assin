package com.ceaver.assin.intentions

import com.ceaver.assin.R
import com.ceaver.assin.notification.AssinNotification

class IntentionNotification(private val intentionId: Long, private val smallImage: Int, private val largeImage: Int, private val title: String, private val text: String) : AssinNotification() {
    override fun getSmallIcon(): Int = smallImage
    override fun getLargeIcon(): Int = largeImage
    override fun getContentTitle(): String = title
    override fun getContentText(): String = text
    override fun getChannelId(): String = CHANNEL_ID

    companion object {
        val CHANNEL_ID = IntentionNotification::class.simpleName!! + ":CHANNEL_ID"
        val INTENTION_ID = IntentionNotification::class.simpleName!! + ":INTENTION_ID"
        val SNOOZE_ACTION = IntentionNotification::class.simpleName!! + ":SNOOZE_ACTION"

        fun near(intentionId: Long, largeIcon: Int, title: String, text: String): AssinNotification {
            return target(intentionId, largeIcon, title, text)
        }

        fun act(intentionId: Long, largeIcon: Int, title: String, text: String): AssinNotification {
            return target(intentionId, largeIcon, title, text)
        }

        private fun target(intentionId: Long, largeIcon: Int, title: String, text: String): AssinNotification {
            return IntentionNotification(intentionId, R.drawable.ic_stat_name, largeIcon, title, text)
        }
    }
}