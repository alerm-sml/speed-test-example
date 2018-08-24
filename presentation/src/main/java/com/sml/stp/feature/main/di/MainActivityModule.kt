package com.sml.stp.feature.main.di

import com.sml.stp.common.ext.weak
import com.sml.stp.delegate.ErrorMessageDelegate
import com.sml.stp.delegate.MessageDelegate
import com.sml.stp.delegate.UiDelegateManager
import com.sml.stp.di.scope.ActivityScope
import com.sml.stp.di.scope.FragmentScope
import com.sml.stp.feature.choosespeedtest.di.ChooseSpeedTestModule
import com.sml.stp.feature.choosespeedtest.view.ChooseSpeedTestFragment
import com.sml.stp.feature.jspeedtest.di.JSpeedTestModule
import com.sml.stp.feature.jspeedtest.view.JSpeedTestFragment
import com.sml.stp.feature.main.navigation.MainActivityNavigator
import com.sml.stp.feature.main.view.MainActivity
import com.sml.stp.feature.okhttp.di.OkHttpSpeedTestModule
import com.sml.stp.feature.okhttp.view.OkHttpSpeedTestFragment
import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector
import ru.terrakok.cicerone.Navigator

@Module
abstract class MainActivityModule {

    @Module
    companion object {
        @ActivityScope
        @Provides
        @JvmStatic
        fun provideNavigator(activity: MainActivity): Navigator =
                MainActivityNavigator(activity, activity.containerId)

        @ActivityScope
        @Provides
        @JvmStatic
        fun provideErrorMessageDelegate(activity: MainActivity): ErrorMessageDelegate =
                ErrorMessageDelegate(activity.weak())

        @ActivityScope
        @Provides
        @JvmStatic
        fun provideMessageDelegate(activity: MainActivity): MessageDelegate =
                MessageDelegate(activity.weak())

        @ActivityScope
        @Provides
        @JvmStatic
        fun provideUiDelegate(errorMessageDelegate: ErrorMessageDelegate, messageDelegate: MessageDelegate): UiDelegateManager =
                UiDelegateManager(errorMessageDelegate, messageDelegate)
    }

    @FragmentScope
    @ContributesAndroidInjector(modules = [ChooseSpeedTestModule::class])
    abstract fun provideChooseSpeedTestFragmentFactory(): ChooseSpeedTestFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = [JSpeedTestModule::class])
    abstract fun provideJSpeedTestFragmentFactory(): JSpeedTestFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = [OkHttpSpeedTestModule::class])
    abstract fun provideRetrofitSpeedTestFragmentFactory(): OkHttpSpeedTestFragment
}