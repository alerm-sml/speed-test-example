package com.sml.stp.feature.main.view

import com.arellomobile.mvp.InjectViewState
import com.sml.stp.base.BasePresenter
import com.sml.stp.navigation.AppScreens
import ru.terrakok.cicerone.Router
import javax.inject.Inject

@InjectViewState
class MainActivityPresenter @Inject constructor(
        private val router: Router
) : BasePresenter<MainActivityView>() {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        goToChooseSpeedTestScreen()
    }

    private fun goToChooseSpeedTestScreen() {
        router.newRootScreen(AppScreens.CHOOSE_SPEEDTEST_SCREEN)
    }
}

