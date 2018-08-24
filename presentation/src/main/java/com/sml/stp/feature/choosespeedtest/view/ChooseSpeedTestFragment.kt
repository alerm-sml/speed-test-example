package com.sml.stp.feature.choosespeedtest.view

import android.os.Bundle
import android.view.View
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.sml.stp.R
import com.sml.stp.basespecial.BaseTitledFragment
import kotlinx.android.synthetic.main.fragment_choose_speedtest.*
import javax.inject.Inject

class ChooseSpeedTestFragment
    : BaseTitledFragment(), ChooseSpeedTestView {

    @Inject
    @InjectPresenter
    lateinit var presenter: ChooseSpeedTestPresenter

    @ProvidePresenter
    fun providePresenter() = presenter

    override fun getLayoutId(): Int = R.layout.fragment_choose_speedtest

    override fun getActionBarTitleId(): Int = R.string.text_toolbar_choose_fragment

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
    }

    private fun initUi() {
        bertrandmartelButton.setOnClickListener {
            presenter.goToJSpeedTestScreen()
        }

        retrofitButton.setOnClickListener {
            presenter.goToRetrofitScreen()
        }
    }
}