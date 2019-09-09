package com.henryli.sidekick.geowifi

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import com.henryli.sidekick.AppConstants
import com.henryli.sidekick.R

class GeofenceDwellReceiver : BroadcastReceiver() {

    val TAG = "Sidekick Geofence Broadcast"

    override fun onReceive(context: Context?, intent: Intent?) {
        val geofencingEvent = GeofencingEvent.fromIntent(intent)

        if (geofencingEvent.hasError()) {
//            val errorMessage = GeofenceErrorMessages.getErrorString(this,
//                geofencingEvent.errorCode)
//            Log.e(TAG, errorMessage)
            return
        }

        // Get the transition type.
        val geofenceTransition: Int = geofencingEvent.geofenceTransition

        // Test that the reported transition was a dwell transition.
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_DWELL) {
            // Get the geofences that were triggered. A single event can trigger
            // multiple geofences.
            val triggeringGeofences: List<Geofence> = geofencingEvent.triggeringGeofences

            val geofenceTransitionDetails =
                getTransitionDetails(geofenceTransition, triggeringGeofences)
            // Send notification and log the transition details.
            createNotification(context!!, "Geofence title", geofenceTransitionDetails)

            Log.e(TAG, geofenceTransitionDetails)
        } else {
            // Log the error.
            Log.e(
                TAG,
                getString(context!!, R.string.geofence_transition_invalid_type, geofenceTransition)
            )
        }
    }

    fun createNotification(context: Context, title: String, description: String) {
        var notification = NotificationCompat.Builder(context, AppConstants.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle(title)
            .setContentText(description)
//            .setStyle(NotificationCompat.BigPictureStyle()
//                .bigPicture(myBitmap))
            .build()
        with(NotificationManagerCompat.from(context)) {
            // notificationId is a unique int for each notification that you must define
            notify(AppConstants.NOTIFICATION_ID, notification)
        }
    }

    private fun getString(context: Context, resid: Int, transition: Int): String {
        val sb = StringBuffer(context.getString(resid))
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
            Geofence.GEOFENCE_TRANSITION_DWELL -> sb.append("Dwelling")
        }
        for (fence: Geofence in fences) {
            sb.append('-')
            sb.append(fence.requestId)
            sb.append(' ')
        }
        return sb.toString()

    }

}
