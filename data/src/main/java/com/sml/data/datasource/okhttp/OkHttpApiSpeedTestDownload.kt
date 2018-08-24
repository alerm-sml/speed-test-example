package com.sml.data.datasource.okhttp

import com.sml.data.datasource.base.SpeedTestListener
import com.sml.data.factory.OkHttp3Factory
import okhttp3.OkHttpClient
import java.io.IOException
import javax.inject.Inject

class OkHttpApiSpeedTestDownload @Inject constructor(
        private val okHttpFactory: OkHttp3Factory
) {

    private lateinit var client: OkHttpClient

    fun executeDownloadSpeedTest(url: String, speedTestListener: SpeedTestListener) {
        val request = okHttpFactory.buildDownloadRequest(url)

        client = okHttpFactory.buildDownloadOkHttpClient(
                listener = speedTestListener,
                reportInterval = OkHttpWayConst.REPORT_INTERVAL
        )

        val call = client.newCall(request)
        call.execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")
            val string = response.body()?.string()
        }
    }

    fun stopTask() {
        client.dispatcher().cancelAll()
    }
}