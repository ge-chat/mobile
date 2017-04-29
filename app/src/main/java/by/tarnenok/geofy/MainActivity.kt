package by.tarnenok.geofy

import android.app.Activity
import android.content.Intent
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import by.tarnenok.geofy.android.DividerItemDecoration
import by.tarnenok.geofy.services.SignalRService
import by.tarnenok.geofy.services.TokenService
import by.tarnenok.geofy.services.api.ApiService
import by.tarnenok.geofy.services.api.ChartReadModelShort
import by.tarnenok.geofy.services.api.MessageReadModel
import by.tarnenok.geofy.services.api.ShortMessage
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.GooglePlayServicesUtil
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsStates
import com.google.android.gms.location.LocationSettingsStatusCodes
import com.rey.material.widget.ProgressView
import microsoft.aspnet.signalr.client.hubs.HubConnection
import microsoft.aspnet.signalr.client.hubs.HubProxy
import org.jetbrains.anko.find
import org.jetbrains.anko.onClick
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener{

    var mApiClient: GoogleApiClient? = null
    var mProgress: ProgressView? = null
    var mCallback: Call<MutableList<ChartReadModelShort>>? = null
    var mRecycleView: RecyclerView? = null
    var mLocation: Location? = null

    var currentChats:MutableList<ChartReadModelShort> = ArrayList<ChartReadModelShort>()

    var signalrConnection: HubConnection? = null
    var chartHub: HubProxy? = null

    val geoSettings: LocationSettingsRequest
        get(){
            val builder = LocationSettingsRequest.Builder()
                .addLocationRequest(Config.locationRequest)
            builder.setNeedBle(true)
            return builder.build()
        }

    val REQUEST_CHECK_SETTINGS = 0x1;
    val PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        checkInstalledGoogleServices()

        ApiService.initialize(Config.apiHost, TokenService(this).get()?.access_token)

        val toolbar = find<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val drawer = find<DrawerLayout>(R.id.drawer_layout)
        val toggle = ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.addDrawerListener(toggle)
        toggle.syncState()

        val navigationView = find<NavigationView>(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

        val floatButton = find<FloatingActionButton>(R.id.button_add_group)
        floatButton.onClick{v ->
            val addGroupIntent = Intent(this, CreateChartActivity::class.java)
            startActivity(addGroupIntent)
        }

        mRecycleView = find<RecyclerView>(R.id.recycleview_charts);
        mRecycleView?.setHasFixedSize(true)
        val linearManger = LinearLayoutManager(this)
        mRecycleView?.layoutManager = linearManger
        mRecycleView?.addItemDecoration(DividerItemDecoration(this))

        mApiClient = GoogleApiClient.Builder(this)
                .addConnectionCallbacks(object : GoogleApiClient.ConnectionCallbacks{
                    override fun onConnectionSuspended(p0: Int) { }

                    override fun onConnected(bundle: Bundle?) {
                        val geoSettingsResult = LocationServices.SettingsApi.checkLocationSettings(mApiClient, geoSettings)
                        geoSettingsResult.setResultCallback { result ->
                            val status = result.status
                            when (status.statusCode){
                                LocationSettingsStatusCodes.SUCCESS -> startLocationUpdating()
                                LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                                    try{
                                        status.startResolutionForResult(this@MainActivity, REQUEST_CHECK_SETTINGS)
                                    }catch(ex: Exception){}
                                }
                                LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                                    //TODO alert and close application
                                }
                            }
                        }
                    }
                })
                .addOnConnectionFailedListener { }
                .addApi(LocationServices.API)
                .build()

        mProgress = find<ProgressView>(R.id.progress_update_charts)

        signalrConnection = SignalRService.createConnection(
                Config.apiHost, TokenService(this))
        chartHub = signalrConnection!!.createHubProxy(SignalRService.Hubs.Chart.Name)
        val handler = Handler()
        chartHub?.on(SignalRService.Hubs.Chart.MessagePosted, {data -> handler.post {
            val chart = currentChats.firstOrNull { it.id == data.chartId } ?: return@post
            chart.lastMessage = ShortMessage(data.id, data.userId, data.created, data.message)
            mRecycleView!!.adapter.notifyDataSetChanged()
        }}, MessageReadModel::class.java)
    }

    override fun onStart() {
        if(TokenService(applicationContext).get() == null){
            val loginIntent = Intent(this, LoginActivity::class.java)
            startActivity(loginIntent)
        }
        mApiClient?.connect()
        signalrConnection?.start()
        super.onStart()
    }

    override fun onStop() {
        val chatsForRemove = currentChats.map { it.id }
        if(chatsForRemove.any()) chartHub?.invoke(
                SignalRService.Hubs.Chart.RemoveConnection, signalrConnection?.connectionId, chatsForRemove)

        mApiClient?.disconnect()
        if(signalrConnection?.connectionId != null) signalrConnection?.stop()
        super.onStop()
    }

    override fun onBackPressed() {
        val drawer = find<DrawerLayout>(R.id.drawer_layout)
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val id = item.itemId
        if (id == R.id.action_settings) {
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.nav_send -> {
                TokenService(applicationContext).reset()
                val loginIntent = Intent(this, LoginActivity::class.java)
                startActivity(loginIntent)
            }
        }

        val drawer = find<DrawerLayout>(R.id.drawer_layout)
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when(requestCode){
            REQUEST_CHECK_SETTINGS -> when(resultCode){
                Activity.RESULT_OK -> startLocationUpdating()
                Activity.RESULT_CANCELED -> toast(resources.getString(R.string.enable_location))
            }
            PLAY_SERVICES_RESOLUTION_REQUEST -> when(resultCode){
                Activity.RESULT_OK ->{

                }
            }
        }
    }

    fun updateCharts(location: Location){
        if(mCallback != null && !mCallback!!.isExecuted) return
        if(mLocation != null) if (mLocation!!.distanceTo(location) < 1) return

        mLocation = location
        mProgress?.start()
        mCallback = ApiService.chart.getInLocation(location.longitude, location.latitude)
        mCallback?.enqueue(object: Callback<MutableList<ChartReadModelShort>>{
            override fun onFailure(call: Call<MutableList<ChartReadModelShort>>, t: Throwable?) {
                mProgress?.stop()
                mLocation = null
                toast(R.string.bad_connection)
            }

            override fun onResponse(call: Call<MutableList<ChartReadModelShort>>, response: Response<MutableList<ChartReadModelShort>>) {
                mProgress?.stop()
                if(response.isSuccessful){
                    updateConnections(response.body())
                    currentChats = response.body()
                    //TODO fill resycleview
                    mRecycleView?.adapter = ChartRVAdapter(currentChats)
                }else{
                    toast(R.string.bad_connection)
                }
            }
        })
    }

    fun updateConnections(chats: MutableList<ChartReadModelShort>){
        val newChats = chats.map { it.id }.toHashSet()
        val currentChats = currentChats.map { it.id }.toHashSet()
        val chatsForAdd = newChats.subtract(currentChats)
        val chatsForRemove = currentChats.subtract(newChats)

        if(chatsForAdd.any()) chartHub?.invoke(
                SignalRService.Hubs.Chart.AddConnection, signalrConnection?.connectionId, chatsForAdd)
        if(chatsForRemove.any()) chartHub?.invoke(
                SignalRService.Hubs.Chart.RemoveConnection, signalrConnection?.connectionId, chatsForRemove)
    }

    fun startLocationUpdating(){
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mApiClient, Config.locationRequest) { location ->
            updateCharts(location)
        };

        try{
            val location = LocationServices.FusedLocationApi.getLastLocation(
                    mApiClient)
            updateCharts(location)
        }catch(ex: Exception){}
    }

    fun checkInstalledGoogleServices(): Boolean{
        val apiAvailability = GoogleApiAvailability.getInstance()
        val code = apiAvailability.isGooglePlayServicesAvailable(this)
        if(code != ConnectionResult.SUCCESS){
            if(apiAvailability.isUserResolvableError(code)){
                apiAvailability.showErrorDialogFragment(this, code, PLAY_SERVICES_RESOLUTION_REQUEST)
            }
            return false
        }
        return true
    }
}
