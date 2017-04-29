package by.tarnenok.geofy

import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.NavigationView
import android.support.design.widget.Snackbar
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarActivity
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import by.tarnenok.geofy.services.TokenService
import by.tarnenok.geofy.services.api.ApiService
import by.tarnenok.geofy.services.api.ChartReadModelShort
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationServices
import com.google.gson.Gson
import org.jetbrains.anko.alert
import org.jetbrains.anko.find
import org.jetbrains.anko.onClick
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, BaseActivity {

    var mApiClient: GoogleApiClient? = null
    var mProgress: com.rey.material.widget.ProgressView? = null
    var mCallback: Call<Array<ChartReadModelShort>>? = null
    var mRecycleView: RecyclerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ApiService.initialize(apiHost, TokenService(this).get()?.access_token)

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

        mApiClient = GoogleApiClient.Builder(this)
                .addConnectionCallbacks(object : GoogleApiClient.ConnectionCallbacks{
                    override fun onConnectionSuspended(p0: Int) { }

                    override fun onConnected(bundle: Bundle?) {
                        LocationServices.FusedLocationApi.requestLocationUpdates(
                                mApiClient, mLocationRequest) { location ->
                            updateCharts(location)
                        };
                        val location = LocationServices.FusedLocationApi.getLastLocation(
                                mApiClient)
                        updateCharts(location)
                    }
                })
                .addOnConnectionFailedListener { }
                .addApi(LocationServices.API)
                .build()

        mProgress = find<com.rey.material.widget.ProgressView>(R.id.progress_update_charts)
    }

    override fun onStart() {
        if(TokenService(applicationContext).get() == null){
            val loginIntent = Intent(this, LoginActivity::class.java)
            startActivity(loginIntent)
        }
        mApiClient?.connect()
        super.onStart()
    }

    override fun onStop() {
        mApiClient?.disconnect()
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

    fun updateCharts(location: Location){
        if(mCallback != null && !mCallback!!.isExecuted) return

        mProgress?.start()
        mCallback = ApiService.chart.getInLocation(location.longitude, location.latitude)
        mCallback?.enqueue(object: Callback<Array<ChartReadModelShort>>{
            override fun onFailure(call: Call<Array<ChartReadModelShort>>?, t: Throwable?) {
                mProgress?.stop()
                toast(R.string.bad_connection)
            }

            override fun onResponse(call: Call<Array<ChartReadModelShort>>?, response: Response<Array<ChartReadModelShort>>?) {
                mProgress?.stop()
                if(response!!.isSuccessful){
                    //TODO fill resycleview
                    mRecycleView?.adapter = ChartRVAdapter(response.body())
                }else{
                    toast(R.string.bad_connection)
                }
            }
        })
    }
}
