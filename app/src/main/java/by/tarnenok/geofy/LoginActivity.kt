package by.tarnenok.geofy

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import org.jetbrains.anko.find

class LoginActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        var loginButton = find<Button>(R.id.button_login);
        loginButton.setOnClickListener({v ->
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        });
    }
}
