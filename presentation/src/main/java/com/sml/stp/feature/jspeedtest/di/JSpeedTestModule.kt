package com.sml.stp.feature.jspeedtest.di

import com.sml.data.repository.JSpeedTestDownloadRepositoryImpl
import com.sml.data.repository.JSpeedTestUploadRepositoryImpl
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
abstract class JSpeedTestModule {

    @Binds
    @FragmentScope
    abstract fun provideSpeedTestDownloadRepository(repository: JSpeedTestDownloadRepositoryImpl): SpeedTestDownloadRepository

    @Binds
    @FragmentScope
    abstract fun provideSpeedTestUploadRepository(repository: JSpeedTestUploadRepositoryImpl): SpeedTestUploadRepository

    @Binds
    @FragmentScope
    abstract fun provideSpeedTestHostRepository(repository: SpeedTestHostRepositoryImpl): SpeedTestHostRepository

    @Binds
    @FragmentScope
    abstract fun provideSpeedTestInterpolatorRepository(repository: SpeedTestInterpolatorRepositoryImpl): SpeedTestInterpolatorRepository
}