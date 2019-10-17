package com.henryli.sidekick

import android.Manifest
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.henryli.sidekick.geowifi.NetworkGeoController
import com.henryli.sidekick.photomanagement.CameraFileMonitorService
import kotlinx.android.synthetic.main.activity_main.*


class HomeActivity : AppCompatActivity() {
    internal lateinit var notificationManager: NotificationUtils
    private lateinit var wifiAnalyzer: NetworkGeoController
    val PERMISSION_REQUEST_ACCESS_FINE_LOCATION = 1359
    val PERMISSION_REQUEST_ACCESS_EXTERNAL_STORAGE = 1421

    lateinit var cm: ConnectivityManager
    lateinit var networkCallback: ConnectivityManager.NetworkCallback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        notificationManager = NotificationUtils(this)
        wifiAnalyzer = NetworkGeoController(this, notificationManager)

        // Check permissions, and if location permissions granted
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            askLocationPermission()
        } else {
            wifiAnalyzer.analyzeWifiSituation()
            cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkRequest =
                NetworkRequest.Builder().addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                    .build()
            networkCallback = object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network?) {
                    // remove notification
                    this@HomeActivity.notificationManager.deleteAllNotifications()
                    super.onAvailable(network)
                }

                override fun onLost(network: Network?) {
                    // if in wifi zone, ask why
                    // if outside wifi zone, analyze situation?
                    this@HomeActivity.wifiAnalyzer.analyzeWifiSituation()
                    super.onLost(network)
                }
            }
            cm.registerNetworkCallback(networkRequest, networkCallback)
        }
        requestExtReadPermission()

        Intent(this, CameraFileMonitorService::class.java).also { intent ->
            startService(intent)
        }
    }

    fun updateLocation(str: String) {
        runOnUiThread {
            // runOnUiThread since this can be called by different context
            helloworld.text = str
        }
    }

    override fun onDestroy() {
        cm.unregisterNetworkCallback(networkCallback)
        super.onDestroy()
    }

    private fun askLocationPermission() {
        AlertDialog.Builder(this)
            .setTitle("Location is needed for Sidekick!")
            .setMessage("Sidekick needs to know where you are to figure out where wifi should be turned on/off. Pick the first option!")
            .setPositiveButton("Ok",
                DialogInterface.OnClickListener { dialog, which ->
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        PERMISSION_REQUEST_ACCESS_FINE_LOCATION
                    )
                })

            .setIcon(android.R.drawable.ic_dialog_alert)
            .show()
    }

    private fun requestExtReadPermission() {
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            AlertDialog.Builder(this)
                .setTitle("File read is needed for Sidekick!")
                .setMessage("Sidekick needs read-only access to ")
                .setPositiveButton("Ok",
                    DialogInterface.OnClickListener { dialog, which ->
                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                            PERMISSION_REQUEST_ACCESS_EXTERNAL_STORAGE
                        )
                    })

                .setIcon(android.R.drawable.ic_dialog_alert)
                .show()

        }
    }
}
