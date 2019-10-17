package com.henryli.sidekick.geowifi

import android.content.Context
import android.content.Context.WIFI_SERVICE
import android.location.Location
import android.net.wifi.WifiManager
import com.google.android.gms.location.Geofence
import com.henryli.sidekick.GeofenceUtils
import com.henryli.sidekick.HomeActivity
import com.henryli.sidekick.NotificationUtils


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
class NetworkGeoController(appContext: Context, notificationUtilsHandle: NotificationUtils) {
    private val context: Context = appContext
    private val notificationUtils = notificationUtilsHandle
    private val poiManager: PoiManager =
        PoiManager()
    private val geofenceUtils = GeofenceUtils(context)

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
        val here: Location? = poiManager.getCurrentLocation(context)
        if (here == null) {
            notificationUtils.notify(
                "Unable to find location ",
                "Please turn on map",
                GeofenceTransitionReceiver.GEOFENCE_NOTIFICATION_ID
            )
        } else {
            if (!isWifiOn()) {
                if (poiManager.atWifiPoi(here)) {
                    // Ask for wifi on
                    notificationUtils.notify(
                        "Turn on wifi",
                        "In wifi zone",
                        GeofenceTransitionReceiver.GEOFENCE_NOTIFICATION_ID
                    )
                } else {
                    // create enter geofence
//                    notificationUtils.notify(
//                        "Good, wifi is off",
//                        "Not in wifi zone",
//                        GeofenceTransitionReceiver.GEOFENCE_NOTIFICATION_ID
//                    )
                    notificationUtils.deleteAllNotifications()
                    createGeofences(Geofence.GEOFENCE_TRANSITION_ENTER)
                }
            } else {
                if (poiManager.atWifiPoi(here)) {
                    // create exit geofence
//                    notificationUtils.notify(
//                        "Good, wifi is on",
//                        "In wifi zone",
//                        GeofenceTransitionReceiver.GEOFENCE_NOTIFICATION_ID
//                    )
                    notificationUtils.deleteAllNotifications()
                    createGeofences(Geofence.GEOFENCE_TRANSITION_EXIT)
                } else {
                    // ask for wifi off
                    notificationUtils.notify(
                        "Turn off wifi",
                        "Left wifi zone",
                        GeofenceTransitionReceiver.GEOFENCE_NOTIFICATION_ID
                    )
                }
            }
        }
        if (context is HomeActivity) {
            val mActivity = context as HomeActivity
            mActivity.updateLocation(poiManager.currLoc)
        }
    }

    private fun isWifiOn(): Boolean {
        val wifi = context.getSystemService(WIFI_SERVICE) as WifiManager
        return wifi.isWifiEnabled

//        val connManager =
//            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
//        val mWifi = connManager!!.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
//        return mWifi.isConnected
    }

    private fun createGeofences(type: Int) {
        val pois = poiManager.getWifiHotspots()
        geofenceUtils.createGeofences(pois, poiManager.names, type)
    }

}