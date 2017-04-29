package by.tarnenok.geofy

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText
import by.tarnenok.geofy.services.TokenService
import by.tarnenok.geofy.services.api.ApiService
import by.tarnenok.geofy.services.api.ErrorViewModel
import by.tarnenok.geofy.services.api.TokenModel
import com.google.gson.Gson
import org.jetbrains.anko.alert
import org.jetbrains.anko.find
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        ApiService.initialize(Config.apiHost, TokenService(this).get()?.access_token)


        val loginButton = find<Button>(R.id.button_login);
        loginButton.setOnClickListener{v ->
            val email = find<EditText>(R.id.edit_email).text.toString();
            val password = find<EditText>(R.id.edit_password).text.toString();
            val progressDialog = ProgressDialog.show(this, "", resources.getString(R.string.loading_text))
            ApiService.auth.login(email,password)
                    .enqueue(object : Callback<TokenModel> {
                override fun onResponse(call: Call<TokenModel>?, response: Response<TokenModel>?) {
                    progressDialog.cancel()
                    if(response!!.isSuccessful){
                        TokenService(applicationContext).set(response.body())
                        val mainIntent = Intent(applicationContext, MainActivity::class.java)
                        startActivity(mainIntent)
                    }else{
                        val error = Gson().fromJson(response.errorBody().string(), ErrorViewModel::class.java)
                        alert(resources.getStringByName(error.error, packageName) ?: error.error_description,
                                resources.getString(R.string.errors_title)){
                            positiveButton(resources.getString(R.string.ok)){}
                        }.show()
                    }
                }

                override fun onFailure(call: Call<TokenModel>?, t: Throwable?) {
                    progressDialog.cancel()
                    alert(resources.getString(R.string.bad_connection)){
                        positiveButton { resources.getString(R.string.ok) }
                    }.show()
                }

            })
        };

        val registerButton = find<Button>(R.id.button_register);
        registerButton.setOnClickListener{ v ->
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent);
        }
    }
}
