package com.henryli.sidekick.geowifi

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.ConnectivityManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.LocationServices
import com.henryli.sidekick.GeofenceUtils
import com.henryli.sidekick.NotificationUtils
import com.henryli.sidekick.data.WifiPoiData
import android.content.Context.LOCATION_SERVICE
import androidx.core.content.ContextCompat.getSystemService
import androidx.annotation.NonNull
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.henryli.sidekick.MainActivity
import android.content.Context.LOCATION_SERVICE
import androidx.core.content.ContextCompat.getSystemService




/*
 if no wifi
    check if home, at work etc.
        if yes, ask for wifi on
        if no, geofence and listen for going home,
    if wifi
    check if home, at work etc.
        if yes, geofence and listen for leaving home, then ask for wifi back on
        if no, ask for wifi off
 */
class NetworkGeoController(appContext: Activity, notificationUtilsHandle: NotificationUtils) {
    private val context: Activity = appContext
    private val notificationUtils = notificationUtilsHandle
    private val poiManager: WifiPoiManager = WifiPoiManager()
    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)
    private val geofenceUtils = GeofenceUtils(context)

    val PERMISSION_REQUEST_ACCESS_FINE_LOCATION = 1359
    /*
    if no wifi
    check if home, at work etc.
        if yes, ask for wifi on
        if no, geofence and listen for going home, then ask for wifi back on
    if wifi
    check if home, at work etc.
        if yes, geofence and listen for leaving home, then ask for wifi back off
        if no, ask for wifi off
     */
    fun analyzeWifiSituation() {
        val here: Location? = getCurrentLocation()
        if (here == null) {
            notificationUtils.notify("Unable to find location ", "Please turn on map")
        } else {
            if (!isWifiOn()) {
                if (poiManager.atWifiPoi(here)) {
                    // Ask for wifi on
                    notificationUtils.notify("Turn on wifi", "In wifi zone")
                } else {
                    // create enter geofence
//                notificationUtils.notify("Wifi off", "Poi on")
                    createGeofences(Geofence.GEOFENCE_TRANSITION_ENTER)
                }
            } else {
                if (poiManager.atWifiPoi(here)) {
                    // create exit geofence
//                notificationUtils.notify("Wifi on", "Poi on")
                    createGeofences(Geofence.GEOFENCE_TRANSITION_EXIT)
                } else {
                    // ask for wifi off
                    notificationUtils.notify("Turn off wifi", "Left wifi zone")
                }
            }
        }
        if (context is MainActivity) {
            val mActivity = context as MainActivity
            mActivity.updateLocation(poiManager.currLoc)
        }
    }

    private fun getCurrentLocation(): Location? {
        var loc = Location("")
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                context,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSION_REQUEST_ACCESS_FINE_LOCATION
                // TODO: implement below code on result
            )
        } else {
            val mLocationManager =
                context.getSystemService(LOCATION_SERVICE) as LocationManager
            val providers = mLocationManager.getProviders(true)
            var bestLocation: Location? = null
            for (provider in providers) {
                val l = mLocationManager.getLastKnownLocation(provider) ?: continue
                if (bestLocation == null || l.getAccuracy() < bestLocation.accuracy) {
                    // Found best last known location: %s", l);
                    bestLocation = l
                }
            }
            if(bestLocation != null){
                loc = bestLocation
            }
        }
        return loc
    }

    private fun isWifiOn(): Boolean {
        val connManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        val mWifi = connManager!!.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
        return mWifi.isConnected
    }

    private fun createGeofences(type: Int) {
        val pois = poiManager.getWifiHotspots()
        geofenceUtils.createGeofences(pois, poiManager.names, type)
    }

    internal class WifiPoiManager {
        private val home: Location = Location(String()) // Provider string unnecessary
        private val work: Location = Location(String())
        val names = ArrayList<String>()

        var currLoc = ""

        init {
            home.latitude = WifiPoiData.HOME_LATITUDE
            home.longitude = WifiPoiData.HOME_LONGITUDE
            work.latitude = WifiPoiData.WORK_LATITUDE
            work.longitude = WifiPoiData.WORK_LONGITUDE
            names.add(WifiPoiData.HOME_ID)
            names.add(WifiPoiData.WORK_ID)
        }

        /**
         * Function for getting list of locations where wifis exist
         */
        fun getWifiHotspots(): ArrayList<Location> {
            val locs = ArrayList<Location>()
            locs.add(home)
            locs.add(work)
            return locs
        }

        fun atWifiPoi(here: Location): Boolean {
            val hotspots = getWifiHotspots()
            for (i in hotspots.indices) {
                if (hotspots.get(i).distanceTo(here) < 500) {
                    currLoc = names.get(i)
                    return true
                }
            }
            currLoc = "Not in a poi" + here.latitude + here.longitude
            return false
        }

    }
}