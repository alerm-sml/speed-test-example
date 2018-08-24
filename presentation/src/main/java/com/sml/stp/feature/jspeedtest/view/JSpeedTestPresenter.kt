package com.sml.stp.feature.jspeedtest.view

import com.arellomobile.mvp.InjectViewState
import com.sml.domain.usecase.JSpeedTestUseCase
import com.sml.stp.base.BasePresenter
import com.sml.stp.mapper.SpeedtestViewMapper
import com.sml.stp.system.SchedulersProvider
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import javax.inject.Inject

@InjectViewState
class JSpeedTestPresenter @Inject constructor(
        private val schedulersProvider: SchedulersProvider,
        private val JSpeedTestUseCase: JSpeedTestUseCase,
        private val speedtestViewMapper: SpeedtestViewMapper
) : BasePresenter<JSpeedTestView>() {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
    }

    fun startSpeedTest() {
        JSpeedTestUseCase.execute()
                .subscribeOn(schedulersProvider.io())
                .observeOn(schedulersProvider.ui())
                .subscribeBy(
                        onNext = { viewState.showSpeed(speedtestViewMapper.map(it)) },
                        onError = { viewState.showError(it.localizedMessage) },
                        onComplete = { viewState.showMessage("End.") }
                )
                .addTo(disposables)
    }
}