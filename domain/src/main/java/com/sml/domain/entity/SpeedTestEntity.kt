package com.sml.domain.entity

import com.sml.domain.enums.FileTransferMarker
import com.sml.domain.helper.DomainConst

data class SpeedTestEntity(
        val uid: Int = -1,

        var latency: Long = DomainConst.MAX_LATENCY,

        var downloadSpeed: Float = 0.0F,
        var uploadSpeed: Float = 0.0F,
        var currentSpeed: Float = 0.0F,
        var speedTestMode: FileTransferMarker = FileTransferMarker.NONE,

        val downloadUrl: String = "",
        val uploadUrl: String = "",
        val hostName: String = ""
)