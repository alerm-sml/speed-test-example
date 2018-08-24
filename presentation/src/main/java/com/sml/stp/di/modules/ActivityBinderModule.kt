package com.sml.stp.di.modules

import com.sml.stp.di.scope.ActivityScope
import com.sml.stp.feature.main.di.MainActivityModule
import com.sml.stp.feature.main.view.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
interface ActivityBinderModule {

    @ActivityScope
    @ContributesAndroidInjector(modules = [MainActivityModule::class])
    fun mainActivityInjector(): MainActivity
}