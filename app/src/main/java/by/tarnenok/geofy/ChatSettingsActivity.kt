package by.tarnenok.geofy

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import by.tarnenok.geofy.services.api.UpdateChatModel
import by.tarnenok.geofy.views.ChatSettingsUI
import com.google.gson.Gson
import org.jetbrains.anko.find
import org.jetbrains.anko.setContentView

/**
 * Created by tarne on 12.06.16.
 */
class ChatSettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val chat = Gson().fromJson(intent.getStringExtra("info"), UpdateChatModel::class.java)

        ChatSettingsUI(chat, {updateChat()}).setContentView(this)

        val toolbar = find<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    fun updateChat(){

    }
}