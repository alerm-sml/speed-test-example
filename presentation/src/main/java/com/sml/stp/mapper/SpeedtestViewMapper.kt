package com.sml.stp.mapper

import com.sml.domain.entity.SpeedTestEntity
import com.sml.stp.format.SpeedTestFormat
import com.sml.stp.model.speedtest.SpeedTestViewModel
import javax.inject.Inject

class SpeedtestViewMapper @Inject constructor(
        private val speedTestFormat: SpeedTestFormat
) {

    fun map(entity: SpeedTestEntity): SpeedTestViewModel =
            SpeedTestViewModel(
                    latency = entity.latency,
                    currentSpeed = speedTestFormat.format(entity.currentSpeed),
                    downloadSpeed = speedTestFormat.format(entity.downloadSpeed),
                    uploadSpeed = speedTestFormat.format(entity.uploadSpeed),
                    speedTestMode = entity.speedTestMode
            )
}