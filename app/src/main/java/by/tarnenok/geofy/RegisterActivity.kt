package by.tarnenok.geofy

import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.Button
import android.widget.EditText
import by.tarnenok.geofy.models.UserRegisterModel
import by.tarnenok.geofy.services.api.RegisterApiService
import org.jetbrains.anko.find

class RegisterActivity : AppCompatActivity() {

    //use dependency injection
    val registerApiService = RegisterApiService()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val registerButton = find<Button>(R.id.button_register);
        registerButton.setOnClickListener{ v ->
            val email = find<EditText>(R.id.edit_email).text.toString();
            val username = find<EditText>(R.id.edit_username).text.toString();
            val password = find<EditText>(R.id.edit_password).text.toString();
            val confirmPassword = find<EditText>(R.id.edit_confirmpassword).text.toString();
            registerApiService.registerUser(UserRegisterModel(
                    email,
                    username,
                    password,
                    confirmPassword
            ))
        }
    }
}
