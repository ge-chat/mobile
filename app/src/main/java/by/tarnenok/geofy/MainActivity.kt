package by.tarnenok.geofy

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.NavigationView
import android.support.design.widget.Snackbar
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarActivity
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import by.tarnenok.geofy.services.TokenService
import by.tarnenok.geofy.services.api.ApiService
import org.jetbrains.anko.find
import org.jetbrains.anko.onClick

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, BaseActivity {

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
    }

    override fun onStart() {
        if(TokenService(applicationContext).get() == null){
            val loginIntent = Intent(this, LoginActivity::class.java)
            startActivity(loginIntent)
        }
        super.onStart()
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
}
