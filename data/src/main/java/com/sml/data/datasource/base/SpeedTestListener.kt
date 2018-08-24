package com.sml.data.datasource.base

import com.sml.data.model.FileTransferModel

interface SpeedTestListener {

    fun onComplete()

    fun onNext(model: FileTransferModel)

    fun onError(throwable: Throwable)
}