package com.sml.data.factory

import com.sml.data.datasource.base.SpeedTestListener
import com.sml.data.datasource.okhttp.OkHttpWayConst
import com.sml.data.datasource.okhttp.SpeedTestDownloadResponseBody
import com.sml.data.datasource.okhttp.SpeedTestUploadRequestBody
import com.sml.data.tools.TimeBenchmark
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class OkHttp3Factory @Inject constructor(
        private val timeBenchmark: TimeBenchmark
) {

    companion object {
        const val CONTENT_TYPE = "image/*"
    }

    fun buildDownloadRequest(url: String): Request =
            Request.Builder()
                    .url(url)
                    .build()

    fun buildDownloadOkHttpClient(listener: SpeedTestListener, reportInterval: Long): OkHttpClient =
            OkHttpClient.Builder()
                    .connectionPool(ConnectionPool(0, 1, TimeUnit.MICROSECONDS))
                    .readTimeout(OkHttpWayConst.SOCKET_TIMEOUT, TimeUnit.SECONDS)
                    .writeTimeout(OkHttpWayConst.SOCKET_TIMEOUT, TimeUnit.SECONDS)
                    .connectTimeout(OkHttpWayConst.SOCKET_TIMEOUT, TimeUnit.SECONDS)
                    .addNetworkInterceptor { chain ->
                        val originalResponse = chain.proceed(chain.request())
                        val originalBody = originalResponse.body()
                        originalBody?.let {
                            originalResponse.newBuilder()
                                    .body(SpeedTestDownloadResponseBody(
                                            responseBody = it,
                                            speedTestListener = listener,
                                            startTimeMillis = timeBenchmark.build(),
                                            timeBenchmark = timeBenchmark,
                                            reportInterval = reportInterval
                                    ))
                                    .build()
                        }
                    }
                    .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.HEADERS))
                    .build()


    fun buildUploadOkHttpClient(): OkHttpClient =
            OkHttpClient.Builder()
                    .connectionPool(ConnectionPool(0, 1, TimeUnit.MICROSECONDS))
                    .readTimeout(OkHttpWayConst.SOCKET_TIMEOUT, TimeUnit.SECONDS)
                    .writeTimeout(OkHttpWayConst.SOCKET_TIMEOUT, TimeUnit.SECONDS)
                    .connectTimeout(OkHttpWayConst.SOCKET_TIMEOUT, TimeUnit.SECONDS)
                    .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.HEADERS))
                    .build()

    fun buildUploadRequest(url: String, requestBody: RequestBody): Request =
            Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .build()

    fun buildUploadRequestUploadBody(listener: SpeedTestListener, file: File, reportInterval: Long): RequestBody =
            MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addPart(
                            Headers.of("Content-Disposition", "form-data; name=\"image\"; filename=\"${file.name}\""),
                            SpeedTestUploadRequestBody(
                                    file = file,
                                    speedTestListener = listener,
                                    contentType = CONTENT_TYPE,
                                    timeBenchmark = timeBenchmark,
                                    reportInterval = reportInterval
                            )
                    )
                    .build()
}