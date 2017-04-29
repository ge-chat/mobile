package by.tarnenok.geofy

import android.os.Bundle
import android.os.PersistableBundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import by.tarnenok.geofy.services.TokenService
import by.tarnenok.geofy.services.api.ApiService
import by.tarnenok.geofy.services.api.ChartReadModel
import com.google.gson.Gson
import org.jetbrains.anko.find

/**
 * Created by tarne on 16.05.16.
 */
class ChartActivity : AppCompatActivity(){
    object CONSTANTS {
        val KEY_CHART  = "chart"
    }

    var chartModel: ChartReadModel? = null

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
    }
}