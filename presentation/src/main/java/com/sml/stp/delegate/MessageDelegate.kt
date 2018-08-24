package com.sml.stp.delegate

import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import com.sml.stp.R
import java.lang.ref.WeakReference

class MessageDelegate constructor(private val activity: WeakReference<AppCompatActivity>) {

    fun showMessage(error: String) {
        val snackbar = Snackbar.make(
                getActivity().findViewById(R.id.mainActivityFragmentContainer) ?: throwException(),
                error,
                Snackbar.LENGTH_SHORT
        )

        with(snackbar) {
            view.setBackgroundColor(ContextCompat.getColor(getActivity().applicationContext, R.color.colorSuccess))
            show()
        }
    }

    private fun getActivity(): AppCompatActivity =
            activity.get() ?: throwException()

    private fun throwException(): Nothing =
            throw  IllegalArgumentException("Weak ref on activity is empty")
}