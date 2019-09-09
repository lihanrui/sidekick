//package com.henryli.sidekick
//
//import androidx.appcompat.app.AppCompatActivity
//import android.os.Bundle
//import com.spotify.android.appremote.api.ConnectionParams;
//import com.spotify.android.appremote.api.Connector;
//import com.spotify.android.appremote.api.SpotifyAppRemote;
//
//import com.spotify.protocol.client.Subscription;
//import com.spotify.protocol.types.PlayerState;
//import com.spotify.protocol.types.Track;
//
//
//class SpotifyActivity : AppCompatActivity() {
//
//    private val CLIENT_ID = "2ce1249653a643c49efd98adb0816396"
//    private val REDIRECT_URI = "http://com.yourdomain.yourapp/callback"
//    private val mSpotifyAppRemote: SpotifyAppRemote? = null
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
//    }
//
//    override fun onStart() {
//        super.onStart()
//// Set the connection parameters
//        val connectionParams = ConnectionParams.Builder(CLIENT_ID)
//            .setRedirectUri(REDIRECT_URI)
//            .showAuthView(true)
//            .build()
//    }
//
//    private fun connected() {
//        // Then we will write some more code here.
//    }
//
//    override fun onStop() {
//        super.onStop()
//        // Aaand we will finish off here.
//    }
//}