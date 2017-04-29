package by.tarnenok.geofy

import android.os.Bundle
import android.os.PersistableBundle
import android.support.design.widget.NavigationView
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem

/**
 * Created by tarne on 16.05.16.
 */
class ChartActivity : AppCompatActivity(), BaseActivity {
    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        setContentView(R.layout.activity_chart)


    }
}