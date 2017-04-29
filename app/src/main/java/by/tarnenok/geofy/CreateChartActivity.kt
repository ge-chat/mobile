package by.tarnenok.geofy

import android.location.Location
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import org.jetbrains.anko.find

class CreateChartActivity : AppCompatActivity() {
    var mMap: GoogleMap? = null
    var mApiClient: GoogleApiClient? = null
    var mLocation: Location? = null
    var mMarker: Marker? = null

    val mLocationRequest: LocationRequest
        get(){
            val request = LocationRequest()
            request.interval = 2000
            request.fastestInterval = 5000
            request.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            return request
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_group)

        val toolbar = find<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha)
        toolbar.setNavigationOnClickListener { v ->
            onBackPressed()
        }

        mApiClient = GoogleApiClient.Builder(this)
             .addConnectionCallbacks(object : GoogleApiClient.ConnectionCallbacks{
                 override fun onConnectionSuspended(p0: Int) {
                     //throw UnsupportedOperationException()
                 }

                 override fun onConnected(bundle: Bundle?) {
                     LocationServices.FusedLocationApi.requestLocationUpdates(
                             mApiClient, mLocationRequest) { location ->
                        setCameraMap(location)
                     };
                     mLocation = LocationServices.FusedLocationApi.getLastLocation(
                             mApiClient)
                 }
             })
            .addOnConnectionFailedListener {

            }
            .addApi(LocationServices.API)
            .build()

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync{map ->
            mMap = map
            if(mLocation != null){
                setCameraMap(mLocation!!)
            }
        }
    }

    override fun onStart() {
        mApiClient!!.connect()
        super.onStart()
    }

    override fun onStop() {
        mApiClient!!.disconnect()
        super.onStop()
    }

    private fun setCameraMap(location: Location){
        if(mMap == null) return
        if(mMarker != null) mMarker?.remove()
        val latLang = LatLng(location.latitude, location.longitude)
        mMarker = mMap?.addMarker(MarkerOptions().position(latLang))
        mMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(latLang, 18.0f));
    }
}
