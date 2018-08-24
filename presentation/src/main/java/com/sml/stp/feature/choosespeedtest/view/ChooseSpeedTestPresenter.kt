package com.sml.stp.feature.choosespeedtest.view

import com.arellomobile.mvp.InjectViewState
import com.sml.stp.base.BasePresenter
import com.sml.stp.navigation.AppScreens
import ru.terrakok.cicerone.Router
import javax.inject.Inject

@InjectViewState
class ChooseSpeedTestPresenter @Inject constructor(
        private val router: Router
) : BasePresenter<ChooseSpeedTestView>() {

    fun goToJSpeedTestScreen() {
        router.navigateTo(AppScreens.SPEEDTEST_JSPEEDTEST_SCREEN)
    }

    fun goToRetrofitScreen() {
        router.navigateTo(AppScreens.SPEEDTEST_RETROFIT_SCREEN)
    }
}