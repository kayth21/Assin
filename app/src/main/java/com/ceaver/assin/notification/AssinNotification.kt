package com.ceaver.assin.notification

import android.app.PendingIntent
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.ceaver.assin.AssinApplication
import com.ceaver.assin.MainActivity
import java.util.*

private const val REQUEST_CODE = 0
private const val FLAGS = 0

abstract class AssinNotification() {

    abstract fun getSmallIcon(): Int
    abstract fun getLargeIcon(): Int
    abstract fun getContentTitle(): String
    abstract fun getContentText(): String
    abstract fun getChannelId(): String

    fun push() {
        val intent = Intent(AssinApplication.appContext!!, MainActivity::class.java)
        // intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK // TODO back button must end in Markets
        val pendingIntent = PendingIntent.getActivity(AssinApplication.appContext!!, REQUEST_CODE, intent, PendingIntent.FLAG_NO_CREATE) // TODO WHAT?

        val notification = NotificationCompat.Builder(AssinApplication.appContext!!, getChannelId())
                .setSmallIcon(getSmallIcon())
                .setLargeIcon(BitmapFactory.decodeResource(AssinApplication.appContext!!.resources, getLargeIcon()))
                .setContentTitle(getContentTitle())
                .setContentText(getContentText())
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(false)
                .build()

        NotificationManagerCompat.from(AssinApplication.appContext!!).notify(Random().nextInt(), notification);
    }
}