package com.sml.stp.feature.main.view

import android.os.Bundle
import android.support.v4.app.FragmentManager
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.sml.stp.R
import com.sml.stp.base.BaseActivity
import kotlinx.android.synthetic.main.layout_toolbar.*
import ru.terrakok.cicerone.Navigator
import javax.inject.Inject

class MainActivity
    : BaseActivity(), MainActivityView, FragmentManager.OnBackStackChangedListener {

    @Inject
    @InjectPresenter
    lateinit var activityPresenter: MainActivityPresenter

    @ProvidePresenter
    fun providePresenter() = activityPresenter

    @Inject
    lateinit var navigator: Navigator

    override fun getLayoutId(): Int = R.layout.activity_main

    val containerId: Int
        get() = R.id.mainActivityFragmentContainer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initUi()
        shouldDisplayHomeUp()
    }

    private fun initUi() {
        setupActionBar()
    }

    private fun setupActionBar() {
        supportFragmentManager.addOnBackStackChangedListener(this)
        setSupportActionBar(toolbar)
        supportActionBar?.let {
            it.setHomeButtonEnabled(false)
            it.setDisplayHomeAsUpEnabled(true)
        }
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    override fun onResume() {
        super.onResume()
        navigatorHolder.setNavigator(navigator)
    }

    override fun onPause() {
        super.onPause()
        navigatorHolder.removeNavigator()
    }

    override fun onBackStackChanged() {
        shouldDisplayHomeUp()
    }

    private fun shouldDisplayHomeUp() {
        val canBack = supportFragmentManager.backStackEntryCount > 0
        supportActionBar?.setDisplayHomeAsUpEnabled(canBack)
    }
}
