package com.henryli.sidekick

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.graphics.Color
import androidx.core.app.NotificationCompat


class NotificationUtils(mContext: Context) {
    private val context = mContext

    private val PRIMARY_CHANNEL_ID = "geofence_notification_channel"

    private var mNotifyManager =
        context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager?

    val listIds = ArrayList<Int>()

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                PRIMARY_CHANNEL_ID,
                "Sidekick Wifi", NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.GREEN);
            notificationChannel.enableVibration(true);
            notificationChannel.setDescription("Sidekick App Notifications");
            mNotifyManager?.createNotificationChannel(notificationChannel);
        }
    }

    fun notify(title: String, text: String, notificationId : Int) {
        val notifyBuilder = getNotificationBuilder(title, text)
        mNotifyManager?.notify(notificationId, notifyBuilder.build())
        listIds.add(notificationId)
    }

    private fun getNotificationBuilder(title: String, text: String): NotificationCompat.Builder {
        val notifyBuilder = NotificationCompat.Builder(context, PRIMARY_CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(text)
            .setSmallIcon(R.drawable.geoicon);
        return notifyBuilder
    }

    fun deleteNotification(id : Int){
        mNotifyManager?.cancel(id)
    }

    fun deleteAllNotifications(){
        for (id in listIds){
            mNotifyManager?.cancel(id)
        }
    }
}