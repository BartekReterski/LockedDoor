package com.ld.lockeddoor.services

import androidx.core.app.NotificationCompat
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.app.PendingIntent
import android.content.Intent
import android.content.BroadcastReceiver
import android.content.Context
import com.ld.lockeddoor.activities.MainActivity
import java.util.*



class AlarmNotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {

        // pobranie danych z intenta z MainActivity
        val notificationId = intent.getIntExtra("notificationId", 0)

        // uruchomienie activity z powiadomienia
        val mainIntent = Intent(context, MainActivity::class.java)
        val contentIntent = PendingIntent.getActivity(context, 0, mainIntent, 0)

        // inicjalizacja notificationManager
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName: CharSequence = "My Notification"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, channelName, importance)
            Objects.requireNonNull(notificationManager).createNotificationChannel(channel)
        }

        // przygotowanie powiadomienia(konfiguracja)
        val builder: NotificationCompat.Builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(com.ld.lockeddoor.R.drawable.notification_icon_task)
            .setContentTitle("Remember about tasks before you go ")
            .setContentText("Click to show your tasks")
            .setContentIntent(contentIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
        Objects.requireNonNull(notificationManager).notify(notificationId, builder.build())
    }

    companion object {
        private const val CHANNEL_ID = "CHANNEL_SAMPLE"
    }
}