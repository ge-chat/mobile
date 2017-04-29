package by.tarnenok.geofy.services.api

import retrofit2.Call
import retrofit2.http.POST

/**
 * Created by tarne on 01.05.16.
 */
interface ChartApiService {
    @POST("api/chart/create")
    fun createChart(model: ChartCreateModel) : Call<Void>
}