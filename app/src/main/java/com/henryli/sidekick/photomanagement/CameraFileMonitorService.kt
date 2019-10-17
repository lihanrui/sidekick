package com.henryli.sidekick.photomanagement

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.widget.Toast
import android.os.FileObserver
import android.os.Binder
import android.util.Log

class CameraFileMonitorService : Service() {
    val TAG = "CameraFileMonitorService"
    private lateinit var observer: FileObserver
    private val mBinder: IBinder = PhotoBinder()

    override fun onBind(intent: Intent?): IBinder? {
        return mBinder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand")

        val cameraSavePath =
            android.os.Environment.getExternalStorageDirectory().toString() + "/DCIM/Camera/"
        Toast.makeText(
            this,
            "Photo monitor service started, trying to watch $cameraSavePath",
            Toast.LENGTH_LONG
        ).show()

        observer = object :
            FileObserver(cameraSavePath) { // set up a file observer to watch this directory on sd card
            override fun onEvent(event: Int, file: String?) {
                if (event == FileObserver.CREATE && !file.equals(".probe")) { // check if it's a "create" and not equal to .probe because that's created every time camera is launched
                    Log.d(TAG, "File created [$cameraSavePath$file]")
                }
            }
        }
        observer.startWatching()
        return super.onStartCommand(intent, flags, startId)
    }

    inner class PhotoBinder : Binder() {
        fun getService(): CameraFileMonitorService {
            return this@CameraFileMonitorService
        }
    }
}