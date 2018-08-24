package com.sml.stp.feature.main.navigation

import android.content.Context
import android.content.Intent
import android.support.annotation.IdRes
import android.support.v4.app.Fragment
import com.sml.stp.feature.choosespeedtest.view.ChooseSpeedTestFragment
import com.sml.stp.feature.jspeedtest.view.JSpeedTestFragment
import com.sml.stp.feature.main.view.MainActivity
import com.sml.stp.feature.okhttp.view.OkHttpSpeedTestFragment
import com.sml.stp.navigation.AppScreens
import ru.terrakok.cicerone.android.SupportAppNavigator

class MainActivityNavigator constructor(
        activity: MainActivity,
        @IdRes private val containerId: Int
) : SupportAppNavigator(activity, containerId) {

    override fun createActivityIntent(context: Context?, screenKey: String?, data: Any?): Intent? =
            null

    override fun createFragment(screenKey: String?, data: Any?): Fragment = when (screenKey) {
        AppScreens.CHOOSE_SPEEDTEST_SCREEN -> ChooseSpeedTestFragment()
        AppScreens.SPEEDTEST_RETROFIT_SCREEN -> OkHttpSpeedTestFragment()
        AppScreens.SPEEDTEST_JSPEEDTEST_SCREEN -> JSpeedTestFragment()
        else -> throw UnsupportedOperationException("MainActivityNavigator: Unknown screen key $screenKey")
    }
}