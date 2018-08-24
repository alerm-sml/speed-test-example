package com.sml.stp.di.modules

import com.sml.data.network.StpApi
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
class NetworkModule {

    companion object {
        const val BASE_URL_PROD = "http://speedtest.tele2.net/"
        const val BASE_URL_DEV = "http://speedtest.tele2.net/"
    }

    @Provides
    @Singleton
    fun provideClient(): OkHttpClient =
            OkHttpClient.Builder()
                    .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.HEADERS))
                    .build()

    @Provides
    @Singleton
    fun provideBackendApi(client: OkHttpClient): StpApi =
            Retrofit.Builder()
                    .baseUrl(BASE_URL_PROD)
                    .client(client)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(StpApi::class.java)
}