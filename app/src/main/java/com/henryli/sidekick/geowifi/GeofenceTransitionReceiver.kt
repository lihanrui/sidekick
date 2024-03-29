package com.henryli.sidekick.geowifi

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingEvent
import com.henryli.sidekick.NotificationUtils

class GeofenceTransitionReceiver : BroadcastReceiver() {

    val TAG = "Sidekick Geofence Broadcast"
    companion object {
        val GEOFENCE_NOTIFICATION_ID = 31
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.e(TAG, "onReceive");
        val geofencingEvent = GeofencingEvent.fromIntent(intent)

        if (geofencingEvent.hasError()) {
            NotificationUtils(context!!).notify(
                "Geofence error",
                getErrorString(geofencingEvent.errorCode),
                GEOFENCE_NOTIFICATION_ID
            )
            return
        }

        // Get the transition type.
        val transitionType: Int = geofencingEvent.geofenceTransition

        // Test that the reported transition was a dwell transition.
        // Get the geofences that were triggered. A single event can trigger
        // multiple geofences.
        val triggeringGeofences: List<Geofence> = geofencingEvent.triggeringGeofences

        // Delegate to the NetworkGeoController to figure out what is going on
        val networkGeoController = NetworkGeoController(context!!, NotificationUtils(context!!))
        networkGeoController.analyzeWifiSituation()

//        when (transitionType) {
////            Geofence.GEOFENCE_TRANSITION_DWELL -> sb.append("Dwelling") // Unclear
//            Geofence.GEOFENCE_TRANSITION_ENTER -> {
//                if()
//            }// if wifi on, clear notification. if wifi off, notify.
//            Geofence.GEOFENCE_TRANSITION_EXIT ->{
//
//            } // if wifi on, notify. if wifi off, clear notification.
//        }


//        val geofenceTransitionDetails =
//            getTransitionDetails(transitionType, triggeringGeofences)
//        // Send notification and log the transition details.
//        NotificationUtils(context!!).notify(
//            getString(transitionType),
//            geofenceTransitionDetails,
//            GEOFENCE_NOTIFICATION_ID
//        )
//
//        Log.e(TAG, geofenceTransitionDetails)
    }

    private fun getString(transition: Int): String {
        val sb = StringBuffer()
        when (transition) {
            Geofence.GEOFENCE_TRANSITION_DWELL -> sb.append("Dwelling")
            Geofence.GEOFENCE_TRANSITION_ENTER -> sb.append("Entered")
            Geofence.GEOFENCE_TRANSITION_EXIT -> sb.append("Exited")
        }
        return sb.toString()
    }

    private fun getTransitionDetails(transition: Int, fences: List<Geofence>): String {
        val sb = StringBuffer()
        when (transition) {
            Geofence.GEOFENCE_TRANSITION_EXIT -> sb.append("Turn off wifi, exited: ")
            Geofence.GEOFENCE_TRANSITION_ENTER -> sb.append("Turn on wifi, entered: ")
        }
        for (fence: Geofence in fences) {
            sb.append(fence.requestId)
        }
        return sb.toString()

    }

    fun getErrorString(code: Int): String {
        when (code) {
            GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE -> {
                return "Geofence service is not available now. Typically because user turned off location access."
            }
            GeofenceStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES -> {
                return "Your app has registered over 100 geofences! Too many."
            }
            GeofenceStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS -> {
                return "App has provided more than 5 PendingIntents to addGeofences call. See GeofenceStatusCodes error codes for more info."
            }
            else -> {
                return "Unknown code"
            }
        }
    }
}
