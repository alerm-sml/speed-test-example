package com.sml.data.network

import com.sml.data.model.network.SpeedtestHostNwModel
import com.sml.data.model.response.SpeedTestServersResponse
import io.reactivex.Single
import javax.inject.Inject

class MockStpApi @Inject constructor() {

    fun getSpeedTestServers(): Single<SpeedTestServersResponse> =
            Single.just(getSpeedTestServersResponse())

    private fun getSpeedTestServersResponse(): SpeedTestServersResponse =
            SpeedTestServersResponse(getNwHosts(), "ok")

    private fun getNwHosts(): List<SpeedtestHostNwModel> =
            listOf(
                    SpeedtestHostNwModel(
                            hostName = "speedtest.tele2.net",
                            downloadUrl = "http://speedtest.tele2.net/100MB.zip",
                            uploadUrl = "http://speedtest.tele2.net/upload"
                    )
            )
}