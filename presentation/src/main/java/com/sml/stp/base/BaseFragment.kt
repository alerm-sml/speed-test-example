package com.sml.stp.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arellomobile.mvp.MvpAppCompatFragment
import com.sml.stp.delegate.UiDelegateManager
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

abstract class BaseFragment
    : MvpAppCompatFragment() {

//    @Inject
//    lateinit var errorMessageDelegate: ErrorMessageDelegate
//
//    @Inject
//    lateinit var messageDelegate: MessageDelegate

    @Inject
    lateinit var uiDelegateManager: UiDelegateManager

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(getLayoutId(), container, false)
    }

    abstract fun getLayoutId(): Int
}