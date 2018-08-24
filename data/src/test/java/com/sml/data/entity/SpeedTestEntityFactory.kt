package com.sml.data.entity

import com.sml.domain.entity.SpeedTestEntity
import com.sml.domain.enums.FileTransferMarker

class SpeedTestEntityFactory {

    fun mockEntity(): SpeedTestEntity =
            SpeedTestEntity(
                    uid = 1,
                    latency = 111,
                    downloadSpeed = 10F,
                    uploadSpeed = 0F,
                    currentSpeed = 10F,
                    speedTestMode = FileTransferMarker.DOWNLOAD,
                    downloadUrl = "downloadUrl",
                    uploadUrl = "uploadUrl",
                    hostName = "hostName"
            )
}