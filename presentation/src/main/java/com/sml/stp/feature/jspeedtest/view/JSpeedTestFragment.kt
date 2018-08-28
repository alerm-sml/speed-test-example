package com.sml.stp.feature.jspeedtest.view

import android.os.Bundle
import android.view.View
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.sml.stp.R
import com.sml.stp.basespecial.BaseTitledFragment
import com.sml.stp.model.speedtest.SpeedTestViewModel
import kotlinx.android.synthetic.main.fragment_jspeedtest.*
import kotlinx.android.synthetic.main.layout_download_speed_info.*
import kotlinx.android.synthetic.main.layout_upload_speed_info.*
import timber.log.Timber
import javax.inject.Inject

class JSpeedTestFragment : BaseTitledFragment(), JSpeedTestView {

    @Inject
    @InjectPresenter
    lateinit var presenter: JSpeedTestPresenter

    @ProvidePresenter
    fun providePresenter() = presenter

    override fun getLayoutId(): Int = R.layout.fragment_jspeedtest

    override fun getActionBarTitleId(): Int = R.string.text_toolbar_jspeedtest_fragment

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
    }

    private fun initUi() {
        stButtonStart.setOnClickListener {
            Timber.d("JSpeedTest start speed test")
            presenter.startSpeedTest()
        }
    }

    override fun showSpeed(model: SpeedTestViewModel) {
        speed_test_progressbar.setArrowAngleBySpeed(model.currentSpeed)
        downloadSpeed.text = model.downloadSpeed.toString()
        uploadSpeed.text = model.uploadSpeed.toString()
    }

    override fun showError(text: String) {
        Timber.d("Error: $text")
        uiDelegateManager.showError(text)
    }

    override fun showMessage(text: String) {
        uiDelegateManager.showMessage(text)
    }
}