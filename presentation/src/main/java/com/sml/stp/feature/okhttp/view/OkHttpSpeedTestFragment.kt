package com.sml.stp.feature.okhttp.view

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.sml.stp.R
import com.sml.stp.basespecial.BaseTitledFragment
import com.sml.stp.model.speedtest.SpeedTestViewModel
import kotlinx.android.synthetic.main.fragment_okhttp_speedtest.*
import kotlinx.android.synthetic.main.layout_download_speed_info.*
import kotlinx.android.synthetic.main.layout_upload_speed_info.*
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.RuntimePermissions
import timber.log.Timber
import javax.inject.Inject

@RuntimePermissions
class OkHttpSpeedTestFragment : BaseTitledFragment(), OkHttpSpeedTestView {

    @Inject
    @InjectPresenter
    lateinit var presenter: OkHttpSpeedTestPresenter

    @ProvidePresenter
    fun providePresenter() = presenter

    override fun getLayoutId(): Int = R.layout.fragment_okhttp_speedtest

    override fun getActionBarTitleId(): Int = R.string.text_toolbar_okhttp_fragment

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
    }

    private fun initUi() {
        stButtonStart.setOnClickListener {
            startSpeedTestWithPermissionCheck()
        }
    }

    @NeedsPermission(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    fun startSpeedTest() {
        Timber.d("OkHttp start speed test")
        presenter.startSpeedTest()
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

    @SuppressLint("NeedOnRequestPermissionsResult")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onRequestPermissionsResult(requestCode, grantResults)
    }
}