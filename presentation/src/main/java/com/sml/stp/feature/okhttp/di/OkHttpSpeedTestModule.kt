package com.sml.stp.feature.okhttp.di

import com.sml.data.repository.*
import com.sml.domain.repository.*
import com.sml.stp.di.scope.FragmentScope
import dagger.Binds
import dagger.Module

@Module
abstract class OkHttpSpeedTestModule {

    @Binds
    @FragmentScope
    abstract fun provideServerLatencyRepository(repository: ServerLatencyRepositoryImpl): ServerLatencyRepository

    @Binds
    @FragmentScope
    abstract fun provideSpeedTestDownloadRepository(repository: OkHttpSpeedTestDownloadRepositoryImpl): SpeedTestDownloadRepository

    @Binds
    @FragmentScope
    abstract fun provideSpeedTestUploadRepository(repository: OkHttpSpeedTestUploadRepositoryImpl): SpeedTestUploadRepository

    @Binds
    @FragmentScope
    abstract fun provideSpeedTestHostRepository(repository: SpeedTestHostRepositoryImpl): SpeedTestHostRepository

    @Binds
    @FragmentScope
    abstract fun provideSpeedTestInterpolatorRepository(repository: SpeedTestInterpolatorRepositoryImpl): SpeedTestInterpolatorRepository
}