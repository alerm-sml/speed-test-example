package com.sml.data.datasource.jspeedtest

import android.accounts.NetworkErrorException
import com.sml.data.datasource.base.SpeedTestListener
import com.sml.data.factory.SpeedTestSocketFactory
import com.sml.data.mapper.FileTransferModelMapper
import com.sml.domain.enums.FileTransferMarker
import fr.bmartel.speedtest.SpeedTestReport
import fr.bmartel.speedtest.SpeedTestSocket
import fr.bmartel.speedtest.inter.ISpeedTestListener
import fr.bmartel.speedtest.model.SpeedTestError
import javax.inject.Inject

class JSpeedTestApiDownload @Inject constructor(
        private val mapper: FileTransferModelMapper,
        private val speedTestSocketFactory: SpeedTestSocketFactory
) {

    private lateinit var speedTestSocket: SpeedTestSocket

    fun initDownloadSpeedtestSettings(speedtestListener: SpeedTestListener) {
        speedTestSocket = speedTestSocketFactory.build()
        speedTestSocket.socketTimeout = JSpeedTestWayConst.SOCKET_TIMEOUT

        speedTestSocket.addSpeedTestListener(object : ISpeedTestListener {

            override fun onCompletion(report: SpeedTestReport) {
                speedTestSocket.removeSpeedTestListener(this)
                speedtestListener.onNext(mapper.map(report, false, FileTransferMarker.NONE))
                speedtestListener.onComplete()
            }

            override fun onProgress(percent: Float, report: SpeedTestReport) {
                speedtestListener.onNext(mapper.map(report, false, FileTransferMarker.DOWNLOAD))
            }

            override fun onError(speedTestError: SpeedTestError, errorMessage: String) {
                speedtestListener.onError(NetworkErrorException("Speed test error"))
            }

        })
    }

    fun runDownloadSpeedtest(url: String) {
        speedTestSocket.startFixedDownload(url, JSpeedTestWayConst.FIXED_TIME, JSpeedTestWayConst.REPORT_INTERVAL)
    }

    fun stopTask() {
        speedTestSocket.forceStopTask()
        speedTestSocket.clearListeners()
    }
}