package com.sml.data.mapper

import com.sml.data.model.FileTransferModel
import com.sml.data.tools.SpeedCalculator
import com.sml.domain.entity.SpeedTestEntity
import com.sml.domain.entity.SpeedTestHostEntity
import javax.inject.Inject

class SpeedTestEntityMapper @Inject constructor(
        private val speedCalculator: SpeedCalculator
) {

    fun mapHostToDomain(entity: SpeedTestHostEntity): SpeedTestEntity =
            SpeedTestEntity(
                    uid = entity.uid,
                    downloadUrl = entity.downloadUrl,
                    uploadUrl = entity.uploadUrl,
                    hostName = entity.hostName
            )

    fun mapToDomain(entity: SpeedTestEntity, model: FileTransferModel): SpeedTestEntity {
        val calculatedSpeed = speedCalculator.calculateSpeed(model.totalBytesRead, model.startTimeMillis)

        val (currentSpeed, downloadSpeed, uploadSpeed) = speedCalculator.setSpeedDependsFileTransferMarker(calculatedSpeed, model.fileTransferMarker)

        return entity.copy(
                downloadSpeed = downloadSpeed,
                uploadSpeed = uploadSpeed,
                currentSpeed = currentSpeed,
                speedTestMode = model.fileTransferMarker
        )
    }
}