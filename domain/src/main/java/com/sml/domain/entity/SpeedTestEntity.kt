package com.sml.domain.entity

import com.sml.domain.enums.FileTransferMarker

data class SpeedTestEntity(
        val uid: Int = -1,
        var downloadSpeed: Float = 0.0F,
        var uploadSpeed: Float = 0.0F,
        var currentSpeed: Float = 0.0F,
        var speedTestMode: FileTransferMarker = FileTransferMarker.NONE,
        val downloadUrl: String = "",
        val uploadUrl: String = "",
        val hostName: String = ""
)