package com.sml.stp.model.speedtest

import com.sml.domain.enums.FileTransferMarker

data class SpeedTestViewModel(
        val latency: Long,
        val currentSpeed: Float,
        val downloadSpeed: Float,
        val uploadSpeed: Float,
        val speedTestMode: FileTransferMarker
)