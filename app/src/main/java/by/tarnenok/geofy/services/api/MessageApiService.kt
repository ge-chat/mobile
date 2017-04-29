package by.tarnenok.geofy.services.api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Created by tarne on 22.05.16.
 */
interface MessageApiService {
    @POST("api/message/create")
    fun create(@Body model: SendMessageModel) : Call<Void>
}