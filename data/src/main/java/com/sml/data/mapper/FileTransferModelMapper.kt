package com.sml.data.mapper

import com.sml.data.model.FileTransferModel
import com.sml.domain.enums.FileTransferMarker
import fr.bmartel.speedtest.SpeedTestReport
import javax.inject.Inject

class FileTransferModelMapper @Inject constructor() {

    fun map(report: SpeedTestReport, isDone: Boolean, fileTransferMarker: FileTransferMarker): FileTransferModel =
            FileTransferModel(
                    totalBytesRead = report.temporaryPacketSize,
                    contentLength = report.totalPacketSize,
                    isDone = isDone,
                    startTimeMillis = report.startTime,
                    fileTransferMarker = fileTransferMarker
            )
}