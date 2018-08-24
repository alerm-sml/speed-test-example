package com.sml.stp.di.modules

import android.app.Application
import android.content.Context
import com.sml.stp.app.StpApp
import com.sml.stp.system.AppSchedulers
import com.sml.stp.system.SchedulersProvider
import dagger.Binds
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
abstract class AppModule {

    @Module
    companion object {
        @Singleton
        @Provides
        @JvmStatic
        fun provideContext(app: StpApp): Context = app
    }

    @Binds
    abstract fun application(app: StpApp): Application

    @Binds
    abstract fun schedulerProvider(appSchedulers: AppSchedulers): SchedulersProvider
}