package by.tarnenok.geofy

import android.app.ProgressDialog
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.Button
import android.widget.EditText
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

class RegisterActivity : AppCompatActivity() {

    //use dependency injection
    val apiService = ApiService("http://192.168.55.2:5000/")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val registerButton = find<Button>(R.id.button_register)
        registerButton.setOnClickListener{ v ->
            val email = find<EditText>(R.id.edit_email).text.toString()
            val username = find<EditText>(R.id.edit_username).text.toString()
            val password = find<EditText>(R.id.edit_password).text.toString()
            val confirmPassword = find<EditText>(R.id.edit_confirmpassword).text.toString()
            val progressDialog = ProgressDialog.show(this, "", resources.getString(R.string.loading_text))
            apiService.auth.registerUser(UserRegisterModel(
                    email,
                    username,
                    password,
                    confirmPassword
            )).enqueue( object : Callback<Void> {
                override fun onFailure(call: Call<Void>?, t: Throwable?) {
                    progressDialog.cancel()
                }

                override fun onResponse(call: Call<Void>?, response: Response<Void>?) {
                    progressDialog.cancel()
                    if(response!!.isSuccessful){

                    }else{
                        val errors = Gson().fromJson(response.errorBody().string(), Array<String?>::class.java)
                        alert(resources.getString(R.string.errors_title),
                                errors.toUnorderedListFromResource(resources, packageName)){
                            positiveButton(resources.getString(R.string.ok)){}
                        }.show()
                    }
                }
            })
        }
    }
}
