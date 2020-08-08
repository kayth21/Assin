package com.ceaver.assin.alerts

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.getSystemService
import com.ceaver.assin.MainActivity
import com.ceaver.assin.MyApplication
import com.ceaver.assin.R
import com.ceaver.assin.extensions.toCurrencyString
import com.ceaver.assin.markets.Title
import java.math.BigDecimal
import java.util.*


object AlertNotification {

    const val CHANNEL_ID = "alert"

    init {
        // Create the NotificationChannel, but only on API 26+ because the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Alert Channel"
            val description = "Notification if an alert reaches target"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance)
            channel.description = description
            // Register the channel with the system; you can't change the importance or other notification behaviors after this
            getSystemService(MyApplication.appContext!!, NotificationManager::class.java)!!.createNotificationChannel(channel)
        }
    }

    fun notify(symbol: Title, reference: Title, targetPrice: BigDecimal, currentPrice: BigDecimal) {

        val intent = Intent(MyApplication.appContext!!, MainActivity::class.java)
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK) // TODO back button must end in Markets
        val pendingIntent = PendingIntent.getActivity(MyApplication.appContext!!, 0, intent, 0)

        val notification = NotificationCompat.Builder(MyApplication.appContext!!, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_stat_name)
                .setLargeIcon(BitmapFactory.decodeResource(MyApplication.appContext!!.resources, symbol.getIcon()))
                .setContentTitle("$symbol " + (if (targetPrice <= currentPrice) "upper" else "lower") + " Target " + targetPrice.toPlainString() + " ${reference.symbol} reached.")
                .setContentText("Current Price: " + currentPrice.toCurrencyString(reference.symbol) + " ${reference.symbol}")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build()

        val notificationManager = NotificationManagerCompat.from(MyApplication.appContext!!)
        // notificationId is a unique int for each notification that you must define
        // Remember to save the notification ID that you pass to NotificationManagerCompat.notify() because you'll need it later if you want to update or remove the notification.
        notificationManager.notify(Random().nextInt(), notification);
    }
}