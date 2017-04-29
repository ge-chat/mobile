package by.tarnenok.geofy

import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.support.design.widget.TextInputLayout
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.widget.EditText
import by.tarnenok.geofy.services.SignalRService
import by.tarnenok.geofy.services.TokenService
import by.tarnenok.geofy.services.api.ApiService
import by.tarnenok.geofy.services.api.ChartReadModel
import by.tarnenok.geofy.services.api.CreateChartModel
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.Circle
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.rey.material.widget.Slider
import microsoft.aspnet.signalr.client.hubs.HubConnection
import microsoft.aspnet.signalr.client.hubs.SubscriptionHandler
import microsoft.aspnet.signalr.client.hubs.SubscriptionHandler1
import org.jetbrains.anko.alert
import org.jetbrains.anko.find
import org.jetbrains.anko.onClick
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CreateChartActivity : AppCompatActivity() , BaseActivity {
    var mMap: GoogleMap? = null
    var mApiClient: GoogleApiClient? = null
    var mLocation: Location? = null
    var mCircle: Circle? = null
    var mCircleMarker: Circle? = null
    var defaultRadiusInMetres = 10.0
    var signalrConnection: HubConnection? = null

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
        setContentView(R.layout.activity_create_chart)
        ApiService.initialize(apiHost, TokenService(this).get()?.access_token)

        val toolbar = find<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha)
        toolbar.setNavigationOnClickListener { v ->
            onBackPressed()
        }

        mApiClient = GoogleApiClient.Builder(this)
             .addConnectionCallbacks(object : GoogleApiClient.ConnectionCallbacks{
                 override fun onConnectionSuspended(p0: Int) { }

                 override fun onConnected(bundle: Bundle?) {
                     LocationServices.FusedLocationApi.requestLocationUpdates(
                             mApiClient, mLocationRequest) { location ->
                        setCameraMap(location)
                     };
                     mLocation = LocationServices.FusedLocationApi.getLastLocation(
                             mApiClient)
                 }
             })
            .addOnConnectionFailedListener { }
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
            val title = find<EditText>(R.id.edit_title).text.toString();
            if(title.isNullOrEmpty()) {
                val textInputTitle = find<TextInputLayout>(R.id.textinput_title);
                textInputTitle.error = getString(R.string.titleRequired)
                textInputTitle.isErrorEnabled = true;
                return@onClick
            }
            progress.start()
            createChartButton.isEnabled = false
            ApiService.chart.createChart(CreateChartModel(
                title,
                mLocation!!.latitude,
                mLocation!!.longitude,
                defaultRadiusInMetres,
                null
            )).enqueue(object : Callback<Void> {
                override fun onFailure(call: Call<Void>?, t: Throwable?) {
                    progress.stop()
                    alert(resources.getString(R.string.bad_connection)){
                        positiveButton { resources.getString(R.string.ok) }
                    }.show()
                }

                override fun onResponse(call: Call<Void>?, response: Response<Void>?) {
                    progress.stop()
                    if(!response!!.isSuccessful){
                        val errors = Gson().fromJson(response.errorBody().string(), Array<String?>::class.java)
                        alert(errors.toUnorderedListFromResource(resources, packageName)!!,
                                resources.getString(R.string.errors_title)){
                            positiveButton(resources.getString(R.string.ok)){}
                        }.show()
                    }
                }
            })
        }

        signalrConnection = SignalRService.createConnection(apiHost, TokenService(this))

        val chartHub = signalrConnection!!.createHubProxy(SignalRService.Hubs.Chart.Name)
        chartHub.on(SignalRService.Hubs.Chart.ChartCreated, { data ->
            //TODO implement actions
        }, ChartReadModel::class.java)
    }

    override fun onStart() {
        mApiClient?.connect()
        signalrConnection?.start()
        super.onStart()
    }

    override fun onStop() {
        mApiClient?.disconnect()
        signalrConnection?.stop()
        super.onStop()
    }

    private fun setCameraMap(location: Location){
        mLocation = location
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
