package com.henryli.sidekick.geowifi


/*
 * 1. Create broadcast receiver to listen for network changes
 * 2. Detect initial state:
 * If no wifi enabled, create geofences and listen for when going to work / home
 * If wifi enabled but not connected to a network, prompt user to turn off wifi
 * 3. When wifi is turned off, broadcast receiver creates geofences and listen for when going to work / home
 * 4. When wifi is turned on, do nothing (user is in control)
 */
class NetworkGeoController {


}