package com.sml.stp.feature.okhttp.view

import com.arellomobile.mvp.MvpView
import com.sml.stp.basespecial.CanShowError
import com.sml.stp.basespecial.CanShowMessage
import com.sml.stp.model.speedtest.SpeedTestViewModel

interface OkHttpSpeedTestView : MvpView, CanShowError, CanShowMessage {

    fun showSpeed(model: SpeedTestViewModel)
}