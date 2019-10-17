package com.henryli.sidekick

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.location.Location
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.henryli.sidekick.data.WifiPoiData
import com.henryli.sidekick.geowifi.GeofenceTransitionReceiver

class GeofenceUtils(appContext: Context) {
    private var context: Context = appContext

    private var geofenceClient: GeofencingClient = LocationServices.getGeofencingClient(context)

    private val geofencePendingIntent: PendingIntent by lazy {
        val intent = Intent(context, GeofenceTransitionReceiver::class.java)
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
        // addGeofences() and removeGeofences()
        PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }


    private fun buildFence(location: Location, fenceID: String, type: Int): Geofence {
        return Geofence.Builder()
            .setRequestId(fenceID)
            .setCircularRegion(
                location.latitude,
                location.longitude,
                WifiPoiData.GEOFENCE_RADIUS
            )
            .setExpirationDuration(Geofence.NEVER_EXPIRE) // Maybe have a way to delete geofences at some point
            .setTransitionTypes(type)
            .build()
    }

    private fun buildGeofenceRequest(
        locations: ArrayList<Location>,
        names: ArrayList<String>,
        type: Int
    ): GeofencingRequest {
        val list = ArrayList<Geofence>()
        for (i in locations.indices) {
            val fence = buildFence(locations.get(i), names.get(i), type)
            list.add(fence)
        }
        return GeofencingRequest.Builder().apply {
            setInitialTrigger(
                GeofencingRequest.INITIAL_TRIGGER_EXIT or
                        GeofencingRequest.INITIAL_TRIGGER_ENTER or
                        GeofencingRequest.INITIAL_TRIGGER_DWELL
            )
            addGeofences(list)
        }.build()
    }

    fun createGeofences(locations: ArrayList<Location>, names: ArrayList<String>, type: Int) {
        geofenceClient?.addGeofences(
            buildGeofenceRequest(locations, names, type),
            geofencePendingIntent
        )?.run {
            //            addOnSuccessListener {
//
//            }
//            addOnFailureListener(
//
//            )
        }
    }
}