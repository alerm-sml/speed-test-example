package com.sml.data.tools

import com.sml.domain.enums.FileTransferMarker
import javax.inject.Inject

class SpeedCalculator @Inject constructor(
        private val timeBenchmark: TimeBenchmark
) {

    fun setSpeedDependsFileTransferMarker(currentSpeed: Float, fileTransferMarker: FileTransferMarker): Triple<Float, Float, Float> =
            when (fileTransferMarker) {
                FileTransferMarker.NONE -> Triple(0F, 0F, 0F)
                FileTransferMarker.DOWNLOAD -> Triple(currentSpeed, currentSpeed, 0F)
                FileTransferMarker.UPLOAD -> Triple(currentSpeed, 0F, currentSpeed)
            }

    fun calculateSpeed(totalBytesRead: Long, startTimeMills: Long, currentTimeMills: Long = timeBenchmark.build()): Float {
        var diff = currentTimeMills - startTimeMills
        diff = if (diff == 0L) 0L else diff
        return totalBytesRead * 1000F / diff.toFloat() / 1024F / 1024F * 8F
    }
}