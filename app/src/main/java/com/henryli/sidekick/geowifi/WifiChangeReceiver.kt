package com.henryli.sidekick.geowifi

//import android.content.BroadcastReceiver
//import android.content.Context
//import android.content.Intent
//import android.net.ConnectivityManager
//import android.util.Log
//import com.henryli.sidekick.NotificationUtils
//
//class WifiChangeReceiver : BroadcastReceiver() {
//    override fun onReceive(context: Context, intent: Intent) {
//        val connManager = context
//            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
//
//        val wifi = connManager
//            .getNetworkInfo(ConnectivityManager.TYPE_WIFI)
//
//        val mobile = connManager
//            .getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
//
//        if (wifi!!.isAvailable || mobile!!.isAvailable) {
//            // Do something
//
//            Log.d("Network Available ", "Flag No 1")
//        }
//        NotificationUtils(context!!).notify("GOT WIFI", "HMM", 321)
//    }
//}