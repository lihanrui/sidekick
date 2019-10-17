package com.henryli.sidekick.geowifi

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.henryli.sidekick.data.WifiPoiData

internal class PoiManager {
    private val home: Location =
        Location(String()) // Provider string unnecessary
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

    fun getCurrentLocation(context: Context): Location? {
        var loc = Location("")

        val mLocationManager =
            context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val providers = mLocationManager.getProviders(true)
        var bestLocation: Location? = null
        for (provider in providers) {
            try {
                val l = mLocationManager.getLastKnownLocation(provider) ?: continue
//                Log.e("$l.accuracy", "$bestLocation?.accuracy")
                if (bestLocation == null || l.getAccuracy() < bestLocation.accuracy
                    || provider.equals("network") // it's possible that gps doesn't have an up to date fix
                ) {
                    // Found best last known location: %s", l);
                    bestLocation = l
                }
            } catch (e: SecurityException) {
            }
        }
        if (bestLocation != null) {
            loc = bestLocation
        }
        return loc
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