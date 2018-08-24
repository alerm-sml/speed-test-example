package com.sml.data.datasource.okhttp

import com.sml.data.datasource.base.SpeedTestListener
import com.sml.data.factory.OkHttp3Factory
import okhttp3.OkHttpClient
import java.io.File
import java.io.IOException
import javax.inject.Inject

class OkHttpApiSpeedTestUpload @Inject constructor(
        private val okHttpFactory: OkHttp3Factory
) {

    private lateinit var client: OkHttpClient

    fun executeUploadSpeedTest(url: String, speedTestListener: SpeedTestListener, file: File) {
        client = okHttpFactory.buildUploadOkHttpClient()

        val requestBody = okHttpFactory.buildUploadRequestUploadBody(
                listener = speedTestListener,
                reportInterval = OkHttpWayConst.REPORT_INTERVAL,
                file = file
        )

        val request = okHttpFactory.buildUploadRequest(
                url = url,
                requestBody = requestBody
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