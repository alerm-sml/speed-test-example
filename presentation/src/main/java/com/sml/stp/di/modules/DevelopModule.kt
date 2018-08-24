package com.sml.stp.di.modules

import com.sml.domain.development.Logger
import com.sml.stp.development.LoggerTimber
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
abstract class DevelopModule {

    @Singleton
    @Binds
    abstract fun loggerProvider(logger: LoggerTimber): Logger
}