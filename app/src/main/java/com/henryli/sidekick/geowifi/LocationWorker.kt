package com.henryli.sidekick.geowifi

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

class LocationWorker(appContext: Context, workerParams: WorkerParameters) : CoroutineWorker(appContext, workerParams) {

    private val home: Location = Location(String()) // Provider string unnecessary
    private val work: Location = Location(String())

    val HOME_ID = "HOME_GEOFENCE"
    val HOME_LATITUDE = 37.396502
    val HOME_LONGITUDE = -122.074725
    val WORK_ID = "WORK_GEOFENCE"
    val WORK_LATITUDE = 37.417666
    val WORK_LONGITUDE = -122.086093
    val GEOFENCE_RADIUS = 300F
    val LOITERING_DELAY = 15000

    private var context: Context = appContext

    private var geofencingClient: GeofencingClient = LocationServices.getGeofencingClient(context)

    private val geofencePendingIntent: PendingIntent by lazy {
        val intent = Intent(context, GeofenceDwellReceiver::class.java)
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
        // addGeofences() and removeGeofences()
        PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    init {
        home.latitude = HOME_LATITUDE
        home.longitude = HOME_LONGITUDE
        work.latitude = WORK_LATITUDE
        work.longitude = WORK_LONGITUDE
    }

    private fun buildPermanentGeofence(location: Location, fenceID : String): Geofence {
        val geofence = Geofence.Builder()
            .setRequestId(fenceID)
            .setCircularRegion(location.latitude, location.longitude, GEOFENCE_RADIUS)
            .setExpirationDuration(Geofence.NEVER_EXPIRE) // Maybe have a way to delete geofences at some point
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_DWELL)
            .setLoiteringDelay(LOITERING_DELAY) // set to 15s
            .setNotificationResponsiveness(30000) // increase battery
            .build()
        return geofence
    }

    override suspend fun doWork(): Result = coroutineScope {

        lateinit var homeFence: Geofence
        lateinit var workFence: Geofence
        val jobs = (0  until 1).map {
            async {
                homeFence = buildPermanentGeofence(home, HOME_ID)
                workFence = buildPermanentGeofence(work, WORK_ID)
                if ((homeFence != null || workFence != null) && ContextCompat.checkSelfPermission(
                        context, Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    val geofenceRequestBuilder = GeofencingRequest.Builder()
                    geofenceRequestBuilder.addGeofence(homeFence)
                    geofenceRequestBuilder.addGeofence(workFence)
                    geofencingClient.addGeofences(geofenceRequestBuilder.build(),
                        geofencePendingIntent)?.run() {
                        //                addOnSuccessListener {
                        //                }
                        //                addOnFailureListener(Exception -> Unit)(
                        //
                        //                )
                    }

                }
            }
        }
        jobs.awaitAll()
        Result.success()
    }

}