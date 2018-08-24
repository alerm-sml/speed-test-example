package com.sml.data.network

import com.sml.data.model.response.SpeedTestServersResponse
import io.reactivex.Single
import retrofit2.http.GET

interface StpApi {
    @GET("{lang}/speedtest/api/servers")
    fun getSpeedTestServers(): Single<SpeedTestServersResponse>
}