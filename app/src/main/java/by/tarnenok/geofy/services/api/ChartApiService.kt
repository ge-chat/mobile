package by.tarnenok.geofy.services.api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * Created by tarne on 01.05.16.
 */
interface ChartApiService {
    @POST("api/chart/create")
    fun createChart(@Body model: CreateChartModel?) : Call<Void>

    @GET("api/chart/inlocation")
    fun getInLocation(@Query("longitude")longitude: Double, @Query("latitude")latitude: Double) : Call<Array<ChartReadModelShort>>
}