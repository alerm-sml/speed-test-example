package com.sml.data.network

import com.sml.data.model.network.SpeedTestHostNwModel
import io.reactivex.Single
import javax.inject.Inject

class MockStpApi @Inject constructor() : StpApi {

    override fun getSpeedTestServer(): Single<SpeedTestHostNwModel> =
            Single.just(getNwHost())

    private fun getNwHost(): SpeedTestHostNwModel =
            SpeedTestHostNwModel(
                    hostName = "speedtest.tele2.net",
                    downloadUrl = "http://speedtest.tele2.net/100MB.zip",
                    uploadUrl = "http://speedtest.tele2.net/upload"
            )
}