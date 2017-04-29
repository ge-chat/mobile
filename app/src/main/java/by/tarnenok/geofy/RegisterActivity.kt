package by.tarnenok.geofy

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.text.Html
import android.view.View
import android.widget.Button
import android.widget.EditText
import by.tarnenok.geofy.services.TokenService
import by.tarnenok.geofy.services.api.ApiService
import by.tarnenok.geofy.services.api.UserRegisterModel
import by.tarnenok.geofy.services.api.AuthenticationApiService
import com.google.gson.Gson
import org.jetbrains.anko.alert
import org.jetbrains.anko.async
import org.jetbrains.anko.find
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        ApiService.initialize(Config.apiHost, TokenService(this).get()?.access_token)

        val registerButton = find<Button>(R.id.button_register)
        registerButton.setOnClickListener{ v ->
            val email = find<EditText>(R.id.edit_email).text.toString()
            val username = find<EditText>(R.id.edit_username).text.toString()
            val password = find<EditText>(R.id.edit_password).text.toString()
            val confirmPassword = find<EditText>(R.id.edit_confirmpassword).text.toString()
            val progressDialog = ProgressDialog.show(this, "", resources.getString(R.string.loading_text))
            ApiService.auth.registerUser(UserRegisterModel(
                    email,
                    username,
                    password,
                    confirmPassword
            )).enqueue( object : Callback<Void> {
                override fun onFailure(call: Call<Void>?, t: Throwable?) {
                    progressDialog.cancel()
                    alert(resources.getString(R.string.bad_connection)){
                        positiveButton { resources.getString(R.string.ok) }
                    }.show()
                }

                override fun onResponse(call: Call<Void>?, response: Response<Void>?) {
                    progressDialog.cancel()
                    if(response!!.isSuccessful){
                        val loginIntent = Intent(applicationContext, LoginActivity::class.java)
                        startActivity(loginIntent)
                    }else{
                        val errors = Gson().fromJson(response.errorBody().string(), Array<String?>::class.java)
                        alert(errors.toUnorderedListFromResource(resources, packageName)!!,
                                resources.getString(R.string.errors_title)){
                            positiveButton(resources.getString(R.string.ok)){}
                        }.show()
                    }
                }
            })
        }
    }
}
