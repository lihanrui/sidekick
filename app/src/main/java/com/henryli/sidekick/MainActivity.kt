package com.henryli.sidekick

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.work.*
import com.henryli.sidekick.geowifi.LocationWorker
import com.henryli.sidekick.geowifi.NetworkGeoController
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    val REPEATINTERVAL = 15L
    private lateinit var notificationManager: NotificationUtils

    private lateinit var wifiAnalyzer: NetworkGeoController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        notificationManager = NotificationUtils(this)
        wifiAnalyzer = NetworkGeoController(this, notificationManager)

        val title = "Sidekick Notification"
        val content = "This is a sample sidekick notification."

        notificationManager.notify(title, content)

        wifiAnalyzer.analyzeWifiSituation()

//        createLocationRepeatWork()
    }

    private fun createLocationRepeatWork() {
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
        WorkManager.getInstance(this).enqueue(uploadWorkRequest)
    }


}
