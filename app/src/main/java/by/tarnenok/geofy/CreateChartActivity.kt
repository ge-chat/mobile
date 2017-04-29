package by.tarnenok.geofy

import android.location.Location
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.Circle
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.rey.material.widget.Slider
import com.rey.material.widget.TextView
import org.jetbrains.anko.enabled
import org.jetbrains.anko.find
import org.jetbrains.anko.onClick

class CreateChartActivity : AppCompatActivity() {
    var mMap: GoogleMap? = null
    var mApiClient: GoogleApiClient? = null
    var mLocation: Location? = null
    var mCircle: Circle? = null
    var mCircleMarker: Circle? = null
    var defaultRadiusInMetres = 10.0

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

        val sliderText = find<android.support.v7.widget.AppCompatTextView>(R.id.textview_radius)
        sliderText.text = "${resources.getString(R.string.radius)} (${defaultRadiusInMetres.toInt()}m)"
        val slider = find<Slider>(R.id.slider_radius)
        slider.setValue(defaultRadiusInMetres.toFloat(), true)
        slider.setOnPositionChangeListener { view, fromUser, oldPos, newPos, oldValue, newValue ->
            if(mCircle == null) return@setOnPositionChangeListener
            defaultRadiusInMetres = newValue.toDouble()
            mCircle?.radius = defaultRadiusInMetres
            sliderText.text = "${resources.getString(R.string.radius)} ($defaultRadiusInMetres)"
        }

        val progress = find<com.rey.material.widget.ProgressView>(R.id.progress_create_chart)
        val createChartButton = find<com.rey.material.widget.Button>(R.id.button_create_chart)
        createChartButton.onClick {
            progress.start()
            createChartButton.isEnabled = false
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
        val latLang = LatLng(location.latitude, location.longitude)

        if(mMap == null) return
        if(mCircle == null){
            mCircle = mMap?.addCircle(CircleOptions()
                .fillColor(ContextCompat.getColor(this, R.color.colorHintTransparent))
                .strokeColor(ContextCompat.getColor(this, R.color.colorPrimaryDark))
                .strokeWidth(5f)
                .radius(defaultRadiusInMetres)
                .center(latLang))
        }
        if(mCircleMarker == null){
            mCircleMarker = mMap?.addCircle(CircleOptions()
                .fillColor(ContextCompat.getColor(this, R.color.colorPrimaryDark))
                .strokeWidth(0f)
                .radius(2.0)
                .center(latLang))
        }
        mCircle?.center = latLang
        mCircleMarker?.center = latLang
        mMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(latLang, 18.0f));
    }
}
