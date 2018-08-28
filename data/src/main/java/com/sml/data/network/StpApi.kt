package com.sml.data.network

import com.sml.data.model.network.SpeedTestHostNwModel
import io.reactivex.Single
import retrofit2.http.GET

interface StpApi {
    @GET("/speedtest/api/server")
    fun getSpeedTestServer(): Single<SpeedTestHostNwModel>
}