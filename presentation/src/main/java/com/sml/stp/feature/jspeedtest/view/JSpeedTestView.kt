package com.sml.stp.feature.jspeedtest.view

import com.arellomobile.mvp.MvpView
import com.sml.stp.basespecial.CanShowError
import com.sml.stp.basespecial.CanShowMessage
import com.sml.stp.model.speedtest.SpeedTestViewModel

interface JSpeedTestView : MvpView, CanShowError, CanShowMessage {

    fun showSpeed(model: SpeedTestViewModel)
}