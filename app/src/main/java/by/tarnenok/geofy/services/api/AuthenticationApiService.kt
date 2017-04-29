package by.tarnenok.geofy.services.api

import retrofit2.Call
import retrofit2.http.*

interface  AuthenticationApiService {
    @POST("api/auth/register")
    fun registerUser(@Body model: UserRegisterModel?) : Call<Void>

    @FormUrlEncoded
    @POST("token")
    fun login(@Field("username") username:String, @Field("password")password: String,
              @Field("grant_type")grant_type: String = "password") : Call<TokenModel>
}

data class ErrorViewModel(val error: String, val error_description: String){
    constructor() : this("", "")
}
