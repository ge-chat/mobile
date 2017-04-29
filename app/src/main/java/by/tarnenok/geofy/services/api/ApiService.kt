package by.tarnenok.geofy.services.api

import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Created by tarne on 24.04.16.
 */
class ApiService(val baseUrl: String) {
    var builder = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val auth: AuthenticationApiService
        get() = builder.create(AuthenticationApiService::class.java)

    val error: Converter<ResponseBody, ApiError>
        get() = builder.responseBodyConverter(ApiError::class.java, arrayOf())
}

data class ApiError(val statusCode: Int?, val message: String?)