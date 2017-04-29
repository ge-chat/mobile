package by.tarnenok.geofy

import com.google.android.gms.location.LocationRequest

/**
 * Created by tarne on 16.05.16.
 */
object Config {
    val apiHost = "http://192.168.55.2:5000/"
    //val apiHost = "http://localhost:5000/"
    //val apiHost = "http://sharkeva.tech/"

    val locationRequest: LocationRequest
        get(){
            val request = LocationRequest()
            request.interval = 2000
            request.fastestInterval = 5000
            request.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            return request
        }
}