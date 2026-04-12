package com.softcode.mymagicapp.core.hardware.domain

data class LatLng(val latitude: Double, val longitude: Double)

interface LocationProvider {
    /** Returns the last known location, or null if unavailable / permission denied. */
    suspend fun getCurrentLocation(): LatLng?
}
