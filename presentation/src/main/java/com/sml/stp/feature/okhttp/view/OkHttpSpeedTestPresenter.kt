package com.sml.stp.feature.okhttp.view

import com.arellomobile.mvp.InjectViewState
import com.sml.domain.usecase.SpeedTestRxUseCase
import com.sml.stp.base.BasePresenter
import com.sml.stp.mapper.SpeedtestViewMapper
import com.sml.stp.system.SchedulersProvider
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import javax.inject.Inject

@InjectViewState
class OkHttpSpeedTestPresenter @Inject constructor(
        private val schedulersProvider: SchedulersProvider,
        private val speedTestUseCase: SpeedTestRxUseCase,
        private val speedtestViewMapper: SpeedtestViewMapper
) : BasePresenter<OkHttpSpeedTestView>() {

    fun startSpeedTest() {
        speedTestUseCase.execute()
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