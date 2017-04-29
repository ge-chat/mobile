package by.tarnenok.geofy

import by.tarnenok.geofy.services.api.ApiService
import com.google.android.gms.location.LocationRequest

/**
 * Created by tarne on 28.04.16.
 */
interface BaseActivity {
    //use dependency injection
    val apiHost:String
        get() = "http://192.168.55.2:5000/"

    val mLocationRequest: LocationRequest
        get(){
            val request = LocationRequest()
            request.interval = 2000
            request.fastestInterval = 5000
            request.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            return request
        }
}