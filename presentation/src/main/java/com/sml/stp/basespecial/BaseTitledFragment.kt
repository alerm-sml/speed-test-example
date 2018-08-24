package com.sml.stp.basespecial

import android.support.v7.app.AppCompatActivity
import com.sml.stp.base.BaseFragment

abstract class BaseTitledFragment : BaseFragment() {

    abstract fun getActionBarTitleId(): Int

    override fun onResume() {
        super.onResume()
        activity?.let {
            (it as AppCompatActivity).supportActionBar?.setTitle(getActionBarTitleId())
        }
    }
}