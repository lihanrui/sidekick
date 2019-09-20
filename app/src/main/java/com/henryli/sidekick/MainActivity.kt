package com.henryli.sidekick

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.henryli.sidekick.geowifi.NetworkGeoController
import kotlinx.android.synthetic.main.activity_main.*

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

    }

    fun updateLocation(str : String){
        helloworld.text = str
    }

}
