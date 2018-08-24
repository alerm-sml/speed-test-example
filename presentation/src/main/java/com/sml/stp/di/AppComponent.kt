package com.sml.stp.di

import com.sml.stp.app.StpApp
import com.sml.stp.di.modules.*
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(
        modules = [
            AndroidInjectionModule::class,
            AndroidSupportInjectionModule::class,
            ActivityBinderModule::class,
            AppModule::class,
            DevelopModule::class,
            DbModule::class,
            NavigationModule::class,
            NetworkModule::class
        ]
)
interface AppComponent : AndroidInjector<StpApp> {

    @Component.Builder
    abstract class Builder : AndroidInjector.Builder<StpApp>()
}