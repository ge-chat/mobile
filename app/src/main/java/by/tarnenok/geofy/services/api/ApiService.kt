package by.tarnenok.geofy.services.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiService {
    private val client = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val ongoing = chain.request().newBuilder()
            if (token != null && token != "") {
                ongoing.addHeader("Authorization", "Bearer $token");
            }
            chain.proceed(ongoing.build());
        }
        .build()

    private val builder: Retrofit?
        get() = Retrofit.Builder()
            .baseUrl(host)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

    private var token: String? = null
    private var host: String? = null

    fun initialize(host: String, token: String?){
        this.token = token
        this.host = host
    }

    val auth: AuthenticationApiService
        get() = builder!!.create(AuthenticationApiService::class.java)

    val chart: ChartApiService
        get() = builder!!.create(ChartApiService::class.java)

    val message: MessageApiService
        get() = builder!!.create(MessageApiService::class.java)
}