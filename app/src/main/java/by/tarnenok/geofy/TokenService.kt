package by.tarnenok.geofy

import android.content.Context
import android.content.SharedPreferences
import by.tarnenok.geofy.services.api.TokenModel
import com.google.gson.Gson

/**
 * Created by tarne on 28.04.16.
 */
class TokenService {
    constructor(context: Context){
        preferences = context.getSharedPreferences(preferencesName, Context.MODE_PRIVATE)
    }

    private val preferencesName = "geofy_settings"
    private val tokenKey = "token"
    private val preferences : SharedPreferences

    fun get(): TokenModel?{
        val tokenModel = preferences.getString(tokenKey, "")
        try{
            return  Gson().fromJson(tokenModel, TokenModel::class.java)
        }catch(ex: Exception){
            return null
        }
    }

    fun set(token: TokenModel?){
        var serializedToken = Gson().toJson(token)
        val editor = preferences.edit()
        editor.putString(tokenKey, serializedToken)
        editor.commit()
    }

    fun reset(){
        val editor = preferences.edit()
        editor.putString(tokenKey, "")
        editor.commit()
    }
}