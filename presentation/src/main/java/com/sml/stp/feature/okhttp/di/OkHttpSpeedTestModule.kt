package com.sml.stp.feature.okhttp.di

import com.sml.data.repository.OkHttpSpeedTestDownloadRepositoryImpl
import com.sml.data.repository.OkHttpSpeedTestUploadRepositoryImpl
import com.sml.data.repository.SpeedTestHostRepositoryImpl
import com.sml.data.repository.SpeedTestInterpolatorRepositoryImpl
import com.sml.domain.repository.SpeedTestDownloadRepository
import com.sml.domain.repository.SpeedTestHostRepository
import com.sml.domain.repository.SpeedTestInterpolatorRepository
import com.sml.domain.repository.SpeedTestUploadRepository
import com.sml.stp.di.scope.FragmentScope
import dagger.Binds
import dagger.Module

@Module
abstract class OkHttpSpeedTestModule {

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