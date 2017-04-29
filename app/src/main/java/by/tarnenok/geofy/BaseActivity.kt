package by.tarnenok.geofy

import by.tarnenok.geofy.services.api.ApiService

/**
 * Created by tarne on 28.04.16.
 */
interface BaseActivity {
    //use dependency injection
    val apiHost:String
        get() = "http://192.168.55.2:5000/"
}
//192.168.55.2