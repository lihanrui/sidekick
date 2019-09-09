package com.henryli.sidekick

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.*
import com.henryli.sidekick.AppConstants.CHANNEL_ID
import com.henryli.sidekick.AppConstants.NOTIFICATION_ID
import com.henryli.sidekick.geowifi.LocationWorker
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    val REPEATINTERVAL = 15L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val title = "Sidekick Notification"
        val content = "This is a sample sidekick notification."
        createNotificationChannel()
        createNotification(title, content)

        createRepeatWork()
    }

    fun createNotification(title: String, description: String) {
        var notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle(title)
            .setContentText(description)
//            .setStyle(NotificationCompat.BigPictureStyle()
//                .bigPicture(myBitmap))
            .build()
        with(NotificationManagerCompat.from(this)) {
            // notificationId is a unique int for each notification that you must define
            notify(NOTIFICATION_ID, notification)
        }
    }

    fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val mChannel = NotificationChannel(CHANNEL_ID, name, importance)
            mChannel.description = descriptionText
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(mChannel)
        }
    }

    private fun createRepeatWork() {
        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(true)
            .build()

        val uploadWorkRequest =
            PeriodicWorkRequestBuilder<LocationWorker>(REPEATINTERVAL, TimeUnit.MINUTES)
                .setBackoffCriteria(
                    BackoffPolicy.LINEAR,
                    OneTimeWorkRequest.DEFAULT_BACKOFF_DELAY_MILLIS,
                    TimeUnit.MILLISECONDS
                )
                .setConstraints(constraints)
                .build()
        WorkManager.getInstance().enqueue(uploadWorkRequest)
    }


}
