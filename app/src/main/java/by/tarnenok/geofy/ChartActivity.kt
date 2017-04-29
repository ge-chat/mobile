package by.tarnenok.geofy

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.PersistableBundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.widget.EditText
import by.tarnenok.geofy.services.SignalRService
import by.tarnenok.geofy.services.TokenService
import by.tarnenok.geofy.services.api.*
import com.google.gson.Gson
import microsoft.aspnet.signalr.client.hubs.HubConnection
import org.jetbrains.anko.alert
import org.jetbrains.anko.find
import org.jetbrains.anko.onClick
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by tarne on 16.05.16.
 */
class ChartActivity : AppCompatActivity(){
    object CONSTANTS {
        val KEY_CHART  = "chart"
    }

    var chartModel: ChartReadModel? = null
    var signalrConnection: HubConnection? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chart)
        ApiService.initialize(Config.apiHost, TokenService(this).get()?.access_token)

        val chartModelStr = intent.getStringExtra(ChartActivity.CONSTANTS.KEY_CHART)
        chartModel = Gson().fromJson(chartModelStr, ChartReadModel::class.java)

        val toolbar = find<Toolbar>(R.id.toolbar)
        toolbar.title = chartModel?.title
        setSupportActionBar(toolbar)

        val drawer = find<DrawerLayout>(R.id.drawer_layout)
        val toggle = ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.addDrawerListener(toggle)
        toggle.syncState()

        val navigationView = find<NavigationView>(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener{
            val drawer = find<DrawerLayout>(R.id.drawer_layout)
            drawer.closeDrawer(GravityCompat.START)
            true
        };

        val sendMessageButton = find<com.rey.material.widget.ImageButton>(R.id.button_send)
        sendMessageButton.onClick {
            val message = find<EditText>(R.id.edit_message).text.toString()
            ApiService.message.create(SendMessageModel(message, chartModel!!.id)).enqueue(
                object : Callback<Void> {
                    override fun onFailure(call: Call<Void>?, t: Throwable?) {
                        toast(R.string.bad_connection)
                    }

                    override fun onResponse(call: Call<Void>?, response: Response<Void>?) {
                        if(!response!!.isSuccessful){}
                    }
                }
            )
        }

        val messagesRV = find<RecyclerView>(R.id.recycleview_messages)
        messagesRV.setHasFixedSize(true)
        val linearManager = LinearLayoutManager(this)
        linearManager.stackFromEnd = true
        messagesRV.layoutManager = linearManager
        messagesRV.adapter = MessageRVAdapter(chartModel!!, TokenService(this).get()!!.userInfo().id)

        signalrConnection = SignalRService.createConnection(Config.apiHost, TokenService(this))
        val chartHub = signalrConnection!!.createHubProxy(SignalRService.Hubs.Chart.Name)
        val handler = Handler()
        chartHub.on(SignalRService.Hubs.Chart.MessagePosted, { data -> handler.post {
            chartModel!!.messages.add(data)
            messagesRV.adapter.notifyDataSetChanged()
            val message = find<EditText>(R.id.edit_message)
            message.text.clear()
            messagesRV.smoothScrollToPosition(messagesRV.adapter.itemCount - 1)
        }}, MessageReadModel::class.java)
        chartHub.on(SignalRService.Hubs.Chart.ParticipantAdded, { data ->
            if(data.chartId == chartModel!!.id){
                chartModel!!.participants.add(data.participant)
            }
        }, ParticipantAddedSignal::class.java)
    }

    override fun onStart() {
        signalrConnection?.start()
        super.onStart()
    }

    override fun onStop() {
        signalrConnection?.stop()
        super.onStop()
    }
}