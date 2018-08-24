package com.sml.stp.development

import com.sml.domain.development.Logger
import timber.log.Timber
import javax.inject.Inject

class LoggerTimber @Inject constructor() : Logger {

    override fun d(message: String, vararg args: Any) {
        Timber.d(message, args)
    }
}