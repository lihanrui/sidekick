package com.henryli.sidekick

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.location.Location
import android.util.Log
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.LocationServices
import com.henryli.sidekick.data.WifiPoiData
import com.henryli.sidekick.data.WifiPoiData.HOME_ID
import com.henryli.sidekick.geowifi.GeofenceDwellReceiver

class GeofenceUtils(appContext : Context) {
    private var context: Context = appContext

    private var geofenceClient: GeofencingClient = LocationServices.getGeofencingClient(context)

    private val geofencePendingIntent: PendingIntent by lazy {
        val intent = Intent(context, GeofenceDwellReceiver::class.java)
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
        // addGeofences() and removeGeofences()
        PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private fun buildDwellFence(location: Location, fenceID: String): Geofence {
        Log.e("CREATING GEOFENCE", fenceID);
        val geofence = Geofence.Builder()
            .setRequestId(fenceID)
            .setCircularRegion(location.latitude, location.longitude, WifiPoiData.GEOFENCE_RADIUS)
            .setExpirationDuration(Geofence.NEVER_EXPIRE) // Maybe have a way to delete geofences at some point
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_DWELL)
            .setLoiteringDelay(WifiPoiData.LOITERING_DELAY) // set to 15s
//            .setNotificationResponsiveness(5000) // increase battery
            .build()
        return geofence
    }

    private fun buildExitFence(location: Location, fenceID: String): Geofence {
        Log.e("CREATING GEOFENCE", fenceID);
        val geofence = Geofence.Builder()
            .setRequestId(fenceID)
            .setCircularRegion(location.latitude, location.longitude, WifiPoiData.GEOFENCE_RADIUS)
            .setExpirationDuration(Geofence.NEVER_EXPIRE) // Maybe have a way to delete geofences at some point
            .setTransitionTypes(
                Geofence.GEOFENCE_TRANSITION_ENTER or
                        Geofence.GEOFENCE_TRANSITION_EXIT or
                        Geofence.GEOFENCE_TRANSITION_DWELL)
//            .setNotificationResponsiveness(5000) // increase battery
            .build()
        return geofence
    }

    fun createGeofences(pois : List<Location>, type: Int){

    }

    fun createGeofence(home : Location){
        val homeFence = buildDwellFence(home, HOME_ID)
//                workFence = buildDwellFence(work, WORK_ID)
//                homeLeaveFence = buildExitFence(home, HOME_ID)
//                if ((homeFence != null || workFence != null) && ContextCompat.checkSelfPermission(
//                        context, Manifest.permission.ACCESS_FINE_LOCATION
//                    ) == PackageManager.PERMISSION_GRANTED
//                ) {
//                    val geofenceRequestBuilder = GeofencingRequest.Builder()
//                    geofenceRequestBuilder.addGeofence(homeFence)
//                    geofenceRequestBuilder.addGeofence(workFence)
//                    geofenceRequestBuilder.addGeofence(homeLeaveFence)
//                    geofenceClient.addGeofences(
//                        geofenceRequestBuilder.build(),
//                        geofencePendingIntent
//                    )?.run() {
//                        addOnSuccessListener {
//                            Log.e("Geofence success", "Created geofence");
//                        }
////                        addOnFailureListener(Exception -> Unit)(
////                        )
//                    }
//
//                }
    }
}