package by.tarnenok.geofy.services.api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface  AuthenticationApiService {
    @POST("api/auth/register")
    fun registerUser(@Body model: UserRegisterModel?) : Call<Void>

    @POST("api/auth/login")
    fun login(@Body model: UserLoginModel?) : Call<TokenModel>
}
